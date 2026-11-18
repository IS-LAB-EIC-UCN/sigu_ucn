package cl.ucn.app.service;

import cl.ucn.app.model.Categoria;
import cl.ucn.app.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository repository;
    private CategoriaService service;

    @BeforeEach
    void setUp() {
        service = new CategoriaService(repository);
    }

    @Test
    void registraCategoriaConNombreYDescripcionSinEspaciosEnLosExtremos() {
        Categoria categoria = service.registrarCategoria("  Bebidas  ", "  Frías y calientes  ");

        assertEquals("Bebidas", categoria.getNombre());
        assertEquals("Frías y calientes", categoria.getDescripcion());
        verify(repository).findByNombre("Bebidas");
        verify(repository).save(categoria);
    }

    @Test
    void permiteDescripcionOpcionalYNombreDeCienCaracteres() {
        Categoria categoria = service.registrarCategoria("a".repeat(100), null);

        assertNull(categoria.getDescripcion());
        verify(repository).save(categoria);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t\n", "\u2003"})
    void rechazaNombreVacioSinGuardar(String nombre) {
        assertThrows(IllegalArgumentException.class, () -> service.registrarCategoria(nombre, null));
        verifyNoInteractions(repository);
    }

    @Test
    void rechazaNombreQueSuperaElLimiteDeLaBaseDeDatos() {
        assertThrows(IllegalArgumentException.class,
                () -> service.registrarCategoria("a".repeat(101), null));
        verifyNoInteractions(repository);
    }

    @Test
    void rechazaCategoriaDuplicadaEncontradaPorElRepositorio() {
        when(repository.findByNombre("bebidas")).thenReturn(new Categoria("Bebidas", null));

        assertThrows(IllegalArgumentException.class,
                () -> service.registrarCategoria("  bebidas  ", null));

        verify(repository, never()).save(any());
    }

    @Test
    void listaCategoriasYAdmiteCatalogoVacio() {
        List<Categoria> categorias = List.of(new Categoria("Bebidas", null));
        when(repository.findAll()).thenReturn(categorias).thenReturn(List.of());

        assertEquals(categorias, service.listarCategorias());
        assertTrue(service.listarCategorias().isEmpty());
    }
}
