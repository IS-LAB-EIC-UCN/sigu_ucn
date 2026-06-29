package cl.ucn.app.service.biblioteca;

import cl.ucn.app.exceptions.ConflictoEstadoException;
import cl.ucn.app.exceptions.ValidacionException;
import cl.ucn.app.model.biblioteca.Ejemplar;
import cl.ucn.app.model.biblioteca.EstadoEjemplar;
import cl.ucn.app.model.biblioteca.EstadoPrestamo;
import cl.ucn.app.model.biblioteca.Lector;
import cl.ucn.app.model.biblioteca.Libro;
import cl.ucn.app.model.biblioteca.PrestamoLibro;
import cl.ucn.app.repository.biblioteca.EjemplarRepository;
import cl.ucn.app.repository.biblioteca.LectorRepository;
import cl.ucn.app.repository.biblioteca.LibroRepository;
import cl.ucn.app.repository.biblioteca.PrestamoLibroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PrestamoLibroServiceTest {
    @Mock
    private PrestamoLibroRepository prestamoLibroRepository;
    @Mock
    private LectorRepository lectorRepository;
    @Mock
    private EjemplarRepository ejemplarRepository;
    @Mock
    private LibroRepository libroRepository;
    @Mock
    private EntityManager em;
    @Mock
    private EntityTransaction tx;

    private PrestamoLibroService prestamoLibroService;

    @BeforeEach
    public void setUp() {
        prestamoLibroService = new PrestamoLibroService(
                prestamoLibroRepository,
                lectorRepository,
                ejemplarRepository,
                libroRepository
        );
        Mockito.lenient().when(em.getTransaction()).thenReturn(tx);
    }

    @Test
    public void testPreparar_ok_cambiaEstadoEjemplar() {
        LocalDate fechaVencimiento = LocalDate.now().plusDays(7);
        Lector lector = new Lector();
        lector.setBloqueado(false);
        Libro libro = new Libro();
        libro.setId(10L);
        Ejemplar ejemplar = new Ejemplar();
        ejemplar.setLibro(libro);
        ejemplar.setEstado(EstadoEjemplar.DISPONIBLE);
        Mockito.when(lectorRepository.findById(1L)).thenReturn(lector);
        Mockito.when(libroRepository.findById(10L)).thenReturn(libro);
        Mockito.when(ejemplarRepository.findDisponiblesByLibro(libro)).thenReturn(List.of(ejemplar));

        PrestamoLibro resultado = prestamoLibroService.prepararPrestamo(1L, 10L, fechaVencimiento, EstadoPrestamo.SOLICITADO);

        assertEquals(EstadoEjemplar.PRESTADO, ejemplar.getEstado());
        assertEquals(EstadoPrestamo.SOLICITADO, resultado.getEstado());
    }

    @Test
    public void testPreparar_fechaVencimientoNula_lanzaValidacion() {
        assertThrows(ValidacionException.class, () ->
                prestamoLibroService.prepararPrestamo(1L, 10L, null, EstadoPrestamo.SOLICITADO));
    }

    @Test
    public void testPreparar_lectorBloqueado_lanzaConflicto() {
        Lector lector = new Lector();
        lector.setBloqueado(true);
        Libro libro = new Libro();
        Ejemplar ejemplar = new Ejemplar();
        ejemplar.setLibro(libro);
        Mockito.when(lectorRepository.findById(1L)).thenReturn(lector);
        Mockito.when(libroRepository.findById(2L)).thenReturn(libro);
        Mockito.when(ejemplarRepository.findDisponiblesByLibro(libro)).thenReturn(List.of(ejemplar));

        assertThrows(ConflictoEstadoException.class, () ->
                prestamoLibroService.prepararPrestamo(1L, 2L, LocalDate.now().plusDays(7), EstadoPrestamo.SOLICITADO));
    }

    @Test
    public void testPreparar_sinDisponibles_lanzaConflicto() {
        Lector lector = new Lector();
        Libro libro = new Libro();
        Mockito.when(lectorRepository.findById(1L)).thenReturn(lector);
        Mockito.when(libroRepository.findById(2L)).thenReturn(libro);
        Mockito.when(ejemplarRepository.findDisponiblesByLibro(libro)).thenReturn(List.of());

        assertThrows(ConflictoEstadoException.class, () ->
                prestamoLibroService.prepararPrestamo(1L, 2L, LocalDate.now().plusDays(7), EstadoPrestamo.SOLICITADO));
    }

    @Test
    public void testSolicitarDevolucion_estadoNoActivo_lanzaConflicto() {
        PrestamoLibro prestamo = new PrestamoLibro();
        prestamo.setId(1L);
        prestamo.setEstado(EstadoPrestamo.SOLICITADO);
        Mockito.when(prestamoLibroRepository.findById(1L)).thenReturn(prestamo);

        assertThrows(ConflictoEstadoException.class, () ->
                prestamoLibroService.solicitarDevolucion(1L));
    }

    @Test
    public void testConfirmarEntrega_estadoNoSolicitado_lanzaConflicto() {
        PrestamoLibro prestamo = new PrestamoLibro();
        prestamo.setId(1L);
        prestamo.setEstado(EstadoPrestamo.ACTIVO);
        Mockito.when(prestamoLibroRepository.findById(1L)).thenReturn(prestamo);

        assertThrows(ConflictoEstadoException.class, () ->
                prestamoLibroService.confirmarEntrega(1L));
    }

    @Test
    public void testPersistirPrestamo_guardaEjemplarYPrestamo() {
        Ejemplar ejemplar = new Ejemplar();
        PrestamoLibro prestamo = new PrestamoLibro();
        prestamo.setEjemplar(ejemplar);

        prestamoLibroService.persistirPrestamo(prestamo, em);

        Mockito.verify(ejemplarRepository).save(ejemplar, em);
        Mockito.verify(prestamoLibroRepository).save(prestamo, em);
    }
}
