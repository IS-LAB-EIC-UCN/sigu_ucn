package cl.ucn.app.routes;

import cl.ucn.app.controller.ExpositorController;
import io.javalin.config.JavalinConfig;

public final class ExpositorRoutes {
    private ExpositorRoutes() {}

    public static void register(JavalinConfig config) {
        ExpositorController expositorController = new ExpositorController();
        config.routes.get("/expositores", expositorController::listar);
        config.routes.get("/expositores/registrar", expositorController::mostrarFormulario);
        config.routes.post("/expositores/registrar", expositorController::registrar);
    }
}
