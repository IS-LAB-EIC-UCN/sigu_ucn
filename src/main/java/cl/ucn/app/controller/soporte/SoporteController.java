package cl.ucn.app.controller.soporte;

import cl.ucn.app.model.soporte.Ticket;
import cl.ucn.app.service.soporte.TicketService;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

public class SoporteController {
    private static final Logger LOG = LoggerFactory.getLogger(SoporteController.class);
    private final TicketService ticketService = new TicketService();

    public void mostrarInicio (Context ctx) {
        String nombre = ctx.sessionAttribute("usuarioNombre");

        if (nombre == null) {
            ctx.redirect("/login");
            return;
        }

        String token = ctx.sessionAttribute("soporteCsrf");
        if (token == null) {
            token = UUID.randomUUID().toString();
            ctx.sessionAttribute("soporteCsrf", token);
        }

        ctx.render("soporte/index.jte", Map.of(
                "title", "Soporte Técnico - SIGU-UCN",
                "usuarioNombre", nombre,
                "csrfToken", token
        ));
    }

    public void crearTicket(Context ctx) {
        Long usuarioId = ctx.sessionAttribute("usuarioId");

        if (usuarioId == null) {
            responderError(ctx, 401, "Debe iniciar sesión.");
            return;
        }

        String token = ctx.sessionAttribute("soporteCsrf");
        if (token == null || !token.equals(ctx.formParam("csrfToken"))) {
            responderError(ctx, 403, "Solicitud no autorizada.");
            return;
        }

        try {
            Ticket ticket = ticketService.crearTicket(ctx.formParam("titulo"),  ctx.formParam("descripcion"), usuarioId);
            ctx.status(201).json(Map.of(
                    "exito", true,
                    "mensaje", "Ticket creado correctamente.",
                    "datos", Map.of("id", ticket.getId(), "estado", ticket.getEstado().name())
            ));
        } catch (IllegalArgumentException e) {
            responderError(ctx, 400, e.getMessage());
        } catch (RuntimeException e) {
            LOG.error("Error al crear ticket", e);
            responderError(ctx, 500, "Error interno al crear el ticket.");
        }
    }

    private void responderError(Context ctx, int estado, String mensaje) {
        ctx.status(estado).json(Map.of("exito", false, "mensaje", mensaje, "datos", Map.of()));
    }
}
