package cl.ucn.app.service.biblioteca;

import cl.ucn.app.model.biblioteca.Libro;
import cl.ucn.app.repository.biblioteca.EjemplarRepository;
import cl.ucn.app.repository.biblioteca.LibroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BusquedaServiceTest {

    @Mock
    private LibroRepository libroRepository;
    @Mock
    private EjemplarRepository ejemplarRepository;

    private BusquedaService busquedaService;

    @BeforeEach
    public void setUp() {
        busquedaService = new BusquedaService(libroRepository, ejemplarRepository);
    }

    @Test
    public void testBuscar_sinDuplicados() {
        Libro libro1 = new Libro();
        libro1.setId(1L);
        libro1.setTitulo("Estructuras de Datos");
        Mockito.when(libroRepository.findByTitulo("datos")).thenReturn(List.of(libro1));
        Mockito.when(libroRepository.findByAutor("datos")).thenReturn(List.of(libro1));
        Mockito.when(libroRepository.findByCategoria("datos")).thenReturn(List.of());

        List<Libro> resultado = busquedaService.buscar("datos");
        assertEquals(1, resultado.size());
    }

    @Test
    public void testBuscar_terminoNulo_retornaVacio() {
        assertTrue(busquedaService.buscar(null).isEmpty());
    }
}
