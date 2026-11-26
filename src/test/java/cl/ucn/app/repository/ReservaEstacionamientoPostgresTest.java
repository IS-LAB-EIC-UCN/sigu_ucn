package cl.ucn.app.repository;

import cl.ucn.app.model.Estacionamiento;
import cl.ucn.app.model.ReservaEstacionamiento;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.model.Vehiculo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

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
                "jakarta.persistence.jdbc.url", url + "?currentSchema=" + schema + ",public",
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
}
