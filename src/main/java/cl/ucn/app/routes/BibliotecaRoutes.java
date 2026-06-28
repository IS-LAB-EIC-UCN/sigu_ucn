package cl.ucn.app.routes;

import cl.ucn.app.controller.biblioteca.CatalogoController;
import cl.ucn.app.controller.biblioteca.LectorController;
import cl.ucn.app.controller.biblioteca.PrestamoController;
import cl.ucn.app.controller.biblioteca.RegistroController;
import cl.ucn.app.exceptions.GlobalExceptionHandler;
import io.javalin.config.JavalinConfig;

public class BibliotecaRoutes {

    public static void register(JavalinConfig config) {
        GlobalExceptionHandler.register(config);

        CatalogoController catalogoController = new CatalogoController();
        PrestamoController prestamoController = new PrestamoController();
        RegistroController registroController = new RegistroController();
        LectorController lectorController = new LectorController();

        // Catalogo
        config.routes.get("/biblioteca/libros", catalogoController::listar);
        config.routes.get("/biblioteca/buscar", catalogoController::buscar);
        config.routes.get("/biblioteca/libros/nuevo", catalogoController::formularioRegistrarLibro);
        config.routes.post("/biblioteca/libros/nuevo", catalogoController::registrarLibro);
        config.routes.get("/biblioteca/ejemplares/nuevo", catalogoController::formularioRegistrarEjemplar);
        config.routes.post("/biblioteca/ejemplares/nuevo", catalogoController::registrarEjemplar);

        // Edicion y eliminacion
        config.routes.get("/biblioteca/libros/editar", registroController::formularioEditarLibro);
        config.routes.post("/biblioteca/libros/editar", registroController::editarLibro);
        config.routes.get("/biblioteca/libros/eliminar", registroController::eliminarLibro);
        config.routes.get("/biblioteca/ejemplares/editar", registroController::formularioEditarEjemplar);
        config.routes.post("/biblioteca/ejemplares/editar", registroController::editarEjemplar);
        config.routes.get("/biblioteca/ejemplares/eliminar", registroController::eliminarEjemplar);

        // Prestamos
        config.routes.get("/biblioteca/prestamo/nuevo", prestamoController::formularioPrestamo);
        config.routes.post("/biblioteca/prestamo/nuevo", prestamoController::solicitarPrestamo);
        config.routes.get("/biblioteca/devolucion", prestamoController::formularioDevolucion);
        config.routes.post("/biblioteca/devolucion", prestamoController::registrarDevolucion);
        config.routes.get("/biblioteca/historial", prestamoController::historial);

        // Lectores
        config.routes.get("/biblioteca/lectores", lectorController::listar);
        config.routes.get("/biblioteca/lectores/nuevo", lectorController::formulario);
        config.routes.post("/biblioteca/lectores/nuevo", lectorController::registrar);
        config.routes.get("/biblioteca/lectores/editar", lectorController::formularioEditar);
        config.routes.post("/biblioteca/lectores/editar", lectorController::editar);
        config.routes.get("/biblioteca/lectores/eliminar", lectorController::eliminar);
    }
}
