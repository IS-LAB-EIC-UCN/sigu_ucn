package cl.ucn.app.routes;

import cl.ucn.app.controller.AuthController;
import cl.ucn.app.controller.RegistroController;
import cl.ucn.app.repository.RegistroRepositoryImpl;
import cl.ucn.app.service.RegistroService;
import io.javalin.config.JavalinConfig;

public final class AuthRoutes {
    private AuthRoutes() {}

    public static void register(JavalinConfig config) {
        AuthController authController = new AuthController();
        RegistroController registroController =
                new RegistroController(new RegistroService(new RegistroRepositoryImpl()));

        // Usamos la propiedad .routes del objeto config
        config.routes.get("/login", authController::showLogin);
        config.routes.post("/login", authController::doLogin);
        config.routes.get("/logout", authController::logout);
        config.routes.get("/registro", registroController::mostrar);
        config.routes.post("/registro", registroController::registrar);
    }
}