package cl.ucn.app.service.biblioteca;

import cl.ucn.app.exceptions.ConflictoEstadoException;
import cl.ucn.app.exceptions.RecursoNoEncontradoException;
import cl.ucn.app.model.biblioteca.Ejemplar;
import cl.ucn.app.model.biblioteca.EstadoEjemplar;
import cl.ucn.app.model.biblioteca.EstadoPrestamo;
import cl.ucn.app.model.biblioteca.PrestamoLibro;
import cl.ucn.app.repository.biblioteca.EjemplarRepository;
import cl.ucn.app.repository.biblioteca.PrestamoLibroRepository;
import cl.ucn.app.service.biblioteca.api.IMultaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DevolucionServiceTest {

    @Mock
    private EjemplarRepository ejemplarRepository;
    @Mock
    private PrestamoLibroRepository prestamoLibroRepository;
    @Mock
    private IMultaService multaService;
    @Mock
    private EntityManager em;
    @Mock
    private EntityTransaction tx;

    private DevolucionService devolucionService;

    @BeforeEach
    public void setUp() {
        devolucionService = new DevolucionService(ejemplarRepository, prestamoLibroRepository, multaService);
        Mockito.lenient().when(em.getTransaction()).thenReturn(tx);
    }

    @Test
    public void testRegistrar_prestamoNoExiste_lanzaRecursoNoEncontrado() {
        Mockito.when(prestamoLibroRepository.findById(99L)).thenReturn(null);
        assertThrows(RecursoNoEncontradoException.class, () ->
                devolucionService.registrarDevolucion(99L, em));
    }

    @Test
    public void testRegistrar_prestamoYaFinalizado_lanzaConflictoEstado() {
        PrestamoLibro prestamo = new PrestamoLibro();
        prestamo.setId(1L);
        prestamo.setEstado(EstadoPrestamo.FINALIZADO);
        Mockito.when(prestamoLibroRepository.findById(1L)).thenReturn(prestamo);
        assertThrows(ConflictoEstadoException.class, () ->
                devolucionService.registrarDevolucion(1L, em));
    }

    @Test
    public void testRegistrar_conAtraso_generaMulta() {
        PrestamoLibro prestamo = new PrestamoLibro();
        prestamo.setId(1L);
        prestamo.setEstado(EstadoPrestamo.ACTIVO);
        prestamo.setFechaVencimiento(LocalDate.now().minusDays(3));
        Ejemplar ejemplar = new Ejemplar();
        prestamo.setEjemplar(ejemplar);
        Mockito.when(prestamoLibroRepository.findById(1L)).thenReturn(prestamo);

        devolucionService.registrarDevolucion(1L, em);

        Mockito.verify(multaService).generarMulta(prestamo, 3, em);
        assertEquals(EstadoPrestamo.FINALIZADO, prestamo.getEstado());
        assertEquals(EstadoEjemplar.DISPONIBLE, ejemplar.getEstado());
    }

    @Test
    public void testRegistrar_sinAtraso_ejemplarQuedaDisponible() {
        PrestamoLibro prestamo = new PrestamoLibro();
        prestamo.setId(1L);
        prestamo.setEstado(EstadoPrestamo.ACTIVO);
        prestamo.setFechaVencimiento(LocalDate.now().plusDays(7));
        Ejemplar ejemplar = new Ejemplar();
        prestamo.setEjemplar(ejemplar);
        Mockito.when(prestamoLibroRepository.findById(1L)).thenReturn(prestamo);

        devolucionService.registrarDevolucion(1L, em);

        Mockito.verify(multaService, Mockito.never()).generarMulta(Mockito.any(), Mockito.anyInt(), Mockito.any());
        assertEquals(EstadoEjemplar.DISPONIBLE, ejemplar.getEstado());
    }
}
