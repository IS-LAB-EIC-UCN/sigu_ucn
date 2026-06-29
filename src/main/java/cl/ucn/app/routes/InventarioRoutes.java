package cl.ucn.app.routes;

import cl.ucn.app.controller.InventarioController;
import cl.ucn.app.repository.InventarioRepository;
import cl.ucn.app.service.InventarioService;
import io.javalin.config.JavalinConfig;

public class InventarioRoutes {

    private static final InventarioController inventarioController =
            new InventarioController(new InventarioService(new InventarioRepository()));

    public InventarioRoutes(InventarioController Controller) {}

    public void register(JavalinConfig config) {
        config.routes.get("/inventario", inventarioController::listarInventario);
    }
}