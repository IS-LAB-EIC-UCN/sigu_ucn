package cl.ucn.app.routes;

import cl.ucn.app.controller.DisponibilidadController;
import cl.ucn.app.controller.ReservaController;
import cl.ucn.app.repository.EspacioRepository;
import cl.ucn.app.repository.ReservaRepository;
import cl.ucn.app.repository.UsuarioRepository;
import cl.ucn.app.service.DisponibilidadService;
import cl.ucn.app.service.EspacioService;
import cl.ucn.app.service.ReservaService;
import io.javalin.config.JavalinConfig;

import java.time.Clock;

public final class ReservaRoutes {

    private ReservaRoutes() {
    }

    public static void register(JavalinConfig config) {
        EspacioRepository espacioRepository = new EspacioRepository();
        ReservaRepository reservaRepository = new ReservaRepository();
        ReservaService reservaService = new ReservaService(
                reservaRepository, Clock.systemDefaultZone(), new UsuarioRepository(), espacioRepository);
        ReservaController reservaController =
                new ReservaController(new EspacioService(espacioRepository), reservaService);
        DisponibilidadController disponibilidadController = new DisponibilidadController(
                new DisponibilidadService(reservaRepository, espacioRepository),
                espacioRepository
        );

        config.routes.get("/reservas", reservaController::listarSalas);
        config.routes.get("/reservas/disponibilidad", disponibilidadController::consultar);
        config.routes.post("/reservas/crear", reservaController::crearReserva);
        MisReservasRoutes.register(config);
    }
}