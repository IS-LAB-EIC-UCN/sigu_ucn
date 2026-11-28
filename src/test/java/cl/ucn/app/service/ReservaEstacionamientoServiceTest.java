
package cl.ucn.app.service;

import java.time.LocalDateTime;
import java.time.Clock;
import java.time.ZoneId;
import java.util.List;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import cl.ucn.app.model.Estacionamiento;
import cl.ucn.app.model.ReservaEstacionamiento;
import cl.ucn.app.model.Rol;
import cl.ucn.app.model.TipoEstacionamiento;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.model.Vehiculo;
import cl.ucn.app.repository.EstacionamientoRepository;
import cl.ucn.app.repository.ReservaEstacionamientoRepository;
import cl.ucn.app.repository.UsuarioRepository;
import cl.ucn.app.repository.VehiculoRepository;
import cl.ucn.app.service.ReservaEstacionamientoService.AccesoDenegadoException;
import jakarta.persistence.EntityManager;

class ReservaEstacionamientoServiceTest {

    private ReservaEstacionamientoRepository reservaRepository;
    private UsuarioRepository usuarioRepository;
    private VehiculoRepository vehiculoRepository;
    private EstacionamientoRepository estacionamientoRepository;
    private EntityManager em;

    private ReservaEstacionamientoService service;

    private Usuario usuario;
    private Vehiculo vehiculo;
    private Estacionamiento estacionamiento;

    private final Long usuarioId = 1L;
    private final Long vehiculoId = 2L;
    private final Long estacionamientoId = 3L;

    private final LocalDateTime inicio =
            LocalDateTime.of(2026, 11, 25, 10, 0);

    private final LocalDateTime fin =
            LocalDateTime.of(2026, 11, 25, 12, 0);

    @BeforeEach
    void setUp() {
        reservaRepository = mock(ReservaEstacionamientoRepository.class);
        usuarioRepository = mock(UsuarioRepository.class);
        vehiculoRepository = mock(VehiculoRepository.class);
        estacionamientoRepository = mock(EstacionamientoRepository.class);
        em = mock(EntityManager.class);

        service = new ReservaEstacionamientoService(
                reservaRepository,
                usuarioRepository,
                vehiculoRepository,
                estacionamientoRepository,
                Clock.fixed(inicio.atZone(ZoneId.of("America/Santiago")).toInstant(),
                        ZoneId.of("America/Santiago"))
        );

        usuario = mock(Usuario.class);
        vehiculo = mock(Vehiculo.class);
        estacionamiento = mock(Estacionamiento.class);

        when(usuario.getId()).thenReturn(usuarioId);

        when(vehiculo.getId()).thenReturn(vehiculoId);
        when(vehiculo.getPropietario()).thenReturn(usuario);

        when(estacionamiento.getId()).thenReturn(estacionamientoId);
        when(estacionamiento.getHabilitado()).thenReturn(true);

        when(reservaRepository.findUsuarioConRol(usuarioId)).thenReturn(usuario);
        when(vehiculoRepository.findById(vehiculoId)).thenReturn(vehiculo);
        when(estacionamientoRepository.conEstacionamientoBloqueado(eq(estacionamientoId), any()))
                .thenAnswer(invocacion -> {
                    BiFunction<EntityManager, Estacionamiento, ReservaEstacionamiento> operacion =
                            invocacion.getArgument(1);
                    return operacion.apply(em, estacionamiento);
                });
    }

    private void configurarRol(String nombreRol) {
        Rol rol = mock(Rol.class);
        when(rol.getNombre()).thenReturn(nombreRol);
        when(usuario.getRol()).thenReturn(rol);
        }

    @Test
    void registraReservaCorrectamente() {
        ReservaEstacionamiento resultado = service.registrarReserva(
                usuarioId, vehiculoId, estacionamientoId, inicio, fin
        );

        assertNotNull(resultado);

        ArgumentCaptor<ReservaEstacionamiento> captor =
                ArgumentCaptor.forClass(ReservaEstacionamiento.class);

        verify(reservaRepository).save(eq(em), captor.capture());

        ReservaEstacionamiento guardada = captor.getValue();

        assertEquals("PENDIENTE", guardada.getEstado());
        assertEquals(inicio, guardada.getFechaHoraInicio());
        assertEquals(fin, guardada.getFechaHoraFin());

        verify(reservaRepository).existsOverlappingReservation(em, estacionamientoId, inicio, fin);
    }

