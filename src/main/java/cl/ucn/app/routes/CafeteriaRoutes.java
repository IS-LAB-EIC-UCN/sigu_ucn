package cl.ucn.app.routes;

import cl.ucn.app.controller.CafeteriaController;
import io.javalin.config.JavalinConfig;

public class CafeteriaRoutes {

    public static void register(JavalinConfig config) {
        CafeteriaController controller = new CafeteriaController();

        config.routes.get("/cafeteria", ctx -> controller.mostrarCafeteria(ctx));

        config.routes.post("/cafeteria/categorias", ctx -> controller.registrarCategoria(ctx));
        config.routes.post("/cafeteria/productos", ctx -> controller.registrarProducto(ctx));
        config.routes.post("/cafeteria/productos/stock", ctx -> controller.agregarStock(ctx));

        config.routes.post("/cafeteria/pedidos", ctx -> controller.crearPedido(ctx));
        config.routes.post("/cafeteria/pedidos/estado", ctx -> controller.cambiarEstadoPedido(ctx));
        config.routes.post("/cafeteria/pedidos/anular", ctx -> controller.anularPedido(ctx));
        config.routes.post("/cafeteria/pedidos/eliminar", ctx -> controller.eliminarPedido(ctx));
    }
}