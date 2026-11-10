
package cl.ucn.app.routes;

import cl.ucn.app.controller.InscripcionesController;
import io.javalin.config.JavalinConfig;

public final class InscripcionesRoutes {

    private InscripcionesRoutes() {
    }

    public static void register(JavalinConfig config) {

        InscripcionesController controller = new InscripcionesController();

        config.routes.post(
                "/eventos/{id}/inscribir",
                controller::inscribir);
    }
}
