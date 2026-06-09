package cl.ucn.app.controller;

import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;

public class TicketController {

    public void registerRoutes(JavalinConfig config) {
        config.routes.get("/tickets", this::listarTickets);
        config.routes.post("/tickets", this::crearTicket);
        config.routes.get("/tickets/{id}", this::obtenerTicket);
    }

    private void listarTickets(Context ctx) {
        ctx.result("lista de tickets");
    }

    private void crearTicket(Context ctx) {
        ctx.result("ticket creado");
    }

    private void obtenerTicket(Context ctx) {
        String id = ctx.pathParam("id");
        ctx.result("ticket " + id);
    }
}