    @ParameterizedTest
    @ValueSource(strings = {"PENDIENTE", "APROBADA"})
    void cancelaLaReservaPropiaYConservaLosDemasDatos(String estado) {
        when(usuario.getActivo()).thenReturn(true);
        var reserva = reservaConBloqueo(estado, inicio.plusMinutes(1), fin, usuario);

        service.cancelarPropia(usuarioId, 10L);

        assertEquals("CANCELADA", reserva.getEstado());
        assertEquals(inicio.plusMinutes(1), reserva.getFechaHoraInicio());
        assertEquals(fin, reserva.getFechaHoraFin());
        assertEquals(usuario, reserva.getUsuario());
        assertEquals(vehiculo, reserva.getVehiculo());
        assertEquals(estacionamiento, reserva.getEstacionamiento());
        var orden = inOrder(reservaRepository, estacionamientoRepository);
        orden.verify(reservaRepository).findById(10L);
        orden.verify(estacionamientoRepository).conEstacionamientoBloqueado(eq(estacionamientoId), any());
        orden.verify(reservaRepository).findByIdBloqueada(em, 10L);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void administradorActivoCancelaReservasPropiasYAjenas(boolean propia) {
        configurarRol("ADMIN");
        when(usuario.getActivo()).thenReturn(true);
        Usuario propietario = propia ? usuario : mock(Usuario.class);
        var reserva = reservaConBloqueo("APROBADA", inicio.plusMinutes(1), fin, propietario);

        service.cancelarComoAdministrador(usuarioId, 10L);

        assertEquals("CANCELADA", reserva.getEstado());
    }

    @Test
    void laRutaPersonalRechazaReservasAjenasConElMismoAvisoQueUnIdInexistente() {
        when(usuario.getActivo()).thenReturn(true);
        Usuario otroUsuario = mock(Usuario.class);
        when(otroUsuario.getId()).thenReturn(20L);
        var reserva = reservaConBloqueo("APROBADA", inicio.plusMinutes(1), fin, otroUsuario);

        var ajena = assertThrows(IllegalArgumentException.class, () -> service.cancelarPropia(usuarioId, 10L));
        var inexistente = assertThrows(IllegalArgumentException.class, () -> service.cancelarPropia(usuarioId, 99L));

        assertEquals(inexistente.getMessage(), ajena.getMessage());
        assertEquals("APROBADA", reserva.getEstado());
        verifyNoInteractions(estacionamientoRepository);
    }

    @ParameterizedTest
    @CsvSource({"PENDIENTE,-1,120", "APROBADA,0,120", "PENDIENTE,-5,-1", "CANCELADA,1,120", "OTRO,1,120"})
    void rechazaEstadoYHoraSobreLaFilaBloqueada(String estado, int inicioMinutos, int finMinutos) {
        when(usuario.getActivo()).thenReturn(true);
        configurarRol("ADMIN");
        var reserva = reservaConBloqueo(estado, inicio.plusMinutes(inicioMinutos), inicio.plusMinutes(finMinutos),
                usuario);
        when(reservaRepository.findById(10L)).thenReturn(new ReservaEstacionamiento(
                inicio.plusMinutes(1), fin, "APROBADA", usuario, vehiculo, estacionamiento));

        assertThrows(IllegalArgumentException.class, () -> service.cancelarPropia(usuarioId, 10L));
        assertThrows(IllegalArgumentException.class, () -> service.cancelarComoAdministrador(usuarioId, 10L));
        assertEquals(estado, reserva.getEstado());
    }

    @Test
    void compruebaPermisosAntesDeConsultarReservas() {
        when(usuario.getActivo()).thenReturn(false);
        configurarRol("ADMIN");

        assertThrows(AccesoDenegadoException.class, () -> service.listarPropias(usuarioId));
        assertThrows(AccesoDenegadoException.class, () -> service.cancelarPropia(usuarioId, 10L));
        assertThrows(AccesoDenegadoException.class, () -> service.listarParaAdministracion(usuarioId));
        assertThrows(AccesoDenegadoException.class, () -> service.cancelarComoAdministrador(usuarioId, 10L));
        assertThrows(AccesoDenegadoException.class, () -> service.cancelarPropia(null, 10L));
        when(usuario.getActivo()).thenReturn(true);
        configurarRol("DOCENTE");
        assertThrows(AccesoDenegadoException.class, () -> service.listarParaAdministracion(usuarioId));
        assertThrows(AccesoDenegadoException.class, () -> service.cancelarComoAdministrador(usuarioId, 10L));
        verify(reservaRepository, never()).findById(any());
        verify(reservaRepository, never()).findByUsuarioId(any());
        verify(reservaRepository, never()).findAllConDetalle();
    }

    @Test
    void rechazaIdNuloCeroNegativoOInexistente() {
        when(usuario.getActivo()).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.cancelarPropia(usuarioId, null));
        assertThrows(IllegalArgumentException.class, () -> service.cancelarPropia(usuarioId, 0L));
        assertThrows(IllegalArgumentException.class, () -> service.cancelarPropia(usuarioId, -1L));
        assertThrows(IllegalArgumentException.class, () -> service.cancelarPropia(usuarioId, 99L));

        verify(reservaRepository).findById(99L);
        verifyNoInteractions(estacionamientoRepository);
    }

