package cl.ucn.app.routes;

import cl.ucn.app.controller.AdminTutoriasController;
import cl.ucn.app.repository.AsignaturaRepository;
import cl.ucn.app.repository.AsignaturaRepositoryImpl;
import cl.ucn.app.repository.EstudianteRepositoryImpl;
import cl.ucn.app.repository.TutorRepositoryImpl;
import cl.ucn.app.repository.TutoriaRepositoryImpl;
import cl.ucn.app.service.AsignaturaService;
import cl.ucn.app.service.TutorService;
import io.javalin.config.JavalinConfig;

public final class TutoriaAdminRoutes {

    private TutoriaAdminRoutes() {
    }

    public static void register(JavalinConfig config) {
        AsignaturaRepository asignaturaRepository = new AsignaturaRepositoryImpl();
        AsignaturaService asignaturaService = new AsignaturaService(
                asignaturaRepository,
                new TutoriaRepositoryImpl()
        );
        TutorService tutorService = new TutorService(
                new TutorRepositoryImpl(),
                new EstudianteRepositoryImpl(),
                asignaturaRepository
        );
        AdminTutoriasController controller = new AdminTutoriasController(asignaturaService, tutorService);

        config.routes.before("/tutorias/admin/*", controller::verificarAdministrador);

        config.routes.get("/tutorias/admin/asignaturas", controller::listarAsignaturas);
        config.routes.get("/tutorias/admin/asignaturas/nueva", controller::mostrarNuevaAsignatura);
        config.routes.post("/tutorias/admin/asignaturas", controller::crearAsignatura);
        config.routes.get("/tutorias/admin/asignaturas/{id}/editar", controller::mostrarEditarAsignatura);
        config.routes.post("/tutorias/admin/asignaturas/{id}", controller::actualizarAsignatura);
        config.routes.post("/tutorias/admin/asignaturas/{id}/eliminar", controller::eliminarAsignatura);

        config.routes.get("/tutorias/admin/tutores", controller::listarTutores);
        config.routes.get("/tutorias/admin/tutores/nuevo", controller::mostrarNuevoTutor);
        config.routes.post("/tutorias/admin/tutores", controller::registrarTutor);
    }
}
