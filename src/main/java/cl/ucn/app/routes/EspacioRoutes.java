package cl.ucn.app.routes;

import cl.ucn.app.controller.EspacioController;
import io.javalin.config.JavalinConfig;

public final class EspacioRoutes {
    private EspacioRoutes() {}

    public static void register(JavalinConfig config) {
        EspacioController espacioController = new EspacioController();
        config.routes.get("/espacios", espacioController::listar);
        config.routes.get("/espacios/registrar", espacioController::mostrarFormularioCrear);
        config.routes.post("/espacios/registrar", espacioController::registrar);
        config.routes.get("/espacios/editar/{id}", espacioController::mostrarFormularioEditar);
        config.routes.post("/espacios/actualizar", espacioController::actualizar);
        config.routes.post("/espacios/eliminar", espacioController::eliminar);
    }
}
