package cl.ucn.app.service.biblioteca;

import cl.ucn.app.model.biblioteca.Ejemplar;
import cl.ucn.app.model.biblioteca.Lector;
import cl.ucn.app.model.biblioteca.PrestamoLibro;
import cl.ucn.app.repository.biblioteca.EjemplarRepository;
import cl.ucn.app.repository.biblioteca.LectorRepository;
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
public class PrestamoLibroServiceTest{
    @Mock
    private PrestamoLibroRepository prestamoLibroRepository;
    @Mock
    private LectorRepository lectorRepository;
    @Mock
    private EjemplarRepository ejemplarRepository;

    private PrestamoLibroService prestamoLibroService;

    @BeforeEach
    public void setUp(){
        prestamoLibroService = new PrestamoLibroService(
                prestamoLibroRepository,
                lectorRepository,
                ejemplarRepository
        );
    }

    @Test
    public void testPrestamoLibroExitoso(){
        LocalDate fechaVencimiento = LocalDate.now().plusDays(7);
        Lector lectorTest = new Lector();
        lectorTest.setBloqueado(false);

        Ejemplar ejemplarTest = new Ejemplar();
        ejemplarTest.setEstado("DISPONIBLE");

        Long idLector = 1L;
        Long idEjemplar = 2L;

        Mockito.when(lectorRepository.findById(Mockito.anyLong())).thenReturn(lectorTest);
        Mockito.when(ejemplarRepository.findById(Mockito.anyLong())).thenReturn(ejemplarTest);

        PrestamoLibro resultado = prestamoLibroService.solicitarPrestamo(idLector,idEjemplar,fechaVencimiento);
        assertNotNull(resultado);
        assertEquals("PRESTADO", ejemplarTest.getEstado(), "El ejemplar debió cambiar su estado a PRESTADO");
        assertEquals("ACTIVO", resultado.getEstado());
        assertEquals(lectorTest, resultado.getLector());

        Mockito.verify(ejemplarRepository, Mockito.times(1)).save(ejemplarTest);
        Mockito.verify(prestamoLibroRepository, Mockito.times(1)).save(resultado);
    }

    @Test
    public void testPrestamoFallido(){
        LocalDate fechaVencimiento = LocalDate.now().plusDays(7);
        Lector lectorTest = new Lector();
        lectorTest.setBloqueado(false);

        Ejemplar ejemplarTest = new Ejemplar();
        ejemplarTest.setEstado("PRESTADO");

        Long idLector = 1L;
        Long idEjemplar = 2L;

        Mockito.when(lectorRepository.findById(Mockito.anyLong())).thenReturn(lectorTest);
        Mockito.when(ejemplarRepository.findById(Mockito.anyLong())).thenReturn(ejemplarTest);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        {prestamoLibroService.solicitarPrestamo(idLector,idEjemplar,fechaVencimiento);});

        assertTrue(exception.getMessage().contains("no disponible"));
        Mockito.verify(prestamoLibroRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void testPrestamoLectorBlock(){
        LocalDate fechaVencimiento = LocalDate.now().plusDays(7);
        Lector lectorTest = new Lector();
        lectorTest.setBloqueado(true);

        Ejemplar ejemplarTest = new Ejemplar();
        ejemplarTest.setEstado("DISPONIBLE");

        Long idLector = 1L;
        Long idEjemplar = 2L;

        Mockito.when(lectorRepository.findById(Mockito.anyLong())).thenReturn(lectorTest);
        Mockito.when(ejemplarRepository.findById(Mockito.anyLong())).thenReturn(ejemplarTest);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        {prestamoLibroService.solicitarPrestamo(idLector,idEjemplar,fechaVencimiento);});

        assertTrue(exception.getMessage().contains("bloqueado"));
        Mockito.verify(prestamoLibroRepository, Mockito.never()).save(Mockito.any());
    }
}