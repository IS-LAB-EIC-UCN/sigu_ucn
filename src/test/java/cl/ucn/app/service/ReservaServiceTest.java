package cl.ucn.app.service;

import cl.ucn.app.model.EstadoReserva;
import cl.ucn.app.model.Reserva;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    private static final Long USUARIO_ID = 3L;
    private static final Long OTRO_USUARIO_ID = 7L;
    private static final Long RESERVA_ID = 10L;
    private static final Long SALA_ID = 1L;

    @Mock
    private ReservaRepository reservaRepository;
    private ReservaService servicio;

    @BeforeEach
    void preparar() {
        servicio = new ReservaService(reservaRepository, Clock.systemDefaultZone());
    }
    private Usuario usuarioConId(Long id) {
        return new Usuario() {
            @Override
            public Long getId() {
                return id;
            }
        };
    }
    // Reserva ya no tiene setEstado: se crea PENDIENTE y se lleva al estado pedido con sus transiciones.
    private Reserva reserva(Long propietarioId, String estado) {
        Reserva r = new Reserva(LocalDate.now().plusDays(30), LocalTime.NOON, LocalTime.NOON.plusHours(1),
                "Motivo de prueba", usuarioConId(propietarioId), null);
        switch (EstadoReserva.valueOf(estado)) {
            case APROBADA -> r.aprobar();
            case RECHAZADA -> r.rechazar("Motivo de prueba");
            case CANCELADA -> r.cancelar(LocalDateTime.now());
            default -> { }
        }
        return r;
    }

    @Test
    void cancelarReservaPendiente() {
        assertTrue(servicio.puedeCancelarse(reserva(USUARIO_ID, "PENDIENTE")));
    }

    @Test
    void cancelarReservaAprobada() {
        assertTrue(servicio.puedeCancelarse(reserva(USUARIO_ID, "APROBADA")));
    }

    @Test
    void cancelarReservaRechazada() {
        assertFalse(servicio.puedeCancelarse(reserva(USUARIO_ID, "RECHAZADA")));
    }

    @Test
    void cancelarReservaYaCancelada() {
        assertFalse(servicio.puedeCancelarse(reserva(USUARIO_ID, "CANCELADA")));
    }

    @Test
    void cancelarReservaPropiaPendienteCambiaElEstadoACancelada() {
        when(reservaRepository.buscarPorId(RESERVA_ID)).thenReturn(reserva(USUARIO_ID, "PENDIENTE"));
        servicio.cancelar(RESERVA_ID, USUARIO_ID);
        verify(reservaRepository).actualizar(argThat(r -> r.getEstado() == EstadoReserva.CANCELADA));
    }

    @Test
    void cancelarReservaPropiaAprobadaCambiaElEstadoACancelada() {
        when(reservaRepository.buscarPorId(RESERVA_ID)).thenReturn(reserva(USUARIO_ID, "APROBADA"));
        servicio.cancelar(RESERVA_ID, USUARIO_ID);
        verify(reservaRepository).actualizar(argThat(r -> r.getEstado() == EstadoReserva.CANCELADA));
    }

    @Test
    void cancelarReservaInexistenteLanzaErrorYNoModificaNada() {
        assertThrows(ReservaException.class, () -> servicio.cancelar(99L, USUARIO_ID));
        verify(reservaRepository, never()).actualizar(any());
    }

    @Test
    void cancelarReservaDeOtroUsuarioLanzaErrorYNoModificaNada() {
        when(reservaRepository.buscarPorId(RESERVA_ID)).thenReturn(reserva(OTRO_USUARIO_ID, "PENDIENTE"));
        assertThrows(ReservaException.class, () -> servicio.cancelar(RESERVA_ID, USUARIO_ID));
        verify(reservaRepository, never()).actualizar(any());
    }

    @Test
    void cancelarReservaRechazadaLanzaErrorYNoModificaNada() {
        when(reservaRepository.buscarPorId(RESERVA_ID)).thenReturn(reserva(USUARIO_ID, "RECHAZADA"));
        assertThrows(ReservaException.class, () -> servicio.cancelar(RESERVA_ID, USUARIO_ID));
        verify(reservaRepository, never()).actualizar(any());
    }

    @Test
    void cancelarReservaYaCanceladaLanzaErrorYNoModificaNada() {
        when(reservaRepository.buscarPorId(RESERVA_ID)).thenReturn(reserva(USUARIO_ID, "CANCELADA"));
        assertThrows(ReservaException.class, () -> servicio.cancelar(RESERVA_ID, USUARIO_ID));
        verify(reservaRepository, never()).actualizar(any());
    }



    @Test
    void filtrarSoloPorSala() {
        List<Reserva> esperadas = List.of(reserva(USUARIO_ID, "PENDIENTE"));
        when(reservaRepository.buscarPorUsuario(USUARIO_ID, SALA_ID, null, null)).thenReturn(esperadas);
        assertEquals(esperadas, servicio.listarMisReservas(USUARIO_ID, SALA_ID, null, null));
    }

    @Test
    void filtrarSoloPorEstado() {
        List<Reserva> esperadas = List.of(reserva(USUARIO_ID, "CANCELADA"));
        when(reservaRepository.buscarPorUsuario(USUARIO_ID, null, null, "CANCELADA")).thenReturn(esperadas);
        assertEquals(esperadas, servicio.listarMisReservas(USUARIO_ID, null, null, "CANCELADA"));
    }

    @Test
    void filtrarPorSalaYEstadoJuntos() {
        List<Reserva> esperadas = List.of(reserva(USUARIO_ID, "APROBADA"));
        when(reservaRepository.buscarPorUsuario(USUARIO_ID, SALA_ID, null, "APROBADA")).thenReturn(esperadas);
        assertEquals(esperadas, servicio.listarMisReservas(USUARIO_ID, SALA_ID, null, "APROBADA"));
    }

    @Test
    void sinFiltrosConsultaSoloPorElUsuario() {
        List<Reserva> esperadas = List.of(reserva(USUARIO_ID, "PENDIENTE"));
        when(reservaRepository.buscarPorUsuario(USUARIO_ID, null, null, null)).thenReturn(esperadas);
        assertEquals(esperadas, servicio.listarMisReservas(USUARIO_ID, null, null, null));
    }
}
