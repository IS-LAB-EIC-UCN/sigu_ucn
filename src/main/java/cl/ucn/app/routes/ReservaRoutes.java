package cl.ucn.app.routes;

import cl.ucn.app.controller.ReservaController;
import cl.ucn.app.repository.EspacioRepository;
import cl.ucn.app.service.EspacioService;
import io.javalin.config.JavalinConfig;

public final class ReservaRoutes {

    private ReservaRoutes() {
    }

    public static void register(JavalinConfig config) {
        ReservaController reservaController =
                new ReservaController(new EspacioService(new EspacioRepository()));

        config.routes.get("/reservas", reservaController::listarSalas);
    }
}