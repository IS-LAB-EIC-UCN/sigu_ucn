package cl.ucn.app;

import io.javalin.Javalin;
import cl.ucn.app.controller.TicketController;

public class Main {
    public static void main(String[] args) {
        TicketController ticketController = new TicketController();

        Javalin.start(config -> {
            config.routes.get("/", ctx -> ctx.result("SIGU-UCN Grupo 4"));
            ticketController.registerRoutes(config);
        });
    }
}