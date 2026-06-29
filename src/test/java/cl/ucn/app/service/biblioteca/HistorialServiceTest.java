package cl.ucn.app.service.biblioteca;

import cl.ucn.app.exceptions.RecursoNoEncontradoException;
import cl.ucn.app.model.biblioteca.Lector;
import cl.ucn.app.model.biblioteca.PrestamoLibro;
import cl.ucn.app.repository.biblioteca.LectorRepository;
import cl.ucn.app.repository.biblioteca.PrestamoLibroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class HistorialServiceTest {

    @Mock
    private LectorRepository lectorRepository;
    @Mock
    private PrestamoLibroRepository prestamoLibroRepository;

    private HistorialService historialService;

    @BeforeEach
    public void setUp() {
        historialService = new HistorialService(lectorRepository, prestamoLibroRepository);
    }

    @Test
    public void testObtenerHistorial_lectorNoExiste_lanzaRecursoNoEncontrado() {
        Mockito.when(lectorRepository.findById(1L)).thenReturn(null);
        assertThrows(RecursoNoEncontradoException.class, () ->
                historialService.obtenerHistorial(1L));
    }

    @Test
    public void testObtenerHistorial_ok_devuelvePrestamos() {
        Lector lector = new Lector();
        PrestamoLibro p1 = new PrestamoLibro();
        p1.setId(10L);
        Mockito.when(lectorRepository.findById(1L)).thenReturn(lector);
        Mockito.when(prestamoLibroRepository.findByLector(lector)).thenReturn(List.of(p1));

        List<PrestamoLibro> resultado = historialService.obtenerHistorial(1L);
        assertEquals(1, resultado.size());
    }
}
