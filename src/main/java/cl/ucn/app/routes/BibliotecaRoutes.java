package cl.ucn.app.routes;

import cl.ucn.app.controller.biblioteca.LibroController;
import cl.ucn.app.controller.biblioteca.LectorController;
import io.javalin.config.JavalinConfig;

public class BibliotecaRoutes {

    public static void register(JavalinConfig config) {
        // Libros
        config.routes.get("/biblioteca/libros", LibroController::listar);
        config.routes.get("/biblioteca/buscar", LibroController::buscar);

        // Préstamos
        config.routes.get("/biblioteca/prestamo/nuevo", LibroController::formularioPrestamo);
        config.routes.post("/biblioteca/prestamo/nuevo", LibroController::solicitarPrestamo);

        // Devoluciones
        config.routes.get("/biblioteca/devolucion", LibroController::formularioDevolucion);
        config.routes.post("/biblioteca/devolucion", LibroController::registrarDevolucion);

        // Historial
        config.routes.get("/biblioteca/historial", LibroController::historial);

        // Lectores
        config.routes.get("/biblioteca/lectores", LectorController::listar);
        config.routes.get("/biblioteca/lectores/nuevo", LectorController::formulario);
        config.routes.post("/biblioteca/lectores/nuevo", LectorController::registrar);
    }
}