    @Test
    void muestraVencidaDesdeElFinYConservaCancelada() {
        when(usuario.getActivo()).thenReturn(true);
        var vigente = new ReservaEstacionamiento(inicio.plusMinutes(1), fin, "PENDIENTE", usuario, vehiculo, estacionamiento);
        var empezada = new ReservaEstacionamiento(inicio, fin, "APROBADA", usuario, vehiculo, estacionamiento);
        var vencida = new ReservaEstacionamiento(inicio.minusHours(1), inicio, "APROBADA", usuario, vehiculo, estacionamiento);
        var cancelada = new ReservaEstacionamiento(inicio.minusHours(2), inicio.minusHours(1), "CANCELADA", usuario, vehiculo, estacionamiento);
        when(reservaRepository.findByUsuarioId(usuarioId)).thenReturn(List.of(vigente, empezada, vencida, cancelada));

        var propias = service.listarPropias(usuarioId);

        assertEquals(List.of("PENDIENTE", "APROBADA", "VENCIDA", "CANCELADA"),
                propias.stream().map(ReservaEstacionamientoService.ResumenReserva::estado).toList());
        assertTrue(propias.getFirst().puedeCancelar());
        assertFalse(propias.stream().skip(1).anyMatch(ReservaEstacionamientoService.ResumenReserva::puedeCancelar));
        assertEquals("APROBADA", vencida.getEstado());
    }

    private ReservaEstacionamiento reservaConBloqueo(String estado, LocalDateTime desde, LocalDateTime hasta,
            Usuario propietario) {
        var reserva = new ReservaEstacionamiento(desde, hasta, estado, propietario, vehiculo, estacionamiento);
        when(reservaRepository.findById(10L)).thenReturn(reserva);
        when(reservaRepository.findByIdBloqueada(em, 10L)).thenReturn(reserva);
        return reserva;
    }

