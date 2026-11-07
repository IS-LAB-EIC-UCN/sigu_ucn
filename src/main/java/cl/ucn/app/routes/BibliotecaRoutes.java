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
        config.routes.post("/biblioteca/prestamos", controller::solicitarPrestamo);
        config.routes.post("/biblioteca/devoluciones", controller::registrarDevolucion);
        config.routes.post("/biblioteca/multas/pago", controller::registrarPagoMulta);

        // Manejo controlado de excepciones del dominio (RNF-01, RNF-02, RNF-06)
        config.routes.exception(ReglaNegocioException.class, (e, ctx) -> {
            ctx.status(409).result("[" + e.getCodigoRegla() + "] " + e.getMessage());
        });
        config.routes.exception(DatosInvalidosException.class, (e, ctx) -> {
            ctx.status(400).result("Error en campo [" + e.getCampo() + "]: " + e.getMessage());
        });
        config.routes.exception(AccesoDenegadoException.class, (e, ctx) -> {
            ctx.status(e.getStatusCode()).result(e.getMessage());
        });
    }
}
