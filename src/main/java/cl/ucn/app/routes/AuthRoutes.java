package cl.ucn.app.routes;

import cl.ucn.app.controller.AuthController;
import io.javalin.Javalin;

public class AuthRoutes {

    private final AuthController authController;

    public AuthRoutes(AuthController authController) {
        this.authController = authController;
    }

    public void register(Javalin app) {
        app.get("/login", authController::renderLogin);
        app.post("/login", authController::handleLogin);
        app.get("/logout", authController::handleLogout);
    }
}
