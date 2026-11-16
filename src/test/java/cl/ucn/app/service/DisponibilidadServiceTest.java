package cl.ucn.app.service;

import cl.ucn.app.model.BloqueHorario;
import cl.ucn.app.model.Espacio;
import cl.ucn.app.model.EstadoReserva;
import cl.ucn.app.model.Reserva;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.EspacioRepository;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DisponibilidadServiceTest {

    private static final Long SALA_ID = 1L;
    private static final LocalDate FECHA = LocalDate.now().plusDays(10);

    @Mock
    private ReservaRepository reservaRepository;
    @Mock
    private EspacioRepository espacioRepository;

    private DisponibilidadService servicio;
    private Espacio salaHabilitada;

    @BeforeEach
    void preparar() {
        servicio = new DisponibilidadService(reservaRepository, espacioRepository);
        salaHabilitada = new Espacio("Sala 101", "SALA", 30, true);
    }

    private Reserva reserva(LocalTime inicio, LocalTime fin, EstadoReserva estado) {
        Reserva r = new Reserva(FECHA, inicio, fin, "Motivo de prueba", new Usuario(), salaHabilitada);
        switch (estado) {
            case APROBADA -> r.aprobar();
            case RECHAZADA -> r.rechazar("Rechazo de prueba");
            case CANCELADA -> r.cancelar(LocalDateTime.now());
            case PENDIENTE -> { }
        }
        return r;
    }

    @Test
    void sinReservasDevuelveTodoElDiaLibre() {
        when(espacioRepository.findById(SALA_ID)).thenReturn(salaHabilitada);
        when(reservaRepository.buscarActivasPorEspacioYFecha(SALA_ID, FECHA)).thenReturn(List.of());

        Disponibilidad resultado = servicio.consultar(SALA_ID, FECHA);

        assertTrue(resultado.ocupados().isEmpty());
        assertEquals(List.of(new BloqueHorario(LocalTime.of(8, 0), LocalTime.of(22, 0), null)),
                resultado.libres());
    }

    @Test
    void dosReservasActivasSeOrdenanYCalculanBloquesLibres() {
        when(espacioRepository.findById(SALA_ID)).thenReturn(salaHabilitada);
        when(reservaRepository.buscarActivasPorEspacioYFecha(SALA_ID, FECHA)).thenReturn(List.of(
                reserva(LocalTime.of(14, 0), LocalTime.of(16, 0), EstadoReserva.PENDIENTE),
                reserva(LocalTime.of(9, 0), LocalTime.of(11, 0), EstadoReserva.APROBADA)
        ));

        Disponibilidad resultado = servicio.consultar(SALA_ID, FECHA);

        assertEquals(List.of(
                new BloqueHorario(LocalTime.of(9, 0), LocalTime.of(11, 0), EstadoReserva.APROBADA),
                new BloqueHorario(LocalTime.of(14, 0), LocalTime.of(16, 0), EstadoReserva.PENDIENTE)
        ), resultado.ocupados());
        assertEquals(List.of(
                new BloqueHorario(LocalTime.of(8, 0), LocalTime.of(9, 0), null),
                new BloqueHorario(LocalTime.of(11, 0), LocalTime.of(14, 0), null),
                new BloqueHorario(LocalTime.of(16, 0), LocalTime.of(22, 0), null)
        ), resultado.libres());
    }

    @Test
    void reservasCanceladasYRechazadasSeIgnoran() {
        when(espacioRepository.findById(SALA_ID)).thenReturn(salaHabilitada);
        when(reservaRepository.buscarActivasPorEspacioYFecha(SALA_ID, FECHA)).thenReturn(List.of(
                reserva(LocalTime.of(10, 0), LocalTime.of(12, 0), EstadoReserva.CANCELADA),
                reserva(LocalTime.of(15, 0), LocalTime.of(17, 0), EstadoReserva.RECHAZADA)
        ));

        Disponibilidad resultado = servicio.consultar(SALA_ID, FECHA);

        assertTrue(resultado.ocupados().isEmpty());
        assertEquals(List.of(new BloqueHorario(LocalTime.of(8, 0), LocalTime.of(22, 0), null)),
                resultado.libres());
    }

    @Test
    void bloquesDesdeAperturaHastaCierreNoGeneranHuecosVacios() {
        when(espacioRepository.findById(SALA_ID)).thenReturn(salaHabilitada);
        when(reservaRepository.buscarActivasPorEspacioYFecha(SALA_ID, FECHA)).thenReturn(List.of(
                reserva(LocalTime.of(8, 0), LocalTime.of(13, 0), EstadoReserva.APROBADA),
                reserva(LocalTime.of(13, 0), LocalTime.of(22, 0), EstadoReserva.PENDIENTE)
        ));

        Disponibilidad resultado = servicio.consultar(SALA_ID, FECHA);

        assertEquals(2, resultado.ocupados().size());
        assertTrue(resultado.libres().isEmpty());
    }

    @Test
    void bloquesAdyacentesSeUnenAlCalcularLibres() {
        when(espacioRepository.findById(SALA_ID)).thenReturn(salaHabilitada);
        when(reservaRepository.buscarActivasPorEspacioYFecha(SALA_ID, FECHA)).thenReturn(List.of(
                reserva(LocalTime.of(10, 0), LocalTime.of(11, 0), EstadoReserva.APROBADA),
                reserva(LocalTime.of(11, 0), LocalTime.of(12, 0), EstadoReserva.PENDIENTE)
        ));

        Disponibilidad resultado = servicio.consultar(SALA_ID, FECHA);

        assertEquals(List.of(
                new BloqueHorario(LocalTime.of(8, 0), LocalTime.of(10, 0), null),
                new BloqueHorario(LocalTime.of(12, 0), LocalTime.of(22, 0), null)
        ), resultado.libres());
    }

    @Test
    void bloquesSolapadosSeUnenYRecortanAlHorarioDeAtencion() {
        when(espacioRepository.findById(SALA_ID)).thenReturn(salaHabilitada);
        when(reservaRepository.buscarActivasPorEspacioYFecha(SALA_ID, FECHA)).thenReturn(List.of(
                reserva(LocalTime.of(7, 0), LocalTime.of(10, 0), EstadoReserva.APROBADA),
                reserva(LocalTime.of(9, 30), LocalTime.of(12, 0), EstadoReserva.PENDIENTE),
                reserva(LocalTime.of(21, 0), LocalTime.of(23, 0), EstadoReserva.APROBADA)
        ));

        Disponibilidad resultado = servicio.consultar(SALA_ID, FECHA);

        assertEquals(List.of(
                new BloqueHorario(LocalTime.of(12, 0), LocalTime.of(21, 0), null)
        ), resultado.libres());
    }

    @Test
    void argumentosNulosLanzanReservaException() {
        assertThrows(ReservaException.class, () -> servicio.consultar(null, FECHA));
        assertThrows(ReservaException.class, () -> servicio.consultar(SALA_ID, null));
        verify(espacioRepository, never()).findById(any());
    }

    @Test
    void salaInexistenteLanzaReservaException() {
        when(espacioRepository.findById(SALA_ID)).thenReturn(null);

        assertThrows(ReservaException.class, () -> servicio.consultar(SALA_ID, FECHA));
        verify(reservaRepository, never()).buscarActivasPorEspacioYFecha(any(), any());
    }

    @Test
    void salaNoHabilitadaLanzaReservaException() {
        Espacio salaInhabilitada = new Espacio("Sala Cerrada", "SALA", 20, false);
        when(espacioRepository.findById(SALA_ID)).thenReturn(salaInhabilitada);

        assertThrows(ReservaException.class, () -> servicio.consultar(SALA_ID, FECHA));
        verify(reservaRepository, never()).buscarActivasPorEspacioYFecha(any(), any());
    }
}
