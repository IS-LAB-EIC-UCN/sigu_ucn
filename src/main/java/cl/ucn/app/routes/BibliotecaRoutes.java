package cl.ucn.app.routes;

import cl.ucn.app.controller.biblioteca.LibroController;
import cl.ucn.app.controller.biblioteca.LectorController;
import io.javalin.Javalin;

public class BibliotecaRoutes {

    public static void register(Javalin app) {
        // Libros
        app.get("/biblioteca/libros", LibroController::listar);
        app.get("/biblioteca/buscar", LibroController::buscar);

        // Préstamos
        app.get("/biblioteca/prestamo/nuevo", LibroController::formularioPrestamo);
        app.post("/biblioteca/prestamo/nuevo", LibroController::solicitarPrestamo);

        // Devoluciones
        app.get("/biblioteca/devolucion", LibroController::formularioDevolucion);
        app.post("/biblioteca/devolucion", LibroController::registrarDevolucion);

        // Historial
        app.get("/biblioteca/historial", LibroController::historial);

        // Lectores
        app.get("/biblioteca/lectores", LectorController::listar);
        app.get("/biblioteca/lectores/nuevo", LectorController::formulario);
        app.post("/biblioteca/lectores/nuevo", LectorController::registrar);
    }
}