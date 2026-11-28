package cl.ucn.app.repository;

import cl.ucn.app.model.Estacionamiento;
import cl.ucn.app.model.ReservaEstacionamiento;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.model.Vehiculo;
import cl.ucn.app.service.EstacionamientoService;
import cl.ucn.app.service.ReservaEstacionamientoService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Requiere una base Docker aislada llamada sigu_hu02_test y las variables
 * SIGU_HU02_TEST_URL, SIGU_HU02_TEST_USER y SIGU_HU02_TEST_PASSWORD.
 * Ejecutar con mvn -Dtest=ReservaEstacionamientoPostgresTest test.
 * Cada caso usa y elimina exclusivamente su propio esquema aleatorio.
 */
@EnabledIfEnvironmentVariable(named = "SIGU_HU02_TEST_URL", matches = ".+")
class ReservaEstacionamientoPostgresTest {
    private static final Path MIGRATIONS = Path.of("db/migrations/grupo-9");
    private static final String MIGRATION = "005_add_reserva_exclusion_constraints.sql";
    private final LocalDateTime inicio = LocalDateTime.of(2030, 1, 1, 10, 0);
    private String url;
    private String user;
    private String password;
    private String schema;
    private Connection connection;
    private EntityManagerFactory factory;
    private ReservaEstacionamientoRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        url = System.getenv("SIGU_HU02_TEST_URL");
        user = System.getenv("SIGU_HU02_TEST_USER");
        password = System.getenv("SIGU_HU02_TEST_PASSWORD");
        connection = DriverManager.getConnection(url, user, password);
        try (var statement = connection.createStatement();
             var result = statement.executeQuery("SELECT current_database()")) {
            assertTrue(result.next());
            assertEquals("sigu_hu02_test", result.getString(1), "Se requiere la base aislada de pruebas.");
        }
        execute("CREATE EXTENSION IF NOT EXISTS btree_gist WITH SCHEMA public");
        schema = "hu02_" + UUID.randomUUID().toString().replace("-", "");
        execute("CREATE SCHEMA " + schema);
        execute("SET search_path TO " + schema + ", public");
        execute(Files.readString(Path.of("db/init/01_scheme.sql")));
        for (String file : List.of("001_create_vehiculos.sql", "002_create_estacionamientos.sql",
                "003_create_reservas_estacionamiento.sql", "004_add_tipo_estacionamiento.sql")) {
            execute(Files.readString(MIGRATIONS.resolve(file)));
        }
        execute("INSERT INTO roles(id,nombre) VALUES (1,'DOCENTE'); "
                + "INSERT INTO usuarios(id,nombre,correo,password,activo,rol_id) "
                + "VALUES (1,'Prueba','hu02@test.local','test',true,1); "
                + "INSERT INTO vehiculos(id,patente,usuario_id) VALUES (1,'ABCD12',1),(2,'EFGH34',1); "
                + "INSERT INTO estacionamientos(id,codigo,habilitado,tipo) "
                + "VALUES (1,'TEST-1',true,'COMUN'),(2,'TEST-2',true,'DOCENTES')");
        factory = Persistence.createEntityManagerFactory("siguPU", Map.of(
                "jakarta.persistence.jdbc.url", url + "?currentSchema=" + schema + ",public&ApplicationName=" + schema,
                "jakarta.persistence.jdbc.user", user,
                "jakarta.persistence.jdbc.password", password,
                "hibernate.default_schema", schema,
                "hibernate.show_sql", "false"));
        repository = new ReservaEstacionamientoRepository(factory::createEntityManager);
    }

    @AfterEach
    void tearDown() throws SQLException {
        try {
            if (factory != null) {
                factory.close();
            }
        } finally {
            if (connection != null) {
                try (Connection testConnection = connection) {
                    execute("ROLLBACK");
                    if (schema != null) {
                        execute("DROP SCHEMA " + schema + " CASCADE");
                    }
                }
            }
        }
    }

    private void execute(String sql) throws SQLException {
        try (var statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private void migrate() throws Exception {
        execute(Files.readString(MIGRATIONS.resolve(MIGRATION)));
    }

    private ReservaEstacionamiento reserva(long vehiculo, long espacio, LocalDateTime desde,
                                          LocalDateTime hasta, String estado) {
        try (EntityManager em = factory.createEntityManager()) {
            return new ReservaEstacionamiento(desde, hasta, estado,
                    em.find(Usuario.class, 1L), em.find(Vehiculo.class, vehiculo),
                    em.find(Estacionamiento.class, espacio));
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void soloUnaSolicitudConcurrenteConfirma(boolean mismoEspacio) throws Exception {
        migrate();
        ReservaEstacionamiento primera = reserva(1, 1, inicio, inicio.plusHours(2), "PENDIENTE");
        ReservaEstacionamiento segunda = reserva(mismoEspacio ? 2 : 1, mismoEspacio ? 1 : 2,
                inicio.plusMinutes(30), inicio.plusHours(1), "APROBADA");
        CyclicBarrier barrier = new CyclicBarrier(2);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Future<String> a = executor.submit(() -> intentarReserva(primera, barrier));
            Future<String> b = executor.submit(() -> intentarReserva(segunda, barrier));
            List<String> results = List.of(a.get(20, TimeUnit.SECONDS), b.get(20, TimeUnit.SECONDS));
            assertEquals(1, results.stream().filter("OK"::equals).count());
            String message = mismoEspacio ? "El estacionamiento ya está reservado en ese horario."
                    : "El vehículo ya tiene una reserva en ese horario.";
            assertTrue(results.contains(message) || results.contains(
                    "Otra solicitud coincidió con esta reserva. Revisa la disponibilidad e inténtalo nuevamente."));
            assertEquals(1, repository.findByUsuarioId(1L).size());
        } finally {
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
        }
    }

    private String intentarReserva(ReservaEstacionamiento reserva, CyclicBarrier barrier) throws Exception {
        assertFalse(repository.existsOverlappingReservation(reserva.getEstacionamiento().getId(),
                reserva.getFechaHoraInicio(), reserva.getFechaHoraFin()));
        assertFalse(repository.existsOverlappingReservationForVehicle(reserva.getVehiculo().getId(),
                reserva.getFechaHoraInicio(), reserva.getFechaHoraFin()));
        // Ambas solicitudes han superado la comprobación previa antes de intentar guardar.
        barrier.await(10, TimeUnit.SECONDS);
        try {
            repository.save(reserva);
            return "OK";
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getCause());
            return e.getMessage();
        }
    }

    @Test
    void permiteReservasConsecutivasYConsultaLosLimites() throws Exception {
        migrate();
        repository.save(reserva(1, 1, inicio, inicio.plusHours(1), "PENDIENTE"));
        assertFalse(repository.existsOverlappingReservation(1L, inicio.plusHours(1), inicio.plusHours(2)));
        assertFalse(repository.existsOverlappingReservationForVehicle(1L, inicio.plusHours(1), inicio.plusHours(2)));
        assertTrue(repository.existsOverlappingReservationForVehicle(1L, inicio.plusMinutes(30), inicio.plusHours(2)));
        repository.save(reserva(1, 1, inicio.plusHours(1), inicio.plusHours(2), "APROBADA"));
        assertEquals(2, repository.findByUsuarioId(1L).size());
    }

    @Test
    void migracionConservaReservasHistoricasCompatibles() throws Exception {
        LocalDateTime pasado = LocalDateTime.of(2020, 1, 1, 10, 0);
        ReservaEstacionamiento historica = reserva(1, 1, pasado, pasado.plusHours(1), "APROBADA");
        repository.save(historica);
        migrate();
        ReservaEstacionamiento guardada = repository.findById(historica.getId());
        assertEquals(pasado, guardada.getFechaHoraInicio());
        assertEquals(pasado.plusHours(1), guardada.getFechaHoraFin());
        assertEquals("APROBADA", guardada.getEstado());
        assertEquals(1, repository.findByUsuarioId(1L).size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"PENDIENTE", "APROBADA"})
    void ambosEstadosBloqueanEspacioYVehiculo(String estado) throws Exception {
        migrate();
        repository.save(reserva(1, 1, inicio, inicio.plusHours(1), estado));
        assertTrue(repository.existsOverlappingReservation(1L, inicio, inicio.plusHours(1)));
        assertTrue(repository.existsOverlappingReservationForVehicle(1L, inicio, inicio.plusHours(1)));
        assertThrows(IllegalArgumentException.class,
                () -> repository.save(reserva(2, 1, inicio, inicio.plusHours(1), "PENDIENTE")));
        assertThrows(IllegalArgumentException.class,
                () -> repository.save(reserva(1, 2, inicio, inicio.plusHours(1), "APROBADA")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"CANCELADA", "RECHAZADA"})
    void otrosEstadosNoBloquean(String estado) throws Exception {
        migrate();
        repository.save(reserva(1, 1, inicio, inicio.plusHours(1), estado));
        assertFalse(repository.existsOverlappingReservation(1L, inicio, inicio.plusHours(1)));
        assertFalse(repository.existsOverlappingReservationForVehicle(1L, inicio, inicio.plusHours(1)));
        repository.save(reserva(1, 1, inicio, inicio.plusHours(1), "PENDIENTE"));
        assertEquals(2, repository.findByUsuarioId(1L).size());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void migracionRechazaDatosIncompatiblesSinCambiarlos(boolean mismoEspacio) throws Exception {
        repository.save(reserva(1, 1, inicio, inicio.plusHours(1), "PENDIENTE"));
        repository.save(reserva(mismoEspacio ? 2 : 1, mismoEspacio ? 1 : 2,
                inicio, inicio.plusHours(1), "APROBADA"));
        SQLException error = assertThrows(SQLException.class, this::migrate);
        assertTrue(error.getMessage().contains("Reservas incompatibles"));
        execute("ROLLBACK");
        assertEquals(2, repository.findByUsuarioId(1L).size());
        try (var statement = connection.createStatement(); var rows = statement.executeQuery(
                "SELECT count(*) FROM pg_constraint WHERE conrelid = 'reservas_estacionamiento'::regclass "
                        + "AND contype = 'x'")) {
            assertTrue(rows.next());
            assertEquals(0, rows.getInt(1));
        }
    }

    @Nested
    class EstacionamientoAdminPostgresTest {
        private static final long ADMIN = 2L;
        private final LocalDateTime ahora = inicio.minusHours(1);
        private EstacionamientoRepository espacios;
        private EstacionamientoService admin;
        private Clock clock;

        @BeforeEach
        void prepararCatalogo() throws Exception {
            migrate();
            execute("INSERT INTO roles(id,nombre) VALUES (2,'ADMIN'); "
                    + "INSERT INTO usuarios(id,nombre,correo,password,activo,rol_id) "
                    + "VALUES (2,'Admin','admin-espacios@test.local','test',true,2); "
                    + "INSERT INTO vehiculos(id,patente,usuario_id) VALUES (3,'JKLM56',2); "
                    + "SELECT setval('estacionamientos_id_seq', 2)");
            ZoneId zona = ZoneId.of("America/Santiago");
            clock = Clock.fixed(ahora.atZone(zona).toInstant(), zona);
            espacios = new EstacionamientoRepository(factory::createEntityManager);
            admin = new EstacionamientoService(espacios, repository, clock);
        }

        @Test
        void persisteAmbosEstadosYPermiteAlAdminReservarUnComunHabilitado() {
            Estacionamiento comun = admin.registrar(ADMIN, " c01 ", "COMUN", "true");
            Estacionamiento docente = admin.registrar(ADMIN, " d01 ", "DOCENTES", "false");

            assertEquals(List.of("C01", "D01", "TEST-1", "TEST-2"),
                    admin.listar(ADMIN).stream().map(Estacionamiento::getCodigo).toList());
            assertTrue(espacios.findById(comun.getId()).getHabilitado());
            assertFalse(espacios.findById(docente.getId()).getHabilitado());
            assertThrows(EstacionamientoService.AccesoDenegadoException.class, () -> admin.listar(1L));
            assertEquals("PENDIENTE", servicioReservas(espacios)
                    .registrarReserva(ADMIN, 3L, comun.getId(), inicio, inicio.plusHours(1)).getEstado());

            admin.cambiarHabilitacion(ADMIN, docente.getId(), "true");
            assertTrue(espacios.findById(docente.getId()).getHabilitado());
            assertEquals(List.of("C01", "TEST-1"), servicioReservas(espacios).listarDisponiblesParaUsuario(ADMIN)
                    .stream().map(Estacionamiento::getCodigo).toList());
            assertThrows(IllegalArgumentException.class, () -> servicioReservas(espacios)
                    .registrarReserva(ADMIN, 3L, docente.getId(), inicio.plusHours(1), inicio.plusHours(2)));
        }

        @Test
        void dosAltasDelMismoCodigoNormalizadoSoloConfirmanUna() throws Exception {
            EstacionamientoRepository repositorioConBarrera = spy(espacios);
            CyclicBarrier barrera = new CyclicBarrier(2);
            doAnswer(invocacion -> {
                boolean existe = (boolean) invocacion.callRealMethod();
                barrera.await(10, TimeUnit.SECONDS);
                return existe;
            }).when(repositorioConBarrera).existsByCodigo("I02");
            var servicioConcurrente = new EstacionamientoService(repositorioConBarrera, repository, clock);

            ExecutorService executor = Executors.newFixedThreadPool(2);
            try {
                Future<String> primera = executor.submit(() -> intentarAlta(servicioConcurrente, " i02 "));
                Future<String> segunda = executor.submit(() -> intentarAlta(servicioConcurrente, "I02"));
                assertEquals(List.of("I02", "Ya existe un estacionamiento con ese código."),
                        List.of(primera.get(20, TimeUnit.SECONDS), segunda.get(20, TimeUnit.SECONDS))
                                .stream().sorted().toList());
                assertEquals(1, admin.listar(ADMIN).stream().filter(espacio -> "I02".equals(espacio.getCodigo())).count());
            } finally {
                executor.shutdownNow();
                assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
            }
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void reservaYDeshabilitacionCompitenPorElMismoBloqueo(boolean reservaPrimero) throws Exception {
            CountDownLatch cambioSinConfirmar = new CountDownLatch(1);
            CountDownLatch permitirConfirmacion = new CountDownLatch(1);
            EstacionamientoRepository primerRepositorio = spy(espacios);
            doAnswer(invocacion -> {
                BiFunction<EntityManager, Estacionamiento, Object> operacion = invocacion.getArgument(1);
                return espacios.conEstacionamientoBloqueado(1L, (em, espacio) -> {
                    Object resultado = operacion.apply(em, espacio);
                    cambioSinConfirmar.countDown();
                    esperar(permitirConfirmacion);
                    return resultado;
                });
            }).when(primerRepositorio).conEstacionamientoBloqueado(eq(1L), any());
            var primerAdmin = new EstacionamientoService(primerRepositorio, repository, clock);
            var primeraReserva = servicioReservas(primerRepositorio);

            ExecutorService executor = Executors.newFixedThreadPool(2);
            try {
                Future<?> primera = executor.submit(() -> {
                    if (reservaPrimero) {
                        primeraReserva.registrarReserva(1L, 1L, 1L, inicio, inicio.plusHours(1));
                    } else {
                        primerAdmin.cambiarHabilitacion(ADMIN, 1L, "false");
                    }
                });
                esperar(cambioSinConfirmar);
                Future<IllegalArgumentException> segunda = executor.submit(() ->
                        assertThrows(IllegalArgumentException.class, () -> {
                            if (reservaPrimero) {
                                admin.cambiarHabilitacion(ADMIN, 1L, "false");
                            } else {
                                servicioReservas(espacios).registrarReserva(1L, 1L, 1L, inicio, inicio.plusHours(1));
                            }
                        }));

                comprobarEsperaPorBloqueoEnPostgres();
                permitirConfirmacion.countDown();
                primera.get(20, TimeUnit.SECONDS);
                String error = segunda.get(20, TimeUnit.SECONDS).getMessage();
                assertEquals(reservaPrimero
                        ? "El estacionamiento tiene reservas pendientes o aprobadas que aún no terminan."
                        : "El estacionamiento no está habilitado.", error);
                assertEquals(reservaPrimero, espacios.findById(1L).getHabilitado());
                assertEquals(reservaPrimero ? 1 : 0, repository.findByUsuarioId(1L).size());
            } finally {
                permitirConfirmacion.countDown();
                executor.shutdownNow();
                assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
            }
        }

        @Test
        void consideraFinEstrictoYConservaLasReservasAlDeshabilitar() {
            ReservaEstacionamiento historica = reserva(1, 1, ahora.minusHours(1), ahora, "APROBADA");
            ReservaEstacionamiento cancelada = reserva(1, 1, inicio, inicio.plusHours(1), "CANCELADA");
            ReservaEstacionamiento aprobada = reserva(2, 2, ahora.minusHours(1), ahora.plusNanos(1000), "APROBADA");
            repository.save(historica);
            repository.save(cancelada);
            repository.save(aprobada);

            var adminSubmicrosegundo =
                    new EstacionamientoService(espacios, repository, Clock.offset(clock, Duration.ofNanos(999)));
            assertThrows(IllegalArgumentException.class,
                    () -> adminSubmicrosegundo.cambiarHabilitacion(ADMIN, 2L, "false"));
            admin.cambiarHabilitacion(ADMIN, 1L, "false");

            assertFalse(espacios.findById(1L).getHabilitado());
            assertTrue(espacios.findById(2L).getHabilitado());
            List<ReservaEstacionamiento> guardadas = repository.findByUsuarioId(1L);
            assertEquals(List.of(historica.getId(), cancelada.getId(), aprobada.getId()).stream().sorted().toList(),
                    guardadas.stream().map(ReservaEstacionamiento::getId).sorted().toList());
            assertEquals("APROBADA", repository.findById(historica.getId()).getEstado());
            assertEquals(ahora, repository.findById(historica.getId()).getFechaHoraFin());
            assertEquals("CANCELADA", repository.findById(cancelada.getId()).getEstado());
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void conservaLaFilaCanceladaYLiberaCadaExclusion(boolean mismoEspacio) {
            long nuevoVehiculo = mismoEspacio ? 2L : 1L;
            long nuevoEspacio = mismoEspacio ? 1L : 2L;
            var original = reserva(1, 1, inicio, inicio.plusHours(1), mismoEspacio ? "PENDIENTE" : "APROBADA");
            repository.save(original);
            var conflicto = assertThrows(IllegalArgumentException.class, () -> repository.save(
                    reserva(nuevoVehiculo, nuevoEspacio, inicio, inicio.plusHours(1), "PENDIENTE")));
            assertTrue(conflicto.getMessage().contains(mismoEspacio ? "estacionamiento" : "vehículo"));

            var servicio = servicioReservas(espacios);
            if (mismoEspacio) {
                servicio.cancelarPropia(1L, original.getId());
            } else {
                servicio.cancelarComoAdministrador(ADMIN, original.getId());
            }

            var persistida = repository.findById(original.getId());
            assertEquals(original.getId(), persistida.getId());
            assertEquals(original.getFechaHoraInicio(), persistida.getFechaHoraInicio());
            assertEquals(original.getFechaHoraFin(), persistida.getFechaHoraFin());
            assertEquals(1L, persistida.getUsuario().getId());
            assertEquals(1L, persistida.getEstacionamiento().getId());
            assertEquals(1L, persistida.getVehiculo().getId());
            assertEquals("CANCELADA", persistida.getEstado());
            var nueva = servicio.registrarReserva(1L, nuevoVehiculo, nuevoEspacio, inicio, inicio.plusHours(1));
            assertEquals("PENDIENTE", repository.findById(nueva.getId()).getEstado());
            assertEquals(2, repository.findByUsuarioId(1L).size());
        }

        @Test
        void listaElHistorialConLasRelacionesCargadasYSinActualizarVencidas() {
            var vencida = reserva(1, 1, ahora.minusHours(1), ahora, "APROBADA");
            var cancelada = reserva(1, 1, ahora.minusHours(1), ahora, "CANCELADA");
            repository.save(vencida);
            repository.save(cancelada);
            var servicio = servicioReservas(espacios);
            servicio.registrarReserva(ADMIN, 3L, 1L, inicio, inicio.plusHours(1));

            var propias = servicio.listarPropias(1L);
            assertEquals(2, propias.size());
            assertTrue(propias.stream().allMatch(r -> "TEST-1".equals(r.codigoEstacionamiento())
                    && "ABCD12".equals(r.patente()) && !r.puedeCancelar()));
            assertEquals(List.of("APROBADA", "CANCELADA"), repository.findByUsuarioId(1L).stream()
                    .map(ReservaEstacionamiento::getEstado).sorted().toList());
            var administradas = servicio.listarParaAdministracion(ADMIN);
            assertEquals(List.of("VENCIDA", "CANCELADA", "PENDIENTE"), administradas.stream()
                    .map(ReservaEstacionamientoService.ResumenReserva::estado).toList());
            assertEquals(List.of("Prueba", "Prueba", "Admin"), administradas.stream()
                    .map(ReservaEstacionamientoService.ResumenReserva::nombrePropietario).toList());
            assertEquals("admin-espacios@test.local", administradas.getLast().correoPropietario());
        }

        @Test
        void revierteLaCancelacionSiFallaAntesDelCommit() {
            var original = reserva(1, 1, inicio, inicio.plusHours(1), "APROBADA");
            repository.save(original);
            EstacionamientoRepository espaciosConFallo = spy(espacios);
            var fallo = new IllegalStateException("Fallo de prueba antes del commit");
            doAnswer(invocacion -> {
                BiFunction<EntityManager, Estacionamiento, Object> operacion = invocacion.getArgument(1);
                return espacios.conEstacionamientoBloqueado(1L, (em, espacio) -> {
                    operacion.apply(em, espacio);
                    em.flush();
                    assertEquals("CANCELADA", em.createQuery("SELECT r.estado FROM ReservaEstacionamiento r WHERE r.id = :id",
                            String.class).setParameter("id", original.getId()).getSingleResult());
                    throw fallo;
                });
            }).when(espaciosConFallo).conEstacionamientoBloqueado(eq(1L), any());

            assertSame(fallo, assertThrows(IllegalStateException.class,
                    () -> servicioReservas(espaciosConFallo).cancelarPropia(1L, original.getId())));
            assertEquals("APROBADA", repository.findById(original.getId()).getEstado());
            assertThrows(IllegalArgumentException.class, () -> servicioReservas(espacios)
                    .registrarReserva(1L, 2L, 1L, inicio, inicio.plusHours(1)));
        }

        @Test
        void dosCancelacionesSimultaneasSoloConfirmanUna() throws Exception {
            var original = reserva(1, 1, inicio, inicio.plusHours(1), "PENDIENTE");
            repository.save(original);
            CountDownLatch cambioSinConfirmar = new CountDownLatch(1);
            CountDownLatch permitirConfirmacion = new CountDownLatch(1);
            var primera = servicioReservas(pausarAntesDelCommit(cambioSinConfirmar, permitirConfirmacion));

            try (var executor = Executors.newFixedThreadPool(2)) {
                try {
                    Future<?> cancelaPropia = executor.submit(() -> primera.cancelarPropia(1L, original.getId()));
                    esperar(cambioSinConfirmar);
                    Future<IllegalArgumentException> cancelaAdmin = executor.submit(() ->
                            assertThrows(IllegalArgumentException.class, () -> servicioReservas(espacios)
                                    .cancelarComoAdministrador(ADMIN, original.getId())));
                    comprobarEsperaPorBloqueoEnPostgres();
                    permitirConfirmacion.countDown();
                    cancelaPropia.get(20, TimeUnit.SECONDS);
                    assertTrue(cancelaAdmin.get(20, TimeUnit.SECONDS).getMessage().contains("estado actual"));
                    assertEquals("CANCELADA", repository.findById(original.getId()).getEstado());
                } finally {
                    permitirConfirmacion.countDown();
                }
            }
        }

        @Test
        void cancelacionCreacionYDeshabilitacionConcurrentesConservanUnEstadoConsistente() throws Exception {
            var original = reserva(1, 1, inicio, inicio.plusHours(1), "APROBADA");
            repository.save(original);
            CountDownLatch cambioSinConfirmar = new CountDownLatch(1);
            CountDownLatch permitirConfirmacion = new CountDownLatch(1);
            var primera = servicioReservas(pausarAntesDelCommit(cambioSinConfirmar, permitirConfirmacion));

            try (var executor = Executors.newFixedThreadPool(3)) {
                try {
                    Future<?> cancelacion = executor.submit(() -> primera.cancelarPropia(1L, original.getId()));
                    esperar(cambioSinConfirmar);
                    Future<String> creacion = executor.submit(() -> {
                        try {
                            servicioReservas(espacios).registrarReserva(1L, 2L, 1L, inicio, inicio.plusHours(1));
                            return "creada";
                        } catch (IllegalArgumentException error) {
                            return error.getMessage();
                        }
                    });
                    Future<String> deshabilitacion = executor.submit(() -> {
                        try {
                            admin.cambiarHabilitacion(ADMIN, 1L, "false");
                            return "deshabilitado";
                        } catch (IllegalArgumentException error) {
                            return error.getMessage();
                        }
                    });
                    comprobarEsperaPorBloqueoEnPostgres(2);
                    permitirConfirmacion.countDown();
                    cancelacion.get(20, TimeUnit.SECONDS);
                    assertEquals("CANCELADA", repository.findById(original.getId()).getEstado());
                    String resultadoCreacion = creacion.get(20, TimeUnit.SECONDS);
                    String resultadoDeshabilitacion = deshabilitacion.get(20, TimeUnit.SECONDS);
                    boolean creada = "creada".equals(resultadoCreacion);
                    if (creada) {
                        assertTrue(resultadoDeshabilitacion.contains("reservas pendientes o aprobadas"));
                    } else {
                        assertTrue(resultadoCreacion.contains("no está habilitado"));
                        assertEquals("deshabilitado", resultadoDeshabilitacion);
                    }
                    assertEquals(creada, espacios.findById(1L).getHabilitado());
                    assertEquals(creada ? 2 : 1, repository.findByUsuarioId(1L).size());
                } finally {
                    permitirConfirmacion.countDown();
                }
            }
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void siOtraOperacionTomaPrimeroElEspacioAunVeLaReservaActiva(boolean intentaCrear) throws Exception {
            var original = reserva(1, 1, inicio, inicio.plusHours(1), "APROBADA");
            repository.save(original);
            CountDownLatch espacioBloqueado = new CountDownLatch(1);
            CountDownLatch verificarReservaActiva = new CountDownLatch(1);
            EstacionamientoRepository primerRepositorio = spy(espacios);
            doAnswer(invocacion -> {
                BiFunction<EntityManager, Estacionamiento, Object> operacion = invocacion.getArgument(1);
                return espacios.conEstacionamientoBloqueado(1L, (em, espacio) -> {
                    espacioBloqueado.countDown();
                    esperar(verificarReservaActiva);
                    return operacion.apply(em, espacio);
                });
            }).when(primerRepositorio).conEstacionamientoBloqueado(eq(1L), any());

            try (var executor = Executors.newFixedThreadPool(2)) {
                try {
                    Future<IllegalArgumentException> primera = executor.submit(() -> assertThrows(
                            IllegalArgumentException.class, () -> {
                                if (intentaCrear) {
                                    servicioReservas(primerRepositorio).registrarReserva(1L, 2L, 1L, inicio,
                                            inicio.plusHours(1));
                                } else {
                                    new EstacionamientoService(primerRepositorio, repository, clock)
                                            .cambiarHabilitacion(ADMIN, 1L, "false");
                                }
                            }));
                    esperar(espacioBloqueado);
                    Future<?> segunda = executor.submit(() -> servicioReservas(espacios).cancelarPropia(1L, original.getId()));
                    comprobarEsperaPorBloqueoEnPostgres();
                    verificarReservaActiva.countDown();
                    assertTrue(primera.get(20, TimeUnit.SECONDS).getMessage().contains(intentaCrear
                            ? "ya está reservado" : "reservas pendientes o aprobadas"));
                    segunda.get(20, TimeUnit.SECONDS);
                    assertEquals("CANCELADA", repository.findById(original.getId()).getEstado());
                    assertTrue(espacios.findById(1L).getHabilitado());
                    assertEquals(1, repository.findByUsuarioId(1L).size());
                } finally {
                    verificarReservaActiva.countDown();
                }
            }
        }

        @Test
        void reevaluarLaHoraTrasLaEsperaRechazaCancelarExactamenteAlInicio() throws Exception {
            var original = reserva(1, 1, inicio, inicio.plusHours(1), "PENDIENTE");
            repository.save(original);
            AtomicReference<Instant> instante = new AtomicReference<>(clock.instant());
            Clock relojControlado = mock(Clock.class);
            when(relojControlado.getZone()).thenReturn(clock.getZone());
            when(relojControlado.instant()).thenAnswer(invocacion -> instante.get());
            var cancelaciones = new ReservaEstacionamientoService(repository, new UsuarioRepository(),
                    new VehiculoRepository(factory::createEntityManager), espacios, relojControlado);
            CountDownLatch espacioBloqueado = new CountDownLatch(1);
            CountDownLatch liberarEspacio = new CountDownLatch(1);

            try (var executor = Executors.newFixedThreadPool(2)) {
                try {
                    Future<?> bloqueo = executor.submit(() -> espacios.conEstacionamientoBloqueado(1L, (em, espacio) -> {
                        espacioBloqueado.countDown();
                        esperar(liberarEspacio);
                        return null;
                    }));
                    esperar(espacioBloqueado);
                    Future<IllegalArgumentException> cancelacion = executor.submit(() -> assertThrows(
                            IllegalArgumentException.class, () -> cancelaciones.cancelarPropia(1L, original.getId())));
                    comprobarEsperaPorBloqueoEnPostgres();
                    instante.set(inicio.atZone(clock.getZone()).toInstant());
                    liberarEspacio.countDown();
                    bloqueo.get(20, TimeUnit.SECONDS);
                    assertTrue(cancelacion.get(20, TimeUnit.SECONDS).getMessage().contains("antes de que comience"));
                    assertEquals("PENDIENTE", repository.findById(original.getId()).getEstado());
                } finally {
                    liberarEspacio.countDown();
                }
            }
        }

        private EstacionamientoRepository pausarAntesDelCommit(CountDownLatch cambio, CountDownLatch confirmar) {
            EstacionamientoRepository repositorio = spy(espacios);
            doAnswer(invocacion -> {
                BiFunction<EntityManager, Estacionamiento, Object> operacion = invocacion.getArgument(1);
                return espacios.conEstacionamientoBloqueado(1L, (em, espacio) -> {
                    Object resultado = operacion.apply(em, espacio);
                    em.flush();
                    cambio.countDown();
                    esperar(confirmar);
                    return resultado;
                });
            }).when(repositorio).conEstacionamientoBloqueado(eq(1L), any());
            return repositorio;
        }

        private ReservaEstacionamientoService servicioReservas(EstacionamientoRepository repo) {
            return new ReservaEstacionamientoService(repository, new UsuarioRepository(),
                    new VehiculoRepository(factory::createEntityManager), repo, clock);
        }

        private String intentarAlta(EstacionamientoService servicio, String codigo) {
            try {
                return servicio.registrar(ADMIN, codigo, "COMUN", "true").getCodigo();
            } catch (IllegalArgumentException error) {
                assertNotNull(error.getCause(), "El conflicto debe provenir de la restricción de PostgreSQL.");
                return error.getMessage();
            }
        }

        private void esperar(CountDownLatch evento) {
            try {
                assertTrue(evento.await(10, TimeUnit.SECONDS), "No se alcanzó el punto de sincronización.");
            } catch (InterruptedException error) {
                Thread.currentThread().interrupt();
                fail(error);
            }
        }

        private void comprobarEsperaPorBloqueoEnPostgres() throws Exception {
            comprobarEsperaPorBloqueoEnPostgres(1);
        }

        private void comprobarEsperaPorBloqueoEnPostgres(int solicitudes) throws Exception {
            String consulta = "SELECT count(*) FROM pg_stat_activity WHERE application_name = ? "
                    + "AND wait_event_type = 'Lock' AND query ~* 'for (no key )?update'";
            long limite = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
            try (var statement = connection.prepareStatement(consulta)) {
                statement.setString(1, schema);
                do {
                    try (var resultados = statement.executeQuery()) {
                        if (resultados.next() && resultados.getInt(1) >= solicitudes) {
                            return;
                        }
                    }
                    Thread.sleep(20);
                } while (System.nanoTime() < limite);
            }
            fail("La segunda transacción no se observó esperando el bloqueo de PostgreSQL.");
        }
    }
}
