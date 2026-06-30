package cl.ucn.app.service;

import cl.ucn.app.model.Espacio;
import cl.ucn.app.model.Evento;
import cl.ucn.app.repository.UsuarioRepository;
import cl.ucn.app.repository.InscripcionRepository;
import cl.ucn.app.model.Expositor;
import cl.ucn.app.repository.EventoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// 9 TESTS PARA LOS EVENTOS
@ExtendWith(MockitoExtension.class)
class EventoServiceTest {

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private InscripcionRepository inscripcionRepository;

    private EventoService eventoService;

    @BeforeEach
    void setUp() {
        eventoService = new EventoService(
                eventoRepository,
                usuarioRepository,
                inscripcionRepository
        );
    }

    @Test
    void registrarEventoValidoDebeGuardarEvento() {
        Evento evento = crearEventoValido();

        when(eventoRepository.save(evento)).thenReturn(evento);

        Evento resultado = eventoService.registrar(evento);

        assertSame(evento, resultado);
        verify(eventoRepository).save(evento);
    }

    @Test
    void registrarEventoSinTituloDebeLanzarExcepcion() {
        Evento evento = crearEventoValido();
        evento.setTitulo("");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> eventoService.registrar(evento)
        );

        assertEquals("El título del evento es obligatorio.", ex.getMessage());
        verify(eventoRepository, never()).save(any());
    }

    @Test
    void registrarEventoConHoraInicioDespuesDeHoraFinDebeLanzarExcepcion() {
        Evento evento = crearEventoValido();
        evento.setHoraInicio(LocalTime.of(12, 0));
        evento.setHoraFin(LocalTime.of(10, 0));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> eventoService.registrar(evento)
        );

        assertEquals("La hora de inicio debe ser anterior a la hora de fin.", ex.getMessage());
        verify(eventoRepository, never()).save(any());
    }

    @Test
    void registrarEventoConCapacidadMayorAlEspacioDebeLanzarExcepcion() {
        Evento evento = crearEventoValido();
        evento.setCapacidad(100);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> eventoService.registrar(evento)
        );

        assertTrue(ex.getMessage().contains("no puede superar la capacidad del espacio"));
        verify(eventoRepository, never()).save(any());
    }

    @Test
    void registrarEventoConConflictoDeHorarioDebeLanzarExcepcion() {
        Evento evento = crearEventoValido();

        when(eventoRepository.existeConflictoHorario(
                anyLong(), any(), any(), any(), isNull()
        )).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> eventoService.registrar(evento)
        );

        assertEquals("Ya existe un evento programado en el mismo espacio y horario.", ex.getMessage());
        verify(eventoRepository, never()).save(any());
    }

    @Test
    void registrarEventoConExpositorRepetidoDebeLanzarExcepcion() {
        Evento evento = crearEventoValido();

        Expositor expositor = new Expositor("Ana Pérez", "ana@ucn.cl", "UCN");
        expositor.setId(1L);

        evento.setExpositores(List.of(expositor, expositor));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> eventoService.registrar(evento)
        );

        assertTrue(ex.getMessage().contains("no puede repetirse"));
        verify(eventoRepository, never()).save(any());
    }

    @Test
    void registrarEventoConExpositorEnOtroEventoMismoHorarioDebeLanzarExcepcion() {
        Evento evento = crearEventoValido();

        Expositor expositor = new Expositor("Ana Pérez", "ana@ucn.cl", "UCN");
        expositor.setId(1L);
        evento.setExpositores(List.of(expositor));

        when(eventoRepository.expositorTieneConflictoHorario(
                anyLong(), any(), any(), any(), isNull()
        )).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> eventoService.registrar(evento)
        );

        assertTrue(ex.getMessage().contains("ya se encuentra asignado a otro evento"));
        verify(eventoRepository, never()).save(any());
    }

    @Test
    void cancelarEventoDebeCambiarEstadoACanceladoYGuardar() {
        Evento evento = crearEventoValido();
        evento.setId(1L);

        when(eventoRepository.findById(1L)).thenReturn(evento);
        when(eventoRepository.save(evento)).thenReturn(evento);

        Evento resultado = eventoService.cancelar(1L);

        assertEquals("CANCELADO", resultado.getEstado());
        verify(eventoRepository).save(evento);
    }

    @Test
    void eliminarEventoNoCanceladoDebeLanzarExcepcion() {
        Evento evento = crearEventoValido();
        evento.setId(1L);
        evento.setEstado("PLANIFICADO");

        when(eventoRepository.findById(1L)).thenReturn(evento);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> eventoService.eliminar(1L)
        );

        assertEquals("Solo se pueden eliminar eventos cancelados.", ex.getMessage());
        verify(eventoRepository, never()).delete(any());
    }

    private Evento crearEventoValido() {
        Espacio espacio = new Espacio("Auditorio K", "Auditorio", 50, true);
        espacio.setId(1L);

        return new Evento(
                "Charla de IA",
                "Evento académico sobre inteligencia artificial",
                LocalDate.now().plusDays(10),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                "Tecnología",
                40,
                espacio
        );
    }
}