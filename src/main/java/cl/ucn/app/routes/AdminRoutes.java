package cl.ucn.app.routes;

import cl.ucn.app.controller.AdminReservasController;
import cl.ucn.app.repository.ReservaRepository;
import cl.ucn.app.service.AdminReservaService;
import io.javalin.config.JavalinConfig;

/**
 * Panel de administración del módulo de reservas (Grupo 1).
 * Se registra desde ReservaRoutes y usa el prefijo /reservas/admin para no
 * modificar el panel general ni chocar con rutas de otros módulos.
 */
public final class AdminRoutes {

    private AdminRoutes() {
    }

    public static void register(JavalinConfig config) {
        AdminReservasController controller = new AdminReservasController(
                new AdminReservaService(new ReservaRepository())
        );

        config.routes.get("/reservas/admin/pendientes", controller::listarPendientes);
        config.routes.post("/reservas/admin/{id}/aprobar", controller::aprobar);
        config.routes.post("/reservas/admin/{id}/rechazar", controller::rechazar);
    }
}
