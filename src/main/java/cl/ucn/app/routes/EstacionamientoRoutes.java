
package cl.ucn.app.routes;

import cl.ucn.app.controller.AdminEstacionamientoController;
import cl.ucn.app.controller.EstacionamientoController;
import cl.ucn.app.controller.ReservaEstacionamientoController;
import io.javalin.config.JavalinConfig;

public final class EstacionamientoRoutes {

    private EstacionamientoRoutes() {
    }

    public static void register(JavalinConfig config) {
        register(config, new AdminEstacionamientoController(), new ReservaEstacionamientoController());
    }

    static void register(JavalinConfig config, AdminEstacionamientoController adminController,
            ReservaEstacionamientoController reservas) {
        EstacionamientoController controller =
                new EstacionamientoController();

        config.routes.post(
                "/estacionamientos/reservar",
                controller::registrarReserva
        );

        config.routes.get("/estacionamientos/admin", adminController::mostrarCatalogo);
        config.routes.post("/estacionamientos/admin", adminController::registrar);
        config.routes.post("/estacionamientos/admin/{id}/habilitacion", adminController::cambiarHabilitacion);
        config.routes.get("/estacionamientos/admin/reservas", reservas::mostrarAdministracion);
        config.routes.post("/estacionamientos/admin/reservas/{id}/cancelar", reservas::cancelarComoAdministrador);
        config.routes.post("/estacionamientos/reservas/{id}/cancelar", reservas::cancelarPropia);
    }
}
