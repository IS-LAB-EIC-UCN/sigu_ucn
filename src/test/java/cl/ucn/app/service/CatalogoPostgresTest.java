package cl.ucn.app.service;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Categoria;
import cl.ucn.app.model.Producto;
import cl.ucn.app.repository.CategoriaRepository;
import cl.ucn.app.repository.ProductoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas reales: docker compose up -d, luego mvn -Dcafeteria.integration=true test. */
@EnabledIfSystemProperty(named = "cafeteria.integration", matches = "true")
class CatalogoPostgresTest {

    private final CategoriaService categorias = new CategoriaService();
    private final ProductoService productos = new ProductoService();
    private final String nombreCategoria = "Prueba-Bloque1-" + UUID.randomUUID();

    @AfterEach
    void eliminarSoloLosDatosCreadosPorEstaPrueba() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
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

    @Test
    void persisteCategoriaYRechazaDuplicadoConMayusculasYEspacios() {
        Categoria categoria = categorias.registrarCategoria("  " + nombreCategoria + "  ", "Integración");

        assertNotNull(categoria.getId());
        Categoria leida = new CategoriaRepository().findById(categoria.getId());
        assertEquals(nombreCategoria, leida.getNombre());
        assertEquals("Integración", leida.getDescripcion());
        assertThrows(IllegalArgumentException.class, () -> categorias.registrarCategoria(
                "  " + nombreCategoria.toUpperCase(Locale.ROOT) + "  ", null));
        assertEquals(1, categorias.listarCategorias().stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(nombreCategoria)).count());
    }

    @Test
    void persisteProductoConPrecioDecimalYCategoriaYRechazaDatosInvalidos() {
        Categoria categoria = categorias.registrarCategoria(nombreCategoria, null);
        Producto producto = productos.registrarProducto("  Café de prueba  ",
                new BigDecimal("1500.50"), 0, categoria.getId());

        assertNotNull(producto.getId());
        Producto leido = new ProductoRepository().findById(producto.getId());
        assertEquals("Café de prueba", leido.getNombre());
        assertEquals(new BigDecimal("1500.50"), leido.getPrecio());
        assertEquals(0, leido.getStock());
        assertEquals(categoria.getId(), leido.getCategoria().getId());
        assertEquals(nombreCategoria, leido.getCategoria().getNombre());
        assertTrue(productos.listarProductos().stream().anyMatch(p -> p.getId().equals(producto.getId())));

        assertThrows(IllegalArgumentException.class, () -> productos.registrarProducto(
                "Inválido", new BigDecimal("-0.01"), 1, categoria.getId()));
        assertThrows(IllegalArgumentException.class, () -> productos.registrarProducto(
                "Inválido", BigDecimal.ONE, -1, categoria.getId()));
        assertThrows(IllegalArgumentException.class, () -> productos.registrarProducto(
                "Inválido", BigDecimal.ONE, 1, Long.MAX_VALUE));
        assertEquals(1, productos.listarProductos().stream()
                .filter(p -> p.getCategoria().getId().equals(categoria.getId())).count());
    }

    @Test
    void actualizaStockPersistidoYConservaElAnteriorAnteUnValorInvalido() {
        Categoria categoria = categorias.registrarCategoria(nombreCategoria, null);
        Producto producto = productos.registrarProducto("Producto de prueba", BigDecimal.ZERO, 10, categoria.getId());
        ProductoRepository repository = new ProductoRepository();

        productos.actualizarStock(producto.getId(), 15);
        assertEquals(15, repository.findById(producto.getId()).getStock());
        assertThrows(IllegalArgumentException.class, () -> productos.actualizarStock(producto.getId(), -1));
        assertEquals(15, repository.findById(producto.getId()).getStock());
        productos.actualizarStock(producto.getId(), 0);
        assertEquals(0, repository.findById(producto.getId()).getStock());
        assertThrows(IllegalArgumentException.class, () -> productos.actualizarStock(Long.MAX_VALUE, 1));
    }
}
