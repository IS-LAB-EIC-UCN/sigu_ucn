package cl.ucn.app.routes;

import cl.ucn.app.controller.InventarioController;
import cl.ucn.app.repository.CategoriaRepository;
import cl.ucn.app.repository.JpaCategoriaRepository;
import cl.ucn.app.repository.JpaProductoRepository;
import cl.ucn.app.repository.JpaProveedorRepository;
import cl.ucn.app.repository.ProductoRepository;
import cl.ucn.app.repository.ProveedorRepository;
import cl.ucn.app.service.InventarioService;
import io.javalin.config.JavalinConfig;

import static cl.ucn.app.controller.ControlAcceso.requiere;
import static cl.ucn.app.util.RolesInventario.SOLO_ADMIN;

/**
 * Rutas del módulo de inventario de laboratorio (Grupo 5).
 * También es la raíz de composición: crea las implementaciones JPA
 * y las inyecta en servicios y controladores.
 */
public final class InventarioRoutes {

    private InventarioRoutes() {
    }

    public static void register(JavalinConfig config) {
        // Persistencia
        ProductoRepository productoRepository = new JpaProductoRepository();
        CategoriaRepository categoriaRepository = new JpaCategoriaRepository();
        ProveedorRepository proveedorRepository = new JpaProveedorRepository();

        // Negocio
        InventarioService inventarioService =
                new InventarioService(productoRepository, categoriaRepository, proveedorRepository);

        // Presentación
        InventarioController inventario = new InventarioController(inventarioService);

        // CU01 - Registrar productos e insumos (administrador)
        config.routes.get("/inventario/productos/nuevo", requiere(SOLO_ADMIN, inventario::formularioProducto));
        config.routes.post("/inventario/productos/nuevo", requiere(SOLO_ADMIN, inventario::registrarProducto));
    }
}