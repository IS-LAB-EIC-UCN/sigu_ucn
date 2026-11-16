package cl.ucn.app.routes;

import cl.ucn.app.controller.AdminReservasController;
import cl.ucn.app.repository.ReservaRepository;
import cl.ucn.app.service.AdminReservaService;
import io.javalin.config.JavalinConfig;

public final class AdminRoutes {

    private AdminRoutes() {
    }

    public static void register(JavalinConfig config) {
        AdminReservasController controller = new AdminReservasController(
                new AdminReservaService(new ReservaRepository())
        );

        config.routes.get("/admin/reservas/pendientes", controller::listarPendientes);
        config.routes.post("/admin/reservas/{id}/aprobar", controller::aprobar);
        config.routes.post("/admin/reservas/{id}/rechazar", controller::rechazar);
    }
}
