package cl.ucn.app.service;

import cl.ucn.app.model.Categoria;
import cl.ucn.app.model.Producto;
import cl.ucn.app.repository.CategoriaRepository;
import cl.ucn.app.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productos;
    @Mock
    private CategoriaRepository categorias;
    private ProductoService service;

    @BeforeEach
    void setUp() {
        service = new ProductoService(productos, categorias);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "1500.50"})
    void registraProductoConCategoriaExistenteYAdmitePrecioYStockCero(String valor) {
        Categoria categoria = new Categoria("Bebidas", null);
        when(categorias.findById(1L)).thenReturn(categoria);
        BigDecimal precio = new BigDecimal(valor);

        Producto producto = service.registrarProducto("  Café  ", precio, 0, 1L);

        assertEquals("Café", producto.getNombre());
        assertEquals(precio, producto.getPrecio());
        assertEquals(0, producto.getStock());
        assertSame(categoria, producto.getCategoria());
        verify(productos).save(producto);
    }

    static Stream<Arguments> datosInvalidos() {
        return Stream.of(
                Arguments.of(null, BigDecimal.ONE, 1, 1L),
                Arguments.of("", BigDecimal.ONE, 1, 1L),
                Arguments.of(" \t\n", BigDecimal.ONE, 1, 1L),
                Arguments.of("a".repeat(101), BigDecimal.ONE, 1, 1L),
                Arguments.of("Café", null, 1, 1L),
                Arguments.of("Café", new BigDecimal("-0.01"), 1, 1L),
                Arguments.of("Café", BigDecimal.ONE, null, 1L),
                Arguments.of("Café", BigDecimal.ONE, -1, 1L),
                Arguments.of("Café", BigDecimal.ONE, 1, null),
                Arguments.of("Café", BigDecimal.ONE, 1, 0L),
                Arguments.of("Café", BigDecimal.ONE, 1, -1L));
    }

    @ParameterizedTest
    @MethodSource("datosInvalidos")
    void rechazaDatosInvalidosSinGuardar(String nombre, BigDecimal precio, Integer stock, Long categoriaId) {
        assertThrows(IllegalArgumentException.class,
                () -> service.registrarProducto(nombre, precio, stock, categoriaId));
        verifyNoInteractions(productos, categorias);
    }

    @Test
    void rechazaCategoriaInexistenteSinGuardarProducto() {
        assertThrows(IllegalArgumentException.class,
                () -> service.registrarProducto("Café", BigDecimal.ONE, 10, 99L));
        verify(productos, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 4, 20})
    void reemplazaStockSinCambiarLosDemasDatos(int nuevoStock) {
        Categoria categoria = new Categoria("Bebidas", null);
        Producto producto = new Producto("Café", new BigDecimal("1500"), 10, categoria);
        when(productos.findById(1L)).thenReturn(producto);

        Producto actualizado = service.actualizarStock(1L, nuevoStock);

        assertSame(producto, actualizado);
        assertEquals(nuevoStock, actualizado.getStock());
        assertEquals("Café", actualizado.getNombre());
        assertEquals(new BigDecimal("1500"), actualizado.getPrecio());
        assertSame(categoria, actualizado.getCategoria());
        verify(productos).update(producto);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {-1, Integer.MIN_VALUE})
    void rechazaStockInvalidoAntesDeConsultarOModificarElProducto(Integer stock) {
        assertThrows(IllegalArgumentException.class, () -> service.actualizarStock(1L, stock));
        verifyNoInteractions(productos, categorias);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1})
    void rechazaIdentificadorInvalidoAlActualizarStock(Long id) {
        assertThrows(IllegalArgumentException.class, () -> service.actualizarStock(id, 10));
        verifyNoInteractions(productos, categorias);
    }

    @Test
    void rechazaProductoInexistenteSinActualizar() {
        assertThrows(IllegalArgumentException.class, () -> service.actualizarStock(99L, 10));
        verify(productos, never()).update(any());
    }

    @Test
    void listaInventarioIncluyendoProductosSinStockYAdmiteListaVacia() {
        Producto agotado = new Producto("Café", BigDecimal.ONE, 0, new Categoria("Bebidas", null));
        when(productos.findAll()).thenReturn(List.of(agotado)).thenReturn(List.of());

        assertEquals(List.of(agotado), service.listarProductos());
        assertTrue(service.listarProductos().isEmpty());
    }
}
