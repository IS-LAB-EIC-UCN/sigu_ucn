package cl.ucn.app.service;

import cl.ucn.app.model.Pedido;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidos;
    @Mock
    private Usuario cajero;
    private PedidoService service;

    @BeforeEach
    void setUp() {
        service = new PedidoService(pedidos);
        lenient().when(cajero.getId()).thenReturn(1L);
    }

    @Test
    void crearPedidoExigeUsuarioConSesion() {
        assertThrows(IllegalArgumentException.class, () -> service.crearPedido(null, Map.of(1L, 1)));
        assertThrows(IllegalArgumentException.class, () -> service.crearPedido(new Usuario(), Map.of(1L, 1)));
    }

    @Test
    void crearPedidoExigeAlMenosUnProducto() {
        assertThrows(IllegalArgumentException.class, () -> service.crearPedido(cajero, null));
        assertThrows(IllegalArgumentException.class, () -> service.crearPedido(cajero, Map.of()));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -2})
    void crearPedidoRechazaCantidadNoPositiva(int cantidad) {
        assertThrows(IllegalArgumentException.class, () -> service.crearPedido(cajero, Map.of(1L, cantidad)));
    }

    @Test
    void crearPedidoRechazaIdDeProductoInvalido() {
        Map<Long, Integer> conNulo = new HashMap<>();
        conNulo.put(null, 1);
        assertThrows(IllegalArgumentException.class, () -> service.crearPedido(cajero, conNulo));
        assertThrows(IllegalArgumentException.class, () -> service.crearPedido(cajero, Map.of(0L, 1)));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1})
    void operacionesSobrePedidoRechazanIdInvalido(Long id) {
        assertThrows(IllegalArgumentException.class, () -> service.agregarProductos(id, Map.of(1L, 1)));
        assertThrows(IllegalArgumentException.class, () -> service.avanzarEstado(id));
        assertThrows(IllegalArgumentException.class, () -> service.anularPedido(id));
        assertThrows(IllegalArgumentException.class, () -> service.buscarPedido(id));
    }

    @Test
    void buscarPedidoInexistenteLanzaError() {
        when(pedidos.findById(99L)).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> service.buscarPedido(99L));
    }

    @Test
    void filtrarVentasRechazaFechasNulasOInvertidas() {
        LocalDate hoy = LocalDate.of(2026, 11, 15);
        assertThrows(IllegalArgumentException.class, () -> service.filtrarVentas(null, hoy));
        assertThrows(IllegalArgumentException.class, () -> service.filtrarVentas(hoy, null));
        assertThrows(IllegalArgumentException.class, () -> service.filtrarVentas(hoy, hoy.minusDays(1)));
        verifyNoInteractions(pedidos);
    }

    @Test
    void filtrarVentasIncluyeDiasCompletosYExcluyeAnulados() {
        LocalDate dia = LocalDate.of(2026, 11, 15);
        Pedido vendido = new Pedido();
        Pedido anulado = new Pedido();
        anulado.setEstado("ANULADO");
        LocalDateTime inicio = LocalDateTime.of(2026, 11, 15, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 11, 15, 23, 59, 59, 999_999_000);
        when(pedidos.findByFechaBetween(inicio, fin)).thenReturn(List.of(vendido, anulado));

        assertEquals(List.of(vendido), service.filtrarVentas(dia, dia));
    }
}
