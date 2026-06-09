package cl.ucn.app.routes;

import cl.ucn.app.controller.TutoriaController;
import io.javalin.config.JavalinConfig;

public class TutoriaRoutes {

    public TutoriaRoutes() {}

    public static void register(JavalinConfig config) {
        TutoriaController tutoriaController = new TutoriaController();


        config.routes.get("/tutorias", tutoriaController::showTutoria);
    }
}
