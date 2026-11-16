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
import cl.ucn.app.controller.PrestamoController;
import cl.ucn.app.repository.JpaPrestamoRepository;
import cl.ucn.app.repository.PrestamoRepository;
import cl.ucn.app.service.PrestamoService;

import static cl.ucn.app.controller.ControlAcceso.requiere;
import static cl.ucn.app.util.RolesInventario.SOLO_ADMIN;
import static cl.ucn.app.util.RolesInventario.TODOS;
import static cl.ucn.app.util.RolesInventario.GESTION;
import static cl.ucn.app.util.RolesInventario.SOLICITANTES;

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

        // CU05 - Consultar inventario (todos los roles)
        config.routes.get("/inventario", requiere(TODOS, inventario::lista));

        // CU01 - Registrar productos e insumos (administrador)
        config.routes.get("/inventario/productos/nuevo", requiere(SOLO_ADMIN, inventario::formularioProducto));
        config.routes.post("/inventario/productos/nuevo", requiere(SOLO_ADMIN, inventario::registrarProducto));

        // Persistencia y negocio de préstamos
        PrestamoRepository prestamoRepository = new JpaPrestamoRepository();
        PrestamoService prestamoService = new PrestamoService(prestamoRepository);
        PrestamoController prestamos = new PrestamoController(prestamoService);

        // CU03 - Solicitar préstamo de equipo (docente / estudiante)
        config.routes.get("/inventario/prestamos", requiere(SOLICITANTES, prestamos::formularioPrestamo));
        config.routes.post("/inventario/prestamos", requiere(SOLICITANTES, prestamos::solicitarPrestamo));

        // CU04 - Registrar devolución (administrador / encargado)
        config.routes.get("/inventario/devoluciones", requiere(GESTION, prestamos::listaDevoluciones));
        config.routes.post("/inventario/devoluciones", requiere(GESTION, prestamos::registrarDevolucion));
    }
}