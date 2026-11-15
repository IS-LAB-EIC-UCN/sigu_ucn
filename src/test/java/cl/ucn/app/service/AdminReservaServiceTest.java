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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminReservaServiceTest {

    private static final Long RESERVA_ID = 10L;

    @Mock
    private ReservaRepository reservaRepository;
    private AdminReservaService servicio;

    @BeforeEach
    void preparar() {
        servicio = new AdminReservaService(reservaRepository);
    }

    private Reserva reservaEnEstado(EstadoReserva estado) {
        Reserva r = new Reserva(
                LocalDate.now().plusDays(15),
                LocalTime.NOON,
                LocalTime.NOON.plusHours(1),
                "Actividad académica",
                new Usuario(),
                null);
        switch (estado) {
            case APROBADA -> r.aprobar();
            case RECHAZADA -> r.rechazar("Motivo previo");
            case CANCELADA -> r.cancelar(LocalDateTime.now());
            case PENDIENTE -> { }
        }
        return r;
    }

    @Test
    void listarPendientesDelegaEnRepositorioYMantieneOrden() {
        Reserva primera = reservaEnEstado(EstadoReserva.PENDIENTE);
        Reserva segunda = reservaEnEstado(EstadoReserva.PENDIENTE);
        List<Reserva> esperadas = List.of(primera, segunda);
        when(reservaRepository.buscarPendientes()).thenReturn(esperadas);

        List<Reserva> resultado = servicio.listarPendientes();

        assertEquals(esperadas, resultado);
        verify(reservaRepository).buscarPendientes();
    }

    @Test
    void aprobarReservaPendienteCambiaEstadoAAprobadaYActualiza() {
        Reserva pendiente = reservaEnEstado(EstadoReserva.PENDIENTE);
        when(reservaRepository.buscarPorId(RESERVA_ID)).thenReturn(pendiente);

        Reserva resultado = servicio.aprobar(RESERVA_ID);

        assertEquals(EstadoReserva.APROBADA, resultado.getEstado());
        verify(reservaRepository).actualizar(argThat(r -> r.getEstado() == EstadoReserva.APROBADA));
    }

    @Test
    void aprobarReservaCanceladaLanzaExcepcionYNoActualiza() {
        when(reservaRepository.buscarPorId(RESERVA_ID)).thenReturn(reservaEnEstado(EstadoReserva.CANCELADA));

        assertThrows(ReservaException.class, () -> servicio.aprobar(RESERVA_ID));

        verify(reservaRepository, never()).actualizar(any());
    }

    @Test
    void aprobarReservaRechazadaLanzaExcepcionYNoActualiza() {
        when(reservaRepository.buscarPorId(RESERVA_ID)).thenReturn(reservaEnEstado(EstadoReserva.RECHAZADA));

        assertThrows(ReservaException.class, () -> servicio.aprobar(RESERVA_ID));

        verify(reservaRepository, never()).actualizar(any());
    }

    @Test
    void aprobarReservaYaAprobadaLanzaExcepcionYNoActualiza() {
        when(reservaRepository.buscarPorId(RESERVA_ID)).thenReturn(reservaEnEstado(EstadoReserva.APROBADA));

        assertThrows(ReservaException.class, () -> servicio.aprobar(RESERVA_ID));

        verify(reservaRepository, never()).actualizar(any());
    }

    @Test
    void aprobarReservaInexistenteLanzaExcepcion() {
        when(reservaRepository.buscarPorId(99L)).thenReturn(null);

        assertThrows(ReservaException.class, () -> servicio.aprobar(99L));

        verify(reservaRepository, never()).actualizar(any());
    }

    @Test
    void rechazarReservaPendienteCambiaEstadoARechazadaYRecortaMotivo() {
        Reserva pendiente = reservaEnEstado(EstadoReserva.PENDIENTE);
        when(reservaRepository.buscarPorId(RESERVA_ID)).thenReturn(pendiente);

        Reserva resultado = servicio.rechazar(RESERVA_ID, "  Sala en mantención  ");

        assertEquals(EstadoReserva.RECHAZADA, resultado.getEstado());
        assertEquals("Sala en mantención", resultado.getMotivoRechazo());
        verify(reservaRepository).actualizar(argThat(r ->
                r.getEstado() == EstadoReserva.RECHAZADA
                        && "Sala en mantención".equals(r.getMotivoRechazo())));
    }

    @Test
    void rechazarConMotivoNuloLanzaExcepcionYSinTocarRepositorio() {
        assertThrows(ReservaException.class, () -> servicio.rechazar(RESERVA_ID, null));

        verifyNoInteractions(reservaRepository);
    }

    @Test
    void rechazarConMotivoEnBlancoLanzaExcepcionYSinTocarRepositorio() {
        assertThrows(ReservaException.class, () -> servicio.rechazar(RESERVA_ID, "   "));

        verifyNoInteractions(reservaRepository);
    }

    @Test
    void rechazarConMotivoMayorA255CaracteresLanzaExcepcionYSinTocarRepositorio() {
        String motivoLargo = "a".repeat(256);

        assertThrows(ReservaException.class, () -> servicio.rechazar(RESERVA_ID, motivoLargo));

        verifyNoInteractions(reservaRepository);
    }

    @Test
    void rechazarReservaInexistenteLanzaExcepcionYNoActualiza() {
        when(reservaRepository.buscarPorId(99L)).thenReturn(null);

        assertThrows(ReservaException.class, () -> servicio.rechazar(99L, "Conflicto de horario"));

        verify(reservaRepository, never()).actualizar(any());
    }

    @Test
    void rechazarReservaCanceladaLanzaExcepcionYNoActualiza() {
        when(reservaRepository.buscarPorId(RESERVA_ID)).thenReturn(reservaEnEstado(EstadoReserva.CANCELADA));

        assertThrows(ReservaException.class, () -> servicio.rechazar(RESERVA_ID, "Conflicto de horario"));

        verify(reservaRepository, never()).actualizar(any());
    }
}
