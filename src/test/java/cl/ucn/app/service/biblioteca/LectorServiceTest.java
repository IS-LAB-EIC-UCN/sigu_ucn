package cl.ucn.app.service.biblioteca;

import cl.ucn.app.exceptions.RecursoNoEncontradoException;
import cl.ucn.app.exceptions.ValidacionException;
import cl.ucn.app.model.biblioteca.Lector;
import cl.ucn.app.repository.biblioteca.LectorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class LectorServiceTest {

    @Mock
    private LectorRepository lectorRepository;

    private LectorService lectorService;

    @BeforeEach
    public void setUp() {
        lectorService = new LectorService(lectorRepository);
    }

    @Test
    public void testRegistrar_correoDuplicado_lanzaValidacion() {
        Mockito.when(lectorRepository.findByRut("12345678-5")).thenReturn(null);
        Mockito.when(lectorRepository.findByCorreo("juan@ucn.cl"))
                .thenReturn(new Lector());
        assertThrows(ValidacionException.class, () ->
                lectorService.registrarLector("Juan Perez", "juan@ucn.cl", "12345678-5"));
    }

    @Test
    public void testRegistrar_rutInvalido_lanzaValidacion() {
        assertThrows(ValidacionException.class, () ->
                lectorService.registrarLector("Juan Perez", "juan@ucn.cl", "1234"));
        assertThrows(ValidacionException.class, () ->
                lectorService.registrarLector("Juan Perez", "juan@ucn.cl", "abcdefgh-i"));
        assertThrows(ValidacionException.class, () ->
                lectorService.registrarLector("Juan Perez", "juan@ucn.cl", "12345678-0"));
    }

    @Test
    public void testRegistrar_nombreInvalido_lanzaValidacion() {
        assertThrows(ValidacionException.class, () ->
                lectorService.registrarLector("Jo", "juan@ucn.cl", "12345678-5"));
    }

    @Test
    public void testActualizar_lectorNoExiste_lanzaRecursoNoEncontrado() {
        Mockito.when(lectorRepository.findById(99L)).thenReturn(null);
        assertThrows(RecursoNoEncontradoException.class, () ->
                lectorService.actualizarLector(99L, "Juan", "j@u.cl", "12345678-5"));
    }
}
