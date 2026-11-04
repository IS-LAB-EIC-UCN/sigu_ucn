
package cl.ucn.app.routes;

import cl.ucn.app.controller.EventosController;
import io.javalin.config.JavalinConfig;

public final class EventosRoutes {

    private EventosRoutes() {}

    public static void register(JavalinConfig config) {

        EventosController controller =
                new EventosController();

        config.routes.get(
                "/eventos",
                controller::mostrarEventos
        );
    }
}
