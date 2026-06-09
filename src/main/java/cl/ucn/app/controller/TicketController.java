package cl.ucn.app.controller;

import cl.ucn.app.model.Ticket;
import cl.ucn.app.service.TicketService;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import java.util.List;

public class TicketController {

    private final TicketService ticketService;

    public TicketController() {
        this.ticketService = new TicketService();
    }

    public void registerRoutes(JavalinConfig config) {
        config.routes.get("/tickets", this::listarTickets);
        config.routes.post("/tickets", this::crearTicket);
        config.routes.get("/tickets/{id}", this::obtenerTicket);
    }

    private void listarTickets(Context ctx) {
        List<Ticket> tickets = ticketService.listarTodos();
        ctx.json(tickets);
    }

    private void crearTicket(Context ctx) {
        Ticket ticket = ctx.bodyAsClass(Ticket.class);
        ticketService.crearTicket(ticket);
        ctx.status(201).result("Ticket creado");
    }

    private void obtenerTicket(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id"));
        Ticket ticket = ticketService.buscarPorId(id);
        if (ticket == null) {
            ctx.status(404).result("Ticket no encontrado");
        } else {
            ctx.json(ticket);
        }
    }
}