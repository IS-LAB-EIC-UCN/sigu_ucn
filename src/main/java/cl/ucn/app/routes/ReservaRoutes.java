package cl.ucn.app.routes;

import cl.ucn.app.controller.DisponibilidadController;
import cl.ucn.app.controller.ReservaController;
import cl.ucn.app.repository.EspacioRepository;
import cl.ucn.app.repository.ReservaRepository;
import cl.ucn.app.service.DisponibilidadService;
import cl.ucn.app.service.EspacioService;
import io.javalin.config.JavalinConfig;

public final class ReservaRoutes {

    private ReservaRoutes() {
    }

    public static void register(JavalinConfig config) {
        EspacioRepository espacioRepository = new EspacioRepository();
        ReservaController reservaController =
                new ReservaController(new EspacioService(espacioRepository));
        DisponibilidadController disponibilidadController = new DisponibilidadController(
                new DisponibilidadService(new ReservaRepository(), espacioRepository),
                espacioRepository
        );

        config.routes.get("/reservas", reservaController::listarSalas);
        config.routes.get("/reservas/disponibilidad", disponibilidadController::consultar);
        MisReservasRoutes.register(config);
    }
}