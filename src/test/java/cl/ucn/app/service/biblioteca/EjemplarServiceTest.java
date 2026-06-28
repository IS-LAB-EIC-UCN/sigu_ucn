package cl.ucn.app.service.biblioteca;

import cl.ucn.app.exceptions.RecursoNoEncontradoException;
import cl.ucn.app.exceptions.ConflictoEstadoException;
import cl.ucn.app.model.biblioteca.Ejemplar;
import cl.ucn.app.model.biblioteca.Libro;
import cl.ucn.app.repository.biblioteca.EjemplarRepository;
import cl.ucn.app.repository.biblioteca.LibroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class EjemplarServiceTest {

    @Mock
    private EjemplarRepository ejemplarRepository;
    @Mock
    private LibroRepository libroRepository;

    private EjemplarService ejemplarService;

    @BeforeEach
    public void setUp() {
        ejemplarService = new EjemplarService(ejemplarRepository, libroRepository);
    }

    @Test
    public void testAgregar_libroNoExiste_lanzaRecursoNoEncontrado() {
        Mockito.when(libroRepository.findById(99L)).thenReturn(null);
        assertThrows(RecursoNoEncontradoException.class, () ->
                ejemplarService.agregarEjemplar(99L));
    }

    @Test
    public void testActualizar_estadoInvalido_lanzaConflictoEstado() {
        Ejemplar e = new Ejemplar();
        e.setId(1L);
        Mockito.when(ejemplarRepository.findById(1L)).thenReturn(e);
        assertThrows(ConflictoEstadoException.class, () ->
                ejemplarService.actualizarEjemplar(1L, null));
    }

    @Test
    public void testEliminar_ejemploNoExiste_lanzaRecursoNoEncontrado() {
        assertThrows(Exception.class, () ->
                ejemplarService.eliminarEjemplar(99L));
    }
}
