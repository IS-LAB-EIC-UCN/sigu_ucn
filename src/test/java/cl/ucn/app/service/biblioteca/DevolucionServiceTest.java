package cl.ucn.app.service.biblioteca;

import cl.ucn.app.exceptions.ConflictoEstadoException;
import cl.ucn.app.exceptions.RecursoNoEncontradoException;
import cl.ucn.app.model.biblioteca.Ejemplar;
import cl.ucn.app.model.biblioteca.Libro;
import cl.ucn.app.model.biblioteca.PrestamoLibro;
import cl.ucn.app.repository.biblioteca.EjemplarRepository;
import cl.ucn.app.repository.biblioteca.PrestamoLibroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DevolucionServiceTest {

    @Mock
    private EjemplarRepository ejemplarRepository;
    @Mock
    private PrestamoLibroRepository prestamoLibroRepository;
    @Mock
    private MultaService multaService;

    private DevolucionService devolucionService;

    @BeforeEach
    public void setUp() {
        devolucionService = new DevolucionService(ejemplarRepository, prestamoLibroRepository, multaService);
    }

    @Test
    public void testRegistrar_prestamoNoExiste_lanzaRecursoNoEncontrado() {
        Mockito.when(prestamoLibroRepository.findById(99L)).thenReturn(null);
        assertThrows(RecursoNoEncontradoException.class, () ->
                devolucionService.registrarDevolucion(99L));
    }

    @Test
    public void testRegistrar_prestamoYaFinalizado_lanzaConflictoEstado() {
        PrestamoLibro prestamo = new PrestamoLibro();
        prestamo.setId(1L);
        prestamo.setEstado("FINALIZADO");
        Mockito.when(prestamoLibroRepository.findById(1L)).thenReturn(prestamo);
        assertThrows(ConflictoEstadoException.class, () ->
                devolucionService.registrarDevolucion(1L));
    }

    @Test
    public void testRegistrar_sinAtraso_noGeneraMulta() {
        PrestamoLibro prestamo = new PrestamoLibro();
        prestamo.setId(1L);
        prestamo.setEstado("ACTIVO");
        prestamo.setFechaVencimiento(LocalDate.now().plusDays(7));
        Ejemplar ejemplar = new Ejemplar();
        ejemplar.setId(10L);
        prestamo.setEjemplar(ejemplar);
        Mockito.when(prestamoLibroRepository.findById(1L)).thenReturn(prestamo);
        devolucionService.registrarDevolucion(1L);
        assertEquals("FINALIZADO", prestamo.getEstado());
        assertEquals("DISPONIBLE", ejemplar.getEstado());
    }
}
