
package cl.ucn.app.service;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
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

class ReservaEstacionamientoServiceTest {

    private ReservaEstacionamientoRepository reservaRepository;
    private UsuarioRepository usuarioRepository;
    private VehiculoRepository vehiculoRepository;
    private EstacionamientoRepository estacionamientoRepository;

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

        service = new ReservaEstacionamientoService(
                reservaRepository,
                usuarioRepository,
                vehiculoRepository,
                estacionamientoRepository
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
        when(estacionamientoRepository.findById(estacionamientoId))
                .thenReturn(estacionamiento);

        when(reservaRepository.existsOverlappingReservation(
                estacionamientoId, inicio, fin
        )).thenReturn(false);
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

        verify(reservaRepository).save(captor.capture());

        ReservaEstacionamiento guardada = captor.getValue();

        assertEquals("PENDIENTE", guardada.getEstado());
        assertEquals(inicio, guardada.getFechaHoraInicio());
        assertEquals(fin, guardada.getFechaHoraFin());

        verify(reservaRepository).existsOverlappingReservation(
                estacionamientoId, inicio, fin
        );
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

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarReserva(
                        usuarioId, vehiculoId, estacionamientoId, inicio, fin
                )
        );

        verify(reservaRepository).findUsuarioConRol(usuarioId);
        verify(reservaRepository, never()).existsOverlappingReservation(
                any(), any(), any()
        );
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void rechazaVehiculoInexistente() {
        when(vehiculoRepository.findById(vehiculoId)).thenReturn(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarReserva(
                        usuarioId, vehiculoId, estacionamientoId, inicio, fin
                )
        );

        verify(reservaRepository).findUsuarioConRol(usuarioId);
        verify(reservaRepository, never()).existsOverlappingReservation(
                any(), any(), any()
        );
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void rechazaVehiculoDeOtroUsuario() {
        Usuario otroUsuario = mock(Usuario.class);
        when(otroUsuario.getId()).thenReturn(99L);

        when(vehiculo.getPropietario()).thenReturn(otroUsuario);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarReserva(
                        usuarioId, vehiculoId, estacionamientoId, inicio, fin
                )
        );

        verifyNoInteractions(estacionamientoRepository);
        verify(reservaRepository).findUsuarioConRol(usuarioId);
        verify(reservaRepository, never()).existsOverlappingReservation(
                any(), any(), any()
        );
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void rechazaEstacionamientoInexistente() {
        when(estacionamientoRepository.findById(estacionamientoId))
                .thenReturn(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarReserva(
                        usuarioId, vehiculoId, estacionamientoId, inicio, fin
                )
        );

        verify(reservaRepository).findUsuarioConRol(usuarioId);
        verify(reservaRepository, never()).existsOverlappingReservation(
                any(), any(), any()
        );
        verify(reservaRepository, never()).save(any());
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

        verify(reservaRepository, never()).save(any());
        verify(reservaRepository, never()).existsOverlappingReservation(
                any(), any(), any()
        );
    }

    @Test
    void rechazaReservaCuandoExisteSolapamiento() {
        when(reservaRepository.existsOverlappingReservation(
                estacionamientoId, inicio, fin
        )).thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarReserva(
                        usuarioId, vehiculoId, estacionamientoId, inicio, fin
                )
        );

        verify(reservaRepository).existsOverlappingReservation(
                estacionamientoId, inicio, fin
        );

        verify(reservaRepository, never()).save(any());
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
        verify(reservaRepository).save(any());
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

        verify(reservaRepository, never()).save(any());
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
        verify(reservaRepository).save(any());
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
        verify(reservaRepository).save(any());
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

        verify(reservaRepository, never()).save(any());
        }
}
