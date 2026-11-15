package cl.ucn.app.routes;

import cl.ucn.app.controller.TutoriaController;
import cl.ucn.app.service.ReservaTutoriaService;
import cl.ucn.app.service.TutoriaService;
import io.javalin.config.JavalinConfig;

public final class TutoriaRoutes {

    private TutoriaRoutes() {
    }

    public static void register(JavalinConfig config) {
        TutoriaController controller = new TutoriaController(new TutoriaService(), new ReservaTutoriaService());

        config.routes.before("/tutorias", controller::verificarSesion);
        config.routes.before("/tutorias/*", controller::verificarSesion);

        config.routes.get("/tutorias", controller::listar);
        // /tutorias/publicar debe registrarse ANTES que /tutorias/{id}.
        config.routes.get("/tutorias/publicar", controller::mostrarPublicar);
        config.routes.post("/tutorias", controller::publicar);
        config.routes.get("/tutorias/{id}", controller::detalle);
        config.routes.post("/tutorias/{id}/reservar", controller::reservar);
    }
}
