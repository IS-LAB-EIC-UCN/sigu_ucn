package cl.ucn.app.routes;

import cl.ucn.app.controller.AuthController;
import io.javalin.config.JavalinConfig;

public final class AuthRoutes {
    private AuthRoutes() {}

    public static void register(JavalinConfig config) {
        AuthController authController = new AuthController();

        // Usamos la propiedad .routes del objeto config
        config.routes.get("/login", authController::showLogin);
        config.routes.post("/login", authController::doLogin);
        config.routes.get("/logout", authController::logout);
    }
}