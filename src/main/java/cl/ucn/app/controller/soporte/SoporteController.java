package cl.ucn.app.controller.soporte;

import cl.ucn.app.model.soporte.TecnicoDisponible;
import cl.ucn.app.model.soporte.Ticket;
import cl.ucn.app.service.soporte.TicketService;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SoporteController {
    private static final Logger LOG = LoggerFactory.getLogger(SoporteController.class);
    private final TicketService ticketService = new TicketService();

    public void mostrarInicio(Context ctx) {Long usuarioId = ctx.sessionAttribute("usuarioId"); String nombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioId == null || nombre == null) {
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
                "csrfToken", token,
                "esAdmin", ticketService.esAdministrador(usuarioId),
                "esTecnico", ticketService.esTecnico(usuarioId)
        ));
    }

    public void crearTicket(Context ctx) {
        if (!validarSesion(ctx) || !validarCsrf(ctx, ctx.formParam("csrfToken"))) {
            return;
        }

        ejecutar(ctx, () -> {
            Long usuarioId = ctx.sessionAttribute("usuarioId");

            Ticket ticket = ticketService.crearTicket(
                    ctx.formParam("titulo"),
                    ctx.formParam("descripcion"),
                    usuarioId);

            ctx.status(201).json(Map.of(
                    "exito", true,
                    "mensaje", "Ticket creado correctamente.",
                    "datos", Map.of("id", ticket.getId(), "estado", ticket.getEstado().name())
            ));
        });
    }

    public void listarTecnicos(Context ctx) {
        if (!validarSesion(ctx)) return;

        ejecutar(ctx, () -> {
            List<TecnicoDisponible> lista = ticketService.listarTecnicos(ctx.sessionAttribute("usuarioId"));
            responderOk(ctx, "Técnicos disponibles.", lista);
        });
    }

    public void listarPendientes(Context ctx) {
        if (!validarSesion(ctx)) return;

        ejecutar(ctx, () -> {
            List<Map<String, Object>> lista =
                    ticketService.listarPendientes(ctx.sessionAttribute("usuarioId")).stream()
                            .map(ticket -> Map.<String, Object>of(
                                    "id", ticket.getId(),
                                    "titulo", ticket.getTitulo(),
                                    "estado", ticket.getEstado().name()
                            )).toList();

            responderOk(ctx, "Tickets pendientes.", lista);
        });
    }

    public void asignarTecnico(Context ctx) {
        if (!validarSesion(ctx) || !validarCsrf(ctx, ctx.header("X-CSRF-Token"))) {
            return;
        }

        ejecutar(ctx, () -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            AsignacionRequest solicitud = ctx.bodyAsClass(AsignacionRequest.class);
            Long usuarioId = ctx.sessionAttribute("usuarioId");
            Ticket ticket = ticketService.asignarTecnico(id, solicitud.tecnicoId(), usuarioId);
            responderOk(ctx, "Ticket asignado correctamente.",
                    Map.of("id", ticket.getId(), "tecnicoId", solicitud.tecnicoId(), "estado", ticket.getEstado().name()
                    ));
        });
    }

    public void listarAsignados(Context ctx) {
        if (!validarSesion(ctx)) return;

        ejecutar(ctx, () -> {
            List<Map<String, Object>> lista =
                    ticketService.listarAsignados(ctx.sessionAttribute("usuarioId")).stream()
                            .map(ticket -> Map.<String, Object>of(
                                    "id", ticket.getId(),
                                    "titulo", ticket.getTitulo(),
                                    "estado", ticket.getEstado().name()
                            )).toList();

            responderOk(ctx, "Tickets asignados.", lista);
        });
    }

    public void iniciarResolucion(Context ctx) {
        if (!validarSesion(ctx) || !validarCsrf(ctx, ctx.header("X-CSRF-Token"))) {
            return;
        }

        ejecutar(ctx, () -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            Long usuarioId = ctx.sessionAttribute("usuarioId");
            Ticket ticket = ticketService.iniciarResolucion(id, usuarioId);
            responderOk(ctx, "El ticket ahora está en resolución.",
                    Map.of("id", ticket.getId(), "estado", ticket.getEstado().name()));
        });
    }

    private record AsignacionRequest(Long tecnicoId) {}

    private boolean validarSesion(Context ctx) {
        if (ctx.sessionAttribute("usuarioId") != null) {
            return true;
        }

        responderError(ctx, 401, "Debe iniciar sesión.");
        return false;
    }

    private boolean validarCsrf(Context ctx, String recibido) {
        String esperado = ctx.sessionAttribute("soporteCsrf");

        if (esperado != null && esperado.equals(recibido)) {
            return true;
        }

        responderError(ctx, 403, "Solicitud no autorizada.");
        return false;
    }

    private void ejecutar(Context ctx, Runnable operacion) {
        try {
            operacion.run();
        } catch (SecurityException e) {
            responderError(ctx, 403, e.getMessage());
        } catch (java.util.NoSuchElementException e) {
            responderError(ctx, 404, e.getMessage());
        } catch (IllegalStateException e) {
            responderError(ctx, 409, e.getMessage());
        } catch (IllegalArgumentException | io.javalin.http.BadRequestResponse e) {
            responderError(ctx, 400, "Los datos enviados no son válidos: " + e.getMessage());
        } catch (RuntimeException e) {
            LOG.error("Error en módulo de soporte", e);
            responderError(ctx, 500, "Ocurrió un error interno.");
        }
    }

    private void responderOk(Context ctx, String mensaje, Object datos) {
        ctx.status(200).json(Map.of("exito", true, "mensaje", mensaje, "datos", datos));
    }

    private void responderError(Context ctx, int estado, String mensaje) {
        ctx.status(estado).json(Map.of("exito", false, "mensaje", mensaje, "datos", Map.of()));
    }
}