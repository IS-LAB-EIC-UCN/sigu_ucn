package cl.ucn.app.routes;

import cl.ucn.app.controller.AuthController;
import cl.ucn.app.service.AuthService;
import io.javalin.config.JavalinConfig;

public final class AuthRoutes {
    
    private static final AuthController authController = new AuthController(new AuthService());

    private AuthRoutes() {}

    public static void register(JavalinConfig config) {

        // Usamos la propiedad .routes del objeto config
        config.routes.get("/login", authController::renderLogin);
        config.routes.post("/login", authController::handleLogin);
        config.routes.get("/logout", authController::handleLogout);
    }
}