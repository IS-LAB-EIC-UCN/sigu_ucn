package cl.ucn.app.routes;

import cl.ucn.app.controller.talleres.InscripcionController;
import cl.ucn.app.controller.talleres.TallerController;
import cl.ucn.app.service.talleres.TalleresModule;
import io.javalin.config.JavalinConfig;

/** Rutas del módulo Grupo 3 - Inscripción a talleres. */
public final class TallerRoutes {

    private TallerRoutes() {
    }

    public static void register(JavalinConfig config) {
        TalleresModule modulo = new TalleresModule();
        TallerController tallerController = new TallerController(modulo.tallerService(), modulo.inscripcionService());
        InscripcionController inscripcionController = new InscripcionController(modulo.inscripcionService());

        // Las rutas fijas van antes que /talleres/{id}
        config.routes.get("/talleres", tallerController::listar);
        config.routes.get("/talleres/nuevo", tallerController::formularioNuevo);
        config.routes.post("/talleres", tallerController::registrar);
        config.routes.get("/talleres/{id}", tallerController::detalle);
        config.routes.post("/talleres/{id}/inscribir", inscripcionController::inscribir);

        config.routes.get("/mis-inscripciones", inscripcionController::misInscripciones);
        config.routes.post("/inscripciones/{id}/anular", inscripcionController::anular);
    }
}
