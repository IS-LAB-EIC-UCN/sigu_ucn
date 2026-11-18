package cl.ucn.app.service;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Categoria;
import cl.ucn.app.model.DetallePedido;
import cl.ucn.app.model.Pedido;
import cl.ucn.app.model.Producto;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.ProductoRepository;
import cl.ucn.app.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas reales: docker compose up -d, luego mvn "-Dcafeteria.integration=true" test. */
@EnabledIfSystemProperty(named = "cafeteria.integration", matches = "true")
class PedidoPostgresTest {

    private final PedidoService pedidos = new PedidoService();
    private final ProductoService productos = new ProductoService();
    private final ProductoRepository productoRepository = new ProductoRepository();
    private final String nombreCategoria = "Prueba-Bloque2-" + UUID.randomUUID();
    private Usuario cajero;
    private Producto cafe;
    private Producto sandwich;

    @BeforeEach
    void crearDatosDePrueba() {
        cajero = new UsuarioRepository().findByCorreo("cajero@sigu.cl");
        assertNotNull(cajero, "Falta el usuario precargado cajero@sigu.cl (db/init/04_cafeteria_seed.sql)");
        Categoria categoria = new CategoriaService().registrarCategoria(nombreCategoria, null);
        cafe = productos.registrarProducto("Café prueba", new BigDecimal("1500"), 5, categoria.getId());
        sandwich = productos.registrarProducto("Sándwich prueba", new BigDecimal("2500"), 3, categoria.getId());
    }

    @AfterEach
    void eliminarSoloLosDatosCreadosPorEstaPrueba() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            // detalle_pedido se borra en cascada desde la base de datos
            em.createQuery("DELETE FROM Pedido p WHERE p.id IN (SELECT d.pedido.id FROM DetallePedido d "
                            + "WHERE d.producto.categoria.nombre = :nombre)")
                    .setParameter("nombre", nombreCategoria).executeUpdate();
            em.createQuery("DELETE FROM Producto p WHERE p.categoria.id IN "
                            + "(SELECT c.id FROM Categoria c WHERE c.nombre = :nombre)")
                    .setParameter("nombre", nombreCategoria).executeUpdate();
            em.createQuery("DELETE FROM Categoria c WHERE c.nombre = :nombre")
                    .setParameter("nombre", nombreCategoria).executeUpdate();
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    private int stock(Producto producto) {
        return productoRepository.findById(producto.getId()).getStock();
    }

    private DetallePedido detalle(Pedido pedido, Producto producto) {
        return pedido.getDetalles().stream()
                .filter(d -> d.getProducto().getId().equals(producto.getId())).findFirst().orElseThrow();
    }

    @Test
    void crearPedidoDescuentaStockGuardaPrecioYCalculaTotal() {
        Pedido creado = pedidos.crearPedido(cajero, Map.of(cafe.getId(), 2, sandwich.getId(), 1));

        Pedido leido = pedidos.buscarPedido(creado.getId());
        assertEquals("PENDIENTE", leido.getEstado());
        assertEquals(0, new BigDecimal("5500").compareTo(leido.getTotal()));
        assertEquals(0, new BigDecimal("1500").compareTo(detalle(leido, cafe).getPrecioRegistrado()));
        assertEquals(cajero.getId(), leido.getUsuario().getId());
        assertEquals(3, stock(cafe));
        assertEquals(2, stock(sandwich));
    }

    @Test
    void stockInsuficienteNoCambiaNada() {
        // el café alcanza y se procesa primero: si no hubiera rollback, su stock quedaría descontado
        assertThrows(IllegalArgumentException.class,
                () -> pedidos.crearPedido(cajero, Map.of(cafe.getId(), 1, sandwich.getId(), 4)));

        assertEquals(5, stock(cafe));
        assertEquals(3, stock(sandwich));
    }

    @Test
    void agregarProductoExistenteSumaCantidadYMantienePrecioOriginal() {
        Pedido pedido = pedidos.crearPedido(cajero, Map.of(cafe.getId(), 1));
        Producto conNuevoPrecio = productoRepository.findById(cafe.getId());
        conNuevoPrecio.setPrecio(new BigDecimal("2000"));
        productoRepository.update(conNuevoPrecio);

        pedidos.agregarProductos(pedido.getId(), Map.of(cafe.getId(), 2, sandwich.getId(), 1));

        Pedido leido = pedidos.buscarPedido(pedido.getId());
        assertEquals(3, detalle(leido, cafe).getCantidad());
        assertEquals(0, new BigDecimal("1500").compareTo(detalle(leido, cafe).getPrecioRegistrado()));
        assertEquals(0, new BigDecimal("7000").compareTo(leido.getTotal()));
        assertEquals(2, stock(cafe));
        assertEquals(2, stock(sandwich));
    }

    @Test
    void agregarProductosSoloEnPedidoPendiente() {
        Pedido pedido = pedidos.crearPedido(cajero, Map.of(cafe.getId(), 1));
        pedidos.avanzarEstado(pedido.getId());

        assertThrows(IllegalArgumentException.class,
                () -> pedidos.agregarProductos(pedido.getId(), Map.of(cafe.getId(), 1)));
        assertEquals(4, stock(cafe));
    }

    @Test
    void avanzarEstadoRecorreElFlujoYNoPasaDeEntregado() {
        Pedido pedido = pedidos.crearPedido(cajero, Map.of(cafe.getId(), 1));

        assertEquals("EN_PREPARACION", pedidos.avanzarEstado(pedido.getId()).getEstado());
        assertEquals("LISTO", pedidos.avanzarEstado(pedido.getId()).getEstado());
        assertEquals("ENTREGADO", pedidos.avanzarEstado(pedido.getId()).getEstado());
        assertThrows(IllegalArgumentException.class, () -> pedidos.avanzarEstado(pedido.getId()));
    }

    @Test
    void anularDevuelveStockYQuedaFueraDeLasVentas() {
        Pedido pedido = pedidos.crearPedido(cajero, Map.of(cafe.getId(), 2, sandwich.getId(), 3));

        assertEquals("ANULADO", pedidos.anularPedido(pedido.getId()).getEstado());
        assertEquals(5, stock(cafe));
        assertEquals(3, stock(sandwich));
        assertThrows(IllegalArgumentException.class, () -> pedidos.anularPedido(pedido.getId()));
        assertThrows(IllegalArgumentException.class, () -> pedidos.avanzarEstado(pedido.getId()));
        LocalDate hoy = LocalDate.now();
        assertTrue(pedidos.filtrarVentas(hoy, hoy).stream().noneMatch(p -> p.getId().equals(pedido.getId())));
        assertTrue(pedidos.listarHistorial().stream().anyMatch(p -> p.getId().equals(pedido.getId())));
    }

    @Test
    void noSePuedeAnularPedidoEntregado() {
        Pedido pedido = pedidos.crearPedido(cajero, Map.of(cafe.getId(), 1));
        pedidos.avanzarEstado(pedido.getId());
        pedidos.avanzarEstado(pedido.getId());
        pedidos.avanzarEstado(pedido.getId());

        assertThrows(IllegalArgumentException.class, () -> pedidos.anularPedido(pedido.getId()));
        assertEquals(4, stock(cafe));
    }
}
