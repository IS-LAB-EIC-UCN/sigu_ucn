package cl.ucn.app.routes;

import cl.ucn.app.controller.BibliotecaController;
import cl.ucn.app.repository.*;
import cl.ucn.app.service.*;
import io.javalin.config.JavalinConfig;

public final class BibliotecaRoutes {
    private BibliotecaRoutes() {}

    public static void register(JavalinConfig config) {
        LectorRepository lectorRepository = new LectorRepositoryImpl();
        BibliotecaService bibliotecaService = new BibliotecaServiceImpl(
                new LibroRepositoryImpl(),
                new EjemplarRepositoryImpl(),
                new PrestamoRepositoryImpl(),
                lectorRepository,
                new MultaRepositoryImpl(),
                new BibliotecarioRepositoryImpl(),
                new CategoriaRepositoryImpl(),
                new AutorRepositoryImpl()
        );
        LectorService lectorService = new LectorServiceImpl(lectorRepository);
        BibliotecaController controller = new BibliotecaController(bibliotecaService, lectorService);

        config.routes.get("/biblioteca", controller::showIndex);
        config.routes.post("/biblioteca/lectores", controller::registrarLector);
        config.routes.post("/biblioteca/libros", controller::registrarLibro);
        config.routes.post("/biblioteca/prestamos", controller::solicitarPrestamo);
        config.routes.post("/biblioteca/devoluciones", controller::registrarDevolucion);
        config.routes.post("/biblioteca/multas/pago", controller::registrarPagoMulta);

        config.routes.exception(ReglaNegocioException.class, controller::manejarReglaNegocio);
        config.routes.exception(DatosInvalidosException.class, controller::manejarDatosInvalidos);
        config.routes.exception(AccesoDenegadoException.class, controller::manejarAccesoDenegado);
    }
}
