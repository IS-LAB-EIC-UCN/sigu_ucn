package cl.ucn.app.routes;

import cl.ucn.app.controller.TutoriaController;
import io.javalin.config.JavalinConfig;

public final class TutoriaRoutes {

    private TutoriaRoutes() {
    }

    public static void register(JavalinConfig config) {

        TutoriaController tutoriaController = new TutoriaController();

        config.routes.get("/tutorias", tutoriaController::listar);
        config.routes.get("/tutorias/crear", tutoriaController::crear);

        config.routes.post("/tutorias/guardar", tutoriaController::guardar);
        config.routes.post("/tutorias/reservar", tutoriaController::reservar);
        config.routes.post("/tutorias/cancelar", tutoriaController::cancelar);
        config.routes.post("/tutorias/asistencia", tutoriaController::registrarAsistencia);

        config.routes.post("/tutorias/asignaturas", tutoriaController::crearAsignatura);
    }
}