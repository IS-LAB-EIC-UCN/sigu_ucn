package cl.ucn.app.routes.soporte;

import cl.ucn.app.controller.soporte.SoporteController;
import io.javalin.config.JavalinConfig;

public class SoporteRoutes {
    private SoporteRoutes(){}

    public static void register(JavalinConfig config) {
        SoporteController controller = new SoporteController();
        config.routes.get("/soporte", controller::mostrarInicio);
        config.routes.post("/api/soporte/tickets", controller::crearTicket);
    }
}
