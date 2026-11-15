package cl.ucn.app.routes;

import cl.ucn.app.controller.MisReservasController;
import cl.ucn.app.repository.EspacioRepository;
import cl.ucn.app.repository.ReservaRepository;
import cl.ucn.app.service.EspacioService;
import cl.ucn.app.service.ReservaService;
import io.javalin.config.JavalinConfig;

import java.time.Clock;

public final class MisReservasRoutes {
    private MisReservasRoutes() {
    }
    public static void register(JavalinConfig config) {
        MisReservasController controller = new MisReservasController(
                new EspacioService(new EspacioRepository()),
                new ReservaService(new ReservaRepository(), Clock.systemDefaultZone())
        );
        config.routes.get("/reservas/mis-reservas", controller::misReservas);
        config.routes.post("/reservas/{id}/cancelar", controller::cancelar);
    }
}