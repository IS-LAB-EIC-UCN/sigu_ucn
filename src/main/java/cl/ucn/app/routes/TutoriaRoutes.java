package cl.ucn.app.routes;

import cl.ucn.app.controller.AgendaController;
import cl.ucn.app.controller.TutoriaController;
import cl.ucn.app.repository.EstudianteRepositoryImpl;
import cl.ucn.app.repository.InscripcionTutoriaRepositoryImpl;
import cl.ucn.app.repository.TutoriaRepositoryImpl;
import cl.ucn.app.service.AgendaService;
import cl.ucn.app.service.ReservaTutoriaService;
import cl.ucn.app.service.TutoriaService;
import cl.ucn.app.util.AuthMiddleware;
import io.javalin.config.JavalinConfig;

public final class TutoriaRoutes {

    private TutoriaRoutes() {
    }

    public static void register(JavalinConfig config) {
        TutoriaController controller = new TutoriaController(new TutoriaService(), new ReservaTutoriaService());
        AgendaController agendaController = new AgendaController(
                new AgendaService(new EstudianteRepositoryImpl(),
                        new InscripcionTutoriaRepositoryImpl(), new TutoriaRepositoryImpl()));

        config.routes.before("/tutorias", AuthMiddleware::requireLogin);
        config.routes.before("/tutorias/*", AuthMiddleware::requireLogin);
        config.routes.before("/tutorias/publicar", AuthMiddleware::requireTutor);
        config.routes.before("/tutorias/{id}/reservar", AuthMiddleware::requireLogin);

        config.routes.get("/tutorias", controller::listar);
        config.routes.get("/tutorias/agenda", agendaController::mostrar);
        config.routes.get("/tutorias/publicar", controller::mostrarPublicar);
        config.routes.post("/tutorias", controller::publicar);
        config.routes.get("/tutorias/{id}", controller::detalle);
        config.routes.post("/tutorias/{id}/reservar", controller::reservar);
    }
}
