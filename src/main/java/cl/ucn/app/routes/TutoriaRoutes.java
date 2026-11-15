package cl.ucn.app.routes;

import cl.ucn.app.controller.AgendaController;
import cl.ucn.app.controller.TutoriaController;
import cl.ucn.app.controller.TutoriaVistaController;
import cl.ucn.app.repository.EstudianteRepositoryImpl;
import cl.ucn.app.repository.InscripcionTutoriaRepositoryImpl;
import cl.ucn.app.repository.TutoriaRepositoryImpl;
import cl.ucn.app.repository.AsignaturaRepositoryImpl;
import cl.ucn.app.repository.TutorRepositoryImpl;
import cl.ucn.app.service.AgendaService;
import cl.ucn.app.service.TutoriaBusquedaService;
import cl.ucn.app.util.AuthMiddleware;
import io.javalin.config.JavalinConfig;

public final class TutoriaRoutes {

    private TutoriaRoutes() {
    }

    public static void register(JavalinConfig config) {
        TutoriaRepositoryImpl tutoriaRepository = new TutoriaRepositoryImpl();
        InscripcionTutoriaRepositoryImpl inscripcionRepository = new InscripcionTutoriaRepositoryImpl();
        TutoriaController tutoriaController = new TutoriaController(
                new TutoriaBusquedaService(tutoriaRepository, inscripcionRepository),
                new AsignaturaRepositoryImpl(),
                new TutorRepositoryImpl());
        TutoriaVistaController vistaController = new TutoriaVistaController(
                tutoriaRepository, inscripcionRepository, new TutorRepositoryImpl());
        AgendaController agendaController = new AgendaController(
                new AgendaService(new EstudianteRepositoryImpl(), inscripcionRepository, tutoriaRepository));

        config.routes.before("/tutorias", AuthMiddleware::requireLogin);
        config.routes.before("/tutorias/*", AuthMiddleware::requireLogin);
        config.routes.get("/tutorias", tutoriaController::listar);
        config.routes.get("/tutorias/agenda", agendaController::mostrar);
        config.routes.before("/tutorias/publicar", AuthMiddleware::requireTutor);
        config.routes.get("/tutorias/publicar", vistaController::mostrarPublicar);
        // Debe ir después de las rutas fijas (/agenda, /publicar) para no capturarlas.
        config.routes.get("/tutorias/{id}", vistaController::detalle);
    }
}
