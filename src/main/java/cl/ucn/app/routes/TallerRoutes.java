package cl.ucn.app.routes;

import cl.ucn.app.controller.TallerController;
import io.javalin.config.JavalinConfig;

public class TallerRoutes {
    private TallerRoutes() {
    }

    public static void register(JavalinConfig config) {
        config.routes.get("/talleres", TallerController::listarTalleres);
        config.routes.post("/talleres", TallerController::registrarTaller);
    }
}
