package cl.ucn.app.service.biblioteca;

import cl.ucn.app.exceptions.ValidacionException;
import cl.ucn.app.model.biblioteca.Libro;
import cl.ucn.app.repository.biblioteca.LibroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class LibroServiceTest {

    @Mock
    private LibroRepository libroRepository;

    private LibroService libroService;

    @BeforeEach
    public void setUp() {
        libroService = new LibroService(libroRepository);
    }

    @Test
    public void testRegistrar_isbnDuplicado_lanzaValidacion() {
        Mockito.when(libroRepository.findByIsbn("978-0-201-00023-8"))
                .thenReturn(new Libro());
        ValidacionException ex = assertThrows(ValidacionException.class, () ->
                libroService.registrarLibro("Titulo", "Autor", "Cat", "978-0-201-00023-8"));
        assertTrue(ex.getMessage().toLowerCase().contains("isbn"));
        Mockito.verify(libroRepository, Mockito.never()).save(any(Libro.class));
    }

    @Test
    public void testRegistrar_isbnInvalido_lanzaValidacion() {
        assertThrows(ValidacionException.class, () ->
                libroService.registrarLibro("Titulo", "Autor", "Cat", "123"));
        assertThrows(ValidacionException.class, () ->
                libroService.registrarLibro("Titulo", "Autor", "Cat", "12345"));
        assertThrows(ValidacionException.class, () ->
                libroService.registrarLibro("Titulo", "Autor", "Cat", "1234567890X"));
        assertThrows(ValidacionException.class, () ->
                libroService.registrarLibro("Titulo", "Autor", "Cat", ""));
    }

    @Test
    public void testRegistrar_isbnValido_guardaLibro() {
        Mockito.when(libroRepository.findByIsbn("978-0-201-00023-8")).thenReturn(null);
        Libro libro = libroService.registrarLibro("Titulo", "Autor", "Cat", "978-0-201-00023-8");
        assertNotNull(libro);
        assertEquals("Titulo", libro.getTitulo());
        Mockito.verify(libroRepository, Mockito.times(1)).save(any(Libro.class));
    }

    @Test
    public void testActualizar_libroNoExiste_lanzaRecursoNoEncontrado() {
        Mockito.when(libroRepository.findById(99L)).thenReturn(null);
        assertThrows(cl.ucn.app.exceptions.RecursoNoEncontradoException.class, () ->
                libroService.actualizarLibro(99L, "T", "A", "C", "978-0-201-00023-8"));
    }

    @Test
    public void testActualizar_isbnDuplicadoDeOtroLibro_lanzaValidacion() {
        Libro libroExistente = new Libro();
        libroExistente.setId(1L);
        Libro otroLibro = new Libro();
        otroLibro.setId(2L);
        Mockito.when(libroRepository.findById(1L)).thenReturn(libroExistente);
        Mockito.when(libroRepository.findByIsbn("978-0-201-00023-8")).thenReturn(otroLibro);
        assertThrows(ValidacionException.class, () ->
                libroService.actualizarLibro(1L, "T", "A", "C", "978-0-201-00023-8"));
    }
}
