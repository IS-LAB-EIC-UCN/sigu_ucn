package cl.ucn.app.routes;

import cl.ucn.app.controller.HomeController;
import io.javalin.config.JavalinConfig;

public final class HomeRoutes {

    private HomeRoutes() {
    }

    public static void register(JavalinConfig config) {
        HomeController homeController = new HomeController();

        config.routes.get("/", ctx -> ctx.redirect("/login"));
        config.routes.get("/home", homeController::showHome);
    }
}