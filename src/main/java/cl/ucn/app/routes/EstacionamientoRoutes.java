
package cl.ucn.app.routes;

import cl.ucn.app.controller.AdminEstacionamientoController;
import cl.ucn.app.controller.EstacionamientoController;
import io.javalin.config.JavalinConfig;

public final class EstacionamientoRoutes {

    private EstacionamientoRoutes() {
    }

    public static void register(JavalinConfig config) {

        EstacionamientoController controller =
                new EstacionamientoController();

        config.routes.post(
                "/estacionamientos/reservar",
                controller::registrarReserva
        );

        AdminEstacionamientoController adminController = new AdminEstacionamientoController();
        config.routes.get("/estacionamientos/admin", adminController::mostrarCatalogo);
        config.routes.post("/estacionamientos/admin", adminController::registrar);
        config.routes.post("/estacionamientos/admin/{id}/habilitacion", adminController::cambiarHabilitacion);
    }
}
