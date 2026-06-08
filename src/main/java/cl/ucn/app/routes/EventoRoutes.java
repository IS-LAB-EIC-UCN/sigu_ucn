package cl.ucn.app.routes;

import cl.ucn.app.controller.EventoController;
import io.javalin.config.JavalinConfig;

public final class EventoRoutes {

    private EventoRoutes() {}

    public static void register(JavalinConfig config) {
        EventoController eventoController = new EventoController();

        config.routes.get("/eventos", eventoController::listar);
        config.routes.get("/eventos/registrar", eventoController::mostrarFormulario);
        config.routes.post("/eventos/registrar", eventoController::registrar);
        config.routes.post("/eventos/cancelar", eventoController::cancelar);
        config.routes.post("/eventos/eliminar", eventoController::eliminar);
    }
}
