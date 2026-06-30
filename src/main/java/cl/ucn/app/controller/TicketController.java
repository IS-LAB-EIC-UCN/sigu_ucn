package cl.ucn.app.controller;

import cl.ucn.app.model.CategoriaTicket;
import cl.ucn.app.model.Ticket;
import cl.ucn.app.repository.CategoriaTicketRepositoryImpl;
import cl.ucn.app.repository.ComentarioRepositoryImpl;
import cl.ucn.app.repository.TicketRepositoryImpl;
import cl.ucn.app.service.ITicketService;
import cl.ucn.app.service.TicketServiceImpl;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

/**
 * Controlador del módulo de soporte.
 *
 * Responsabilidad única (S de SOLID): recibir peticiones HTTP,
 * delegar al servicio y renderizar la vista. Sin lógica de negocio.
 *
 * Instancia sus dependencias igual que AuthController (new en constructor),
 * respetando el patrón del proyecto base.
 */
public class TicketController {

    private final ITicketService ticketService;

    public TicketController() {
        this.ticketService = new TicketServiceImpl(
                new TicketRepositoryImpl(),
                new ComentarioRepositoryImpl(),
                new CategoriaTicketRepositoryImpl()
        );
    }

    // ── Vistas ────────────────────────────────────────────────────────────────

    /** GET /soporte */
    public void listarTickets(Context ctx) {
        String estado    = ctx.queryParam("estado");
        String prioridad = ctx.queryParam("prioridad");
        String tecQ      = ctx.queryParam("tecnicoId");
        Long tecnicoId   = (tecQ != null && !tecQ.isBlank()) ? Long.parseLong(tecQ) : null;

        List<Ticket> tickets = ticketService.filtrarTickets(estado, prioridad, tecnicoId);
        List<CategoriaTicket> categorias = new CategoriaTicketRepositoryImpl().listarTodas();

        ctx.render("soporte/lista.jte", Map.of(
                "tickets",   tickets,
                "categorias", categorias,
                "usuarioRol", String.valueOf(ctx.sessionAttribute("usuarioRol"))
        ));
    }

    /** GET /soporte/nuevo */
    public void mostrarFormularioCrear(Context ctx) {
        List<CategoriaTicket> categorias = new CategoriaTicketRepositoryImpl().listarTodas();
        ctx.render("soporte/nuevo.jte", Map.of("categorias", categorias, "error", ""));
    }

    /** POST /soporte/nuevo */
    public void crearTicket(Context ctx) {
        try {
            String titulo      = ctx.formParam("titulo");
            String descripcion = ctx.formParam("descripcion");
            String prioridad   = ctx.formParam("prioridad");
            Long categoriaId   = Long.parseLong(ctx.formParam("categoriaId"));
            Long solicitanteId = (Long) ctx.sessionAttribute("usuarioId");

            ticketService.crearTicket(titulo, descripcion, prioridad, categoriaId, solicitanteId);
            ctx.redirect("/soporte");
        } catch (Exception e) {
            List<CategoriaTicket> categorias = new CategoriaTicketRepositoryImpl().listarTodas();
            ctx.render("soporte/nuevo.jte", Map.of("categorias", categorias, "error", e.getMessage()));
        }
    }

    /** GET /soporte/{id} */
    public void verTicket(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id"));
        Ticket ticket = ticketService.obtenerTicket(id);
        ctx.render("soporte/detalle.jte", Map.of(
                "ticket",      ticket,
                "comentarios", ticketService.obtenerComentarios(id),
                "usuarioId",   ctx.sessionAttribute("usuarioId"),
                "usuarioRol",  String.valueOf(ctx.sessionAttribute("usuarioRol"))
        ));
    }

    /** POST /soporte/{id}/asignar */
    public void asignarTecnico(Context ctx) {
        Long ticketId  = Long.parseLong(ctx.pathParam("id"));
        Long tecnicoId = Long.parseLong(ctx.formParam("tecnicoId"));
        try {
            ticketService.asignarTecnico(ticketId, tecnicoId);
        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Error: " + e.getMessage());
        }
        ctx.redirect("/soporte/" + ticketId);
    }

    /** POST /soporte/{id}/estado */
    public void cambiarEstado(Context ctx) {
        Long ticketId  = Long.parseLong(ctx.pathParam("id"));
        String estado  = ctx.formParam("estado");
        Long usuarioId = (Long) ctx.sessionAttribute("usuarioId");
        try {
            ticketService.cambiarEstado(ticketId, estado, usuarioId);
        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Error: " + e.getMessage());
        }
        ctx.redirect("/soporte/" + ticketId);
    }

    /** POST /soporte/{id}/cerrar */
    public void cerrarTicket(Context ctx) {
        Long ticketId   = Long.parseLong(ctx.pathParam("id"));
        String resolucion = ctx.formParam("resolucion");
        try {
            ticketService.cerrarTicket(ticketId, resolucion);
        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Error: " + e.getMessage());
        }
        ctx.redirect("/soporte/" + ticketId);
    }

    /** POST /soporte/{id}/comentar */
    public void agregarComentario(Context ctx) {
        Long ticketId  = Long.parseLong(ctx.pathParam("id"));
        String cuerpo  = ctx.formParam("contenido");
        Long autorId   = (Long) ctx.sessionAttribute("usuarioId");
        try {
            ticketService.agregarComentario(ticketId, cuerpo, autorId);
        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Error: " + e.getMessage());
        }
        ctx.redirect("/soporte/" + ticketId);
    }
}
