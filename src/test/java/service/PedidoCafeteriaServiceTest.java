package cl.ucn.app.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.ucn.app.model.DetallePedidoCafeteria;
import cl.ucn.app.model.PedidoCafeteria;
import cl.ucn.app.model.ProductoCafeteria;
import cl.ucn.app.repository.DetallePedidoCafeteriaRepository;
import cl.ucn.app.repository.PedidoCafeteriaRepository;
import cl.ucn.app.repository.ProductoCafeteriaRepository;

public class PedidoCafeteriaServiceTest {

    private PedidoCafeteriaRepository pedidoRepository;
    private ProductoCafeteriaRepository productoRepository;
    private DetallePedidoCafeteriaRepository detalleRepository;
    private PedidoCafeteriaService pedidoService;

    @BeforeEach
    public void setUp() {
        pedidoRepository = Mockito.mock(PedidoCafeteriaRepository.class);
        productoRepository = Mockito.mock(ProductoCafeteriaRepository.class);
        detalleRepository = Mockito.mock(DetallePedidoCafeteriaRepository.class);

        pedidoService = new PedidoCafeteriaService(
                pedidoRepository,
                productoRepository,
                detalleRepository
        );
    }

    @Test
    public void calcularSubtotal_debeMultiplicarPrecioPorCantidad() {
        ProductoCafeteria producto = new ProductoCafeteria();
        producto.setNombre("Café");
        producto.setPrecio(new BigDecimal("1200"));
        producto.setStock(10);

        BigDecimal subtotal = pedidoService.calcularSubtotal(producto, 3);

        assertEquals(new BigDecimal("3600"), subtotal);
    }

    @Test
    public void calcularSubtotal_debeLanzarErrorSiProductoEsNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> pedidoService.calcularSubtotal(null, 2)
        );
    }

    @Test
    public void calcularSubtotal_debeLanzarErrorSiCantidadEsCero() {
        ProductoCafeteria producto = new ProductoCafeteria();
        producto.setNombre("Té");
        producto.setPrecio(new BigDecimal("1000"));
        producto.setStock(10);

        assertThrows(
                IllegalArgumentException.class,
                () -> pedidoService.calcularSubtotal(producto, 0)
        );
    }

    @Test
    public void validarStock_noDebeLanzarErrorSiHayStockSuficiente() {
        ProductoCafeteria producto = new ProductoCafeteria();
        producto.setNombre("Pan con palta");
        producto.setPrecio(new BigDecimal("2500"));
        producto.setStock(5);

        assertDoesNotThrow(() -> pedidoService.validarStock(producto, 3));
    }

    @Test
    public void validarStock_debeLanzarErrorSiNoHayStockSuficiente() {
        ProductoCafeteria producto = new ProductoCafeteria();
        producto.setNombre("Galleta");
        producto.setPrecio(new BigDecimal("1500"));
        producto.setStock(2);

        assertThrows(
                IllegalArgumentException.class,
                () -> pedidoService.validarStock(producto, 5)
        );
    }

    @Test
    public void crearPedidoConVariosProductos_debeCrearPedidoYDescontarStock() {
        ProductoCafeteria producto1 = new ProductoCafeteria();
        producto1.setNombre("Café");
        producto1.setPrecio(new BigDecimal("1200"));
        producto1.setStock(10);

        ProductoCafeteria producto2 = new ProductoCafeteria();
        producto2.setNombre("Galleta");
        producto2.setPrecio(new BigDecimal("1500"));
        producto2.setStock(8);

        when(productoRepository.buscarPorId(1L)).thenReturn(producto1);
        when(productoRepository.buscarPorId(2L)).thenReturn(producto2);

        PedidoCafeteria pedido = pedidoService.crearPedidoConVariosProductos(
                List.of(1L, 2L),
                List.of(2, 1)
        );

        assertEquals(new BigDecimal("3900"), pedido.getTotal());

        verify(pedidoRepository, times(1)).guardar(pedido);
        verify(detalleRepository, times(2)).guardar(Mockito.any(DetallePedidoCafeteria.class));
        verify(productoRepository, times(2)).actualizar(Mockito.any(ProductoCafeteria.class));

        assertEquals(8, producto1.getStock());
        assertEquals(7, producto2.getStock());
    }

    @Test
    public void crearPedidoConVariosProductos_debeLanzarErrorSiNoTieneProductos() {
        assertThrows(
                IllegalArgumentException.class,
                () -> pedidoService.crearPedidoConVariosProductos(
                        List.of(),
                        List.of()
                )
        );
    }

    @Test
    public void crearPedidoConVariosProductos_debeLanzarErrorSiListasNoCoinciden() {
        assertThrows(
                IllegalArgumentException.class,
                () -> pedidoService.crearPedidoConVariosProductos(
                        List.of(1L, 2L),
                        List.of(2)
                )
        );
    }

    @Test
    public void cambiarEstado_debeActualizarPedidoConEstadoValido() {
        PedidoCafeteria pedido = new PedidoCafeteria(
                LocalDateTime.now(),
                "PENDIENTE",
                new BigDecimal("1200")
        );

        when(pedidoRepository.buscarPorId(1L)).thenReturn(pedido);

        PedidoCafeteria resultado = pedidoService.cambiarEstado(1L, "ENTREGADO");

        assertEquals("ENTREGADO", resultado.getEstado());
        verify(pedidoRepository, times(1)).actualizar(pedido);
    }

    @Test
    public void cambiarEstado_debeLanzarErrorConEstadoInvalido() {
        PedidoCafeteria pedido = new PedidoCafeteria(
                LocalDateTime.now(),
                "PENDIENTE",
                new BigDecimal("1200")
        );

        when(pedidoRepository.buscarPorId(1L)).thenReturn(pedido);

        assertThrows(
                IllegalArgumentException.class,
                () -> pedidoService.cambiarEstado(1L, "ESTADO_MALO")
        );
    }

    @Test
    public void anularPedido_debeLanzarErrorSiPedidoEstaEntregado() {
        PedidoCafeteria pedido = new PedidoCafeteria(
                LocalDateTime.now(),
                "ENTREGADO",
                new BigDecimal("2500")
        );

        when(pedidoRepository.buscarPorId(1L)).thenReturn(pedido);

        assertThrows(
                IllegalArgumentException.class,
                () -> pedidoService.anularPedido(1L)
        );
    }

    @Test
    public void anularPedido_debeCambiarEstadoAAnuladoSiNoEstaEntregado() {
        PedidoCafeteria pedido = new PedidoCafeteria(
                LocalDateTime.now(),
                "PENDIENTE",
                new BigDecimal("2500")
        );

        when(pedidoRepository.buscarPorId(1L)).thenReturn(pedido);

        PedidoCafeteria resultado = pedidoService.anularPedido(1L);

        assertEquals("ANULADO", resultado.getEstado());
        verify(pedidoRepository, times(1)).actualizar(pedido);
    }
}