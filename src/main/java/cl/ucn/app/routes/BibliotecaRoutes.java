package cl.ucn.app.routes;

import cl.ucn.app.auth.BibliotecaAuthFilter;
import cl.ucn.app.controller.biblioteca.CatalogoController;
import cl.ucn.app.controller.biblioteca.LectorController;
import cl.ucn.app.controller.biblioteca.MisDatosController;
import cl.ucn.app.controller.biblioteca.MultaController;
import cl.ucn.app.controller.biblioteca.PrestamoController;
import cl.ucn.app.controller.biblioteca.RegistroController;
import cl.ucn.app.exceptions.GlobalExceptionHandler;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.util.Set;

public class BibliotecaRoutes {

    private static final Set<String> TODOS = Set.of("ADMIN", "DOCENTE", "ESTUDIANTE");
    private static final Set<String> ADMIN = Set.of("ADMIN");
    private static final Set<String> LECTORES = Set.of("DOCENTE", "ESTUDIANTE");
    // DOCENTE y ESTUDIANTE son sinonimos para biblioteca. Si se requiere
    // diferenciar (ej: docente 5 libros, estudiante 3), crear un nuevo
    // Set<String> LIMITADO_DOCENTE con logica propia en un futuro.

    public static void register(JavalinConfig config) {
        GlobalExceptionHandler.register(config);

        CatalogoController catalogoController = new CatalogoController();
        PrestamoController prestamoController = new PrestamoController();
        RegistroController registroController = new RegistroController();
        LectorController lectorController = new LectorController();

        // Catalogo (lectura para todos)
        config.routes.get("/biblioteca/libros", conPermiso(catalogoController::listar, TODOS));
        config.routes.get("/biblioteca/buscar", conPermiso(catalogoController::buscar, TODOS));

        // Crear libros y ejemplares (solo ADMIN)
        config.routes.get("/biblioteca/libros/nuevo", conPermiso(catalogoController::formularioRegistrarLibro, ADMIN));
        config.routes.post("/biblioteca/libros/nuevo", conPermiso(catalogoController::registrarLibro, ADMIN));
        config.routes.get("/biblioteca/ejemplares/nuevo", conPermiso(catalogoController::formularioRegistrarEjemplar, ADMIN));
        config.routes.post("/biblioteca/ejemplares/nuevo", conPermiso(catalogoController::registrarEjemplar, ADMIN));

        // Edicion y eliminacion (solo ADMIN)
        config.routes.get("/biblioteca/libros/editar", conPermiso(registroController::formularioEditarLibro, ADMIN));
        config.routes.post("/biblioteca/libros/editar", conPermiso(registroController::editarLibro, ADMIN));
        config.routes.get("/biblioteca/libros/eliminar", conPermiso(registroController::eliminarLibro, ADMIN));
        config.routes.get("/biblioteca/ejemplares/editar", conPermiso(registroController::formularioEditarEjemplar, ADMIN));
        config.routes.post("/biblioteca/ejemplares/editar", conPermiso(registroController::editarEjemplar, ADMIN));
        config.routes.get("/biblioteca/ejemplares/eliminar", conPermiso(registroController::eliminarEjemplar, ADMIN));

        // Prestamos (DOCENTE y ESTUDIANTE pueden solicitar; ADMIN registra devoluciones)
        config.routes.get("/biblioteca/prestamo/nuevo", conPermiso(prestamoController::formularioPrestamo, LECTORES));
        config.routes.post("/biblioteca/prestamo/nuevo", conPermiso(prestamoController::solicitarPrestamo, LECTORES));
        config.routes.post("/biblioteca/devolucion", conPermiso(prestamoController::registrarDevolucion, ADMIN));

        // Historial (todos pueden ver)
        config.routes.get("/biblioteca/historial", conPermiso(prestamoController::historial, TODOS));
        config.routes.post("/biblioteca/prestamo/devolver", conPermiso(prestamoController::devolverMiPrestamo, LECTORES));

        // Mis datos (LECTORES completan su perfil de lector)
        MisDatosController misDatosController = new MisDatosController();
        config.routes.get("/biblioteca/mis-datos", conPermiso(misDatosController::formulario, LECTORES));
        config.routes.post("/biblioteca/mis-datos", conPermiso(misDatosController::guardar, LECTORES));

        // Multas (registrar pago solo ADMIN)
        MultaController multaController = new MultaController();
        config.routes.post("/biblioteca/multa/pagar", conPermiso(multaController::registrarPago, ADMIN));

        // Lectores (solo ADMIN)
        config.routes.get("/biblioteca/lectores", conPermiso(lectorController::listar, ADMIN));
        config.routes.get("/biblioteca/lectores/nuevo", conPermiso(lectorController::formulario, ADMIN));
        config.routes.post("/biblioteca/lectores/nuevo", conPermiso(lectorController::registrar, ADMIN));
        config.routes.get("/biblioteca/lectores/editar", conPermiso(lectorController::formularioEditar, ADMIN));
        config.routes.post("/biblioteca/lectores/editar", conPermiso(lectorController::editar, ADMIN));
        config.routes.get("/biblioteca/lectores/eliminar", conPermiso(lectorController::eliminar, ADMIN));
    }

    private static Handler conPermiso(Handler handler, Set<String> permitidos) {
        return ctx -> {
            if (BibliotecaAuthFilter.requireRol(ctx, permitidos)) {
                handler.handle(ctx);
            }
        };
    }
}
