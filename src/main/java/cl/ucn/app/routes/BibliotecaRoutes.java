package cl.ucn.app.routes;

import cl.ucn.app.controller.biblioteca.LibroController;
import cl.ucn.app.controller.biblioteca.LectorController;
import io.javalin.config.JavalinConfig;

public class BibliotecaRoutes {

    public static void register(JavalinConfig config) {
        LibroController libroController = new LibroController();
        LectorController lectorController = new LectorController();

        // Libros
        config.routes.get("/biblioteca/libros", libroController::listar);
        config.routes.get("/biblioteca/libros/nuevo", libroController::formularioRegistrarLibro);
        config.routes.post("/biblioteca/libros/nuevo", libroController::registrarLibro);
        config.routes.get("/biblioteca/buscar", libroController::buscar);

        // Ejemplares
        config.routes.get("/biblioteca/ejemplares/nuevo", libroController::formularioRegistrarEjemplar);
        config.routes.post("/biblioteca/ejemplares/nuevo", libroController::registrarEjemplar);

        // Préstamos
        config.routes.get("/biblioteca/prestamo/nuevo", libroController::formularioPrestamo);
        config.routes.post("/biblioteca/prestamo/nuevo", libroController::solicitarPrestamo);

        // Devoluciones
        config.routes.get("/biblioteca/devolucion", libroController::formularioDevolucion);
        config.routes.post("/biblioteca/devolucion", libroController::registrarDevolucion);

        // Historial
        config.routes.get("/biblioteca/historial", libroController::historial);

        // Lectores
        config.routes.get("/biblioteca/lectores", lectorController::listar);
        config.routes.get("/biblioteca/lectores/nuevo", lectorController::formulario);
        config.routes.post("/biblioteca/lectores/nuevo", lectorController::registrar);
        config.routes.get("/biblioteca/lectores/editar", lectorController::formularioEditar);
        config.routes.post("/biblioteca/lectores/editar", lectorController::editar);
    }
}