    @Test
    void rechazaIdentificadoresNulos() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarReserva(
                        null, vehiculoId, estacionamientoId, inicio, fin
                )
        );

        verifyNoInteractions(usuarioRepository);
        verifyNoInteractions(reservaRepository);
    }

    @Test
    void rechazaIntervaloConFechasNulas() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarReserva(
                        usuarioId, vehiculoId, estacionamientoId, null, fin
                )
        );

        verifyNoInteractions(usuarioRepository);
        verifyNoInteractions(reservaRepository);
    }

    @Test
    void rechazaFechaFinalAnteriorOIgualAlInicio() {
        LocalDateTime fechaInvalida = inicio;

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarReserva(
                        usuarioId, vehiculoId, estacionamientoId,
                        inicio, fechaInvalida
                )
        );

        verifyNoInteractions(usuarioRepository);
        verifyNoInteractions(reservaRepository);
    }

    @Test
    void rechazaUsuarioInexistente() {
        when(reservaRepository.findUsuarioConRol(usuarioId)).thenReturn(null);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarReserva(
                        usuarioId, vehiculoId, estacionamientoId, inicio, fin
                )
        );

        assertEquals("El usuario no existe.", error.getMessage());
        verify(reservaRepository).findUsuarioConRol(usuarioId);
        verify(reservaRepository, never()).existsOverlappingReservation(eq(em), any(), any(), any());
        verify(reservaRepository, never()).existsOverlappingReservationForVehicle(eq(em), any(), any(), any());
        verify(reservaRepository, never()).save(eq(em), any());
    }

    @Test
    void rechazaVehiculoInexistente() {
        when(vehiculoRepository.findById(vehiculoId)).thenReturn(null);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarReserva(
                        usuarioId, vehiculoId, estacionamientoId, inicio, fin
                )
        );

        assertEquals("El vehículo no existe.", error.getMessage());
        verify(reservaRepository).findUsuarioConRol(usuarioId);
        verify(reservaRepository, never()).existsOverlappingReservation(eq(em), any(), any(), any());
        verify(reservaRepository, never()).existsOverlappingReservationForVehicle(eq(em), any(), any(), any());
        verify(reservaRepository, never()).save(eq(em), any());
    }

    @Test
    void rechazaVehiculoDeOtroUsuario() {
        Usuario otroUsuario = mock(Usuario.class);
        when(otroUsuario.getId()).thenReturn(99L);

        when(vehiculo.getPropietario()).thenReturn(otroUsuario);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarReserva(
                        usuarioId, vehiculoId, estacionamientoId, inicio, fin
                )
        );

        assertEquals("El vehículo no pertenece al usuario.", error.getMessage());
        verifyNoInteractions(estacionamientoRepository);
        verify(reservaRepository).findUsuarioConRol(usuarioId);
        verify(reservaRepository, never()).existsOverlappingReservation(eq(em), any(), any(), any());
        verify(reservaRepository, never()).existsOverlappingReservationForVehicle(eq(em), any(), any(), any());
        verify(reservaRepository, never()).save(eq(em), any());
    }

    @Test
    void rechazaEstacionamientoInexistente() {
        estacionamiento = null;

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarReserva(
                        usuarioId, vehiculoId, estacionamientoId, inicio, fin
                )
        );

        assertEquals("El estacionamiento no existe.", error.getMessage());
        verify(reservaRepository).findUsuarioConRol(usuarioId);
        verify(reservaRepository, never()).existsOverlappingReservation(eq(em), any(), any(), any());
        verify(reservaRepository, never()).existsOverlappingReservationForVehicle(eq(em), any(), any(), any());
        verify(reservaRepository, never()).save(eq(em), any());
    }

    @Test
    void rechazaEstacionamientoDeshabilitado() {
        when(estacionamiento.getHabilitado()).thenReturn(false);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarReserva(
                        usuarioId, vehiculoId, estacionamientoId, inicio, fin
                )
        );

        verify(reservaRepository, never()).save(eq(em), any());
        verify(reservaRepository, never()).existsOverlappingReservation(eq(em), any(), any(), any());
    }

    @Test
    void rechazaReservaCuandoExisteSolapamiento() {
        when(reservaRepository.existsOverlappingReservation(em, estacionamientoId, inicio, fin)).thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarReserva(
                        usuarioId, vehiculoId, estacionamientoId, inicio, fin
                )
        );

        verify(reservaRepository).existsOverlappingReservation(em, estacionamientoId, inicio, fin);

        verify(reservaRepository, never()).save(eq(em), any());
    }

    @Test
        void estudiantePuedeReservarEstacionamientoComun() {
        configurarRol("ESTUDIANTE");
        when(estacionamiento.getTipo())
                .thenReturn(TipoEstacionamiento.COMUN);

        ReservaEstacionamiento resultado = service.registrarReserva(
                usuarioId, vehiculoId, estacionamientoId, inicio, fin
        );

        assertNotNull(resultado);
        verify(reservaRepository).save(eq(em), any());
        }

        @Test
        void estudianteNoPuedeReservarEstacionamientoDocentes() {
        configurarRol("ESTUDIANTE");
        when(estacionamiento.getTipo())
                .thenReturn(TipoEstacionamiento.DOCENTES);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarReserva(
                        usuarioId, vehiculoId, estacionamientoId, inicio, fin
                )
        );

        verify(reservaRepository, never()).save(eq(em), any());
        }

        @Test
        void docentePuedeReservarEstacionamientoComun() {
        configurarRol("DOCENTE");
        when(estacionamiento.getTipo())
                .thenReturn(TipoEstacionamiento.COMUN);

        ReservaEstacionamiento resultado = service.registrarReserva(
                usuarioId, vehiculoId, estacionamientoId, inicio, fin
        );

        assertNotNull(resultado);
        verify(reservaRepository).save(eq(em), any());
        }

        @Test
        void docentePuedeReservarEstacionamientoExclusivo() {
        configurarRol("DOCENTE");
        when(estacionamiento.getTipo())
                .thenReturn(TipoEstacionamiento.DOCENTES);

        ReservaEstacionamiento resultado = service.registrarReserva(
                usuarioId, vehiculoId, estacionamientoId, inicio, fin
        );

        assertNotNull(resultado);
        verify(reservaRepository).save(eq(em), any());
        }

        @Test
        void administradorNoPuedeReservarEstacionamientoDocentes() {
        configurarRol("ADMIN");
        when(estacionamiento.getTipo())
                .thenReturn(TipoEstacionamiento.DOCENTES);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarReserva(
                        usuarioId, vehiculoId, estacionamientoId, inicio, fin
                )
        );

        verify(reservaRepository, never()).save(eq(em), any());
        }

    @Test
    void rechazaInicioAnteriorAlMomentoActual() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> service.registrarReserva(usuarioId, vehiculoId, estacionamientoId,
                        inicio.minusNanos(1), fin));
        assertEquals("La fecha y hora de inicio no pueden estar en el pasado.", error.getMessage());
        verifyNoInteractions(reservaRepository);
    }

    @Test
    void permiteInicioExactamenteActual() {
        assertNotNull(service.registrarReserva(usuarioId, vehiculoId, estacionamientoId, inicio, fin));
    }

    @Test
    void permiteInicioFuturo() {
        assertNotNull(service.registrarReserva(usuarioId, vehiculoId, estacionamientoId,
                inicio.plusMinutes(1), fin));
    }

    @Test
    void rechazaFinAnteriorAlInicio() {
        assertThrows(IllegalArgumentException.class,
                () -> service.registrarReserva(usuarioId, vehiculoId, estacionamientoId,
                        inicio, inicio.minusMinutes(1)));
        verifyNoInteractions(reservaRepository);
    }

    @Test
    void rechazaVehiculoConReservaSuperpuestaEnOtroEspacio() {
        when(reservaRepository.existsOverlappingReservationForVehicle(em, vehiculoId, inicio, fin))
                .thenReturn(true);
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> service.registrarReserva(usuarioId, vehiculoId, estacionamientoId, inicio, fin));
        assertEquals("El vehículo ya tiene una reserva en ese horario.", error.getMessage());
        verify(reservaRepository, never()).save(eq(em), any());
    }

    @Test
    void permiteReservasConsecutivasSinCruceDelVehiculo() {
        assertNotNull(service.registrarReserva(usuarioId, vehiculoId, estacionamientoId, inicio, fin));
        assertNotNull(service.registrarReserva(usuarioId, vehiculoId, estacionamientoId,
                fin, fin.plusHours(1)));
        verify(reservaRepository).existsOverlappingReservationForVehicle(em, vehiculoId, fin, fin.plusHours(1));
    }
}
