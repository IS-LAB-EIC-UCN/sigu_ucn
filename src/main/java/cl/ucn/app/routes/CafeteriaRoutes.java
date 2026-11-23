package cl.ucn.app.routes;

import cl.ucn.app.controller.CafeteriaController;
import io.javalin.config.JavalinConfig;

// Rutas del módulo Cafetería (Grupo 7), todas bajo /cafeteria.
public final class CafeteriaRoutes {

    private CafeteriaRoutes() {
    }

    public static void register(JavalinConfig config) {
        CafeteriaController controller = new CafeteriaController();

        // Panel y consulta de ventas
        config.routes.get("/cafeteria", controller::showPanel);
        config.routes.get("/cafeteria/ventas", controller::filtrarVentas);

        // Catálogo y stock
        config.routes.post("/cafeteria/categorias", controller::registrarCategoria);
        config.routes.post("/cafeteria/productos", controller::registrarProducto);
        config.routes.post("/cafeteria/productos/{id}/stock", controller::actualizarStock);

        // Pedidos
        config.routes.post("/cafeteria/pedidos", controller::crearPedido);
        config.routes.post("/cafeteria/pedidos/{id}/productos", controller::agregarProductos);
        config.routes.post("/cafeteria/pedidos/{id}/avanzar", controller::avanzarEstado);
        config.routes.post("/cafeteria/pedidos/{id}/anular", controller::anularPedido);
    }
}
