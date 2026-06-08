package cl.ucn.app.service.biblioteca;

import cl.ucn.app.model.biblioteca.Ejemplar;
import cl.ucn.app.model.biblioteca.Lector;
import cl.ucn.app.model.biblioteca.PrestamoLibro;
import cl.ucn.app.repository.biblioteca.EjemplarRepository;
import cl.ucn.app.repository.biblioteca.LectorRepository;
import cl.ucn.app.repository.biblioteca.PrestamoLibroRepository;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

    @InjectMocks
    private PrestamoLibroService prestamoLibroService;

    @Test
    public void testPrestamoLibroExitoso(){
        LocalDate fechaVencimiento = LocalDate.now().plusDays(7);
        Lector lectorTest = new Lector();
        lectorTest.setBloqueado(false);

        Ejemplar ejemplarTest = new Ejemplar();
        ejemplarTest.setEstado("DISPONIBLE");

        Long idLector = lectorTest.getId();
        Long idEjemplar = ejemplarTest.getId();

        Mockito.when(lectorRepository.findById(idLector)).thenReturn(lectorTest);
        Mockito.when(ejemplarRepository.findById(idEjemplar)).thenReturn(ejemplarTest);

        PrestamoLibro resultado = prestamoLibroService.solicitarPrestamo(idLector,idEjemplar,fechaVencimiento);
        assertNotNull(resultado);
        assertEquals("PRESTADO", ejemplarTest.getEstado(), "El ejemplar debió cambiar su estado a PRESTADO");
        assertEquals("ACTIVO", resultado.getEstado());
        assertEquals(lectorTest, resultado.getLector());

        Mockito.verify(ejemplarRepository, Mockito.times(1)).save(ejemplarTest);
        Mockito.verify(prestamoLibroRepository, Mockito.times(1)).save(resultado);
    }

    public void testPrestamoFallido(){
        LocalDate fechaVencimiento = LocalDate.now().plusDays(7);
        Lector lectorTest = new Lector();
        lectorTest.setBloqueado(false);

        Ejemplar ejemplarTest = new Ejemplar();
        ejemplarTest.setEstado("PRESTADO");

        Long idLector = lectorTest.getId();
        Long idEjemplar = ejemplarTest.getId();

        Mockito.when(lectorRepository.findById(lectorTest.getId())).thenReturn(lectorTest);
        Mockito.when(ejemplarRepository.findById(ejemplarTest.getId())).thenReturn(ejemplarTest);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        {prestamoLibroService.solicitarPrestamo(idLector,idEjemplar,fechaVencimiento);});

        assertTrue(exception.getMessage().contains("no disponible"));
        Mockito.verify(prestamoLibroRepository, Mockito.never()).save(Mockito.any());
    }

    public void testPrestamoLectorBlock(){
        LocalDate fechaVencimiento = LocalDate.now().plusDays(7);
        Lector lectorTest = new Lector();
        lectorTest.setBloqueado(true);

        Ejemplar ejemplarTest = new Ejemplar();
        ejemplarTest.setEstado("DISPONIBLE");

        Long idLector = lectorTest.getId();
        Long idEjemplar = ejemplarTest.getId();

        Mockito.when(lectorRepository.findById(lectorTest.getId())).thenReturn(lectorTest);
        Mockito.when(ejemplarRepository.findById(ejemplarTest.getId())).thenReturn(ejemplarTest);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        {prestamoLibroService.solicitarPrestamo(idLector,idEjemplar,fechaVencimiento);});

        assertTrue(exception.getMessage().contains("bloqueado"));
        Mockito.verify(prestamoLibroRepository, Mockito.never()).save(Mockito.any());
    }
}