
package cl.ucn.app.controller;

import cl.ucn.app.repository.InscripcionRepository;
import io.javalin.http.Context;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import cl.ucn.app.model.Evento;
import cl.ucn.app.model.Inscripcion;
import cl.ucn.app.repository.EventoRepository;
import java.util.List;
import java.util.Map;

public class InscripcionesController {

    private final InscripcionRepository repository;
    private final EventoRepository eventoRepository;
    public InscripcionesController() { this(new InscripcionRepository(), new EventoRepository()); }
    public InscripcionesController(InscripcionRepository repository, EventoRepository eventoRepository) {
        this.repository=repository;this.eventoRepository=eventoRepository;
    }

    public void inscribir(Context ctx) {

        Long usuarioId = ctx.sessionAttribute("usuarioId");

        if (usuarioId == null) {
            ctx.redirect("/login");
            return;
        }

        String tokenSesion = ctx.sessionAttribute("csrfToken");
        String tokenFormulario = ctx.formParam("csrfToken");

        if (tokenSesion == null || tokenFormulario == null ||
                !MessageDigest.isEqual(
                        tokenSesion.getBytes(StandardCharsets.UTF_8),
                        tokenFormulario.getBytes(StandardCharsets.UTF_8))) {

            ctx.status(403).result("Token de seguridad inválido");
            return;
        }

        Long eventoId;

        try {
            eventoId = Long.parseLong(ctx.pathParam("id"));
        } catch (NumberFormatException e) {
            ctx.status(400).result("ID de evento inválido");
            return;
        }

        try {
            repository.inscribir(eventoId, usuarioId);
            ctx.sessionAttribute("eventosAviso", "Tu inscripción se registró correctamente. Ya tienes un cupo reservado.");

            ctx.redirect("/eventos");

        } catch (IllegalArgumentException e) {
            mostrarError(ctx, 400, e.getMessage());

        } catch (IllegalStateException e) {
            mostrarError(ctx, 409, e.getMessage());
        }
    }

    public void cancelar(Context ctx) {

        Long usuarioId = ctx.sessionAttribute("usuarioId");

        if (usuarioId == null) {
            ctx.redirect("/login");
            return;
        }

        // Validar token CSRF
        String tokenSesion = ctx.sessionAttribute("csrfToken");
        String tokenFormulario = ctx.formParam("csrfToken");

        if (tokenSesion == null || tokenFormulario == null ||
                !MessageDigest.isEqual(
                        tokenSesion.getBytes(StandardCharsets.UTF_8),
                        tokenFormulario.getBytes(StandardCharsets.UTF_8))) {

            ctx.status(403).result("Token de seguridad inválido");
            return;
        }

        Long eventoId;

        try {
            eventoId = Long.parseLong(ctx.pathParam("id"));
        } catch (NumberFormatException e) {
            ctx.status(400).result("ID de evento inválido");
            return;
        }

        try {
            repository.cancelarInscripcion(eventoId, usuarioId);
            ctx.sessionAttribute("eventosAviso", "Tu inscripción fue cancelada. El cupo quedó disponible.");
            ctx.redirect("/eventos");

        } catch (IllegalArgumentException e) {
            mostrarError(ctx, 400, e.getMessage());

        } catch (IllegalStateException e) {
            mostrarError(ctx, 409, e.getMessage());
        }
    }

    public void mostrarInscritos(Context ctx) {

        Long usuarioId = ctx.sessionAttribute("usuarioId");
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        if (usuarioId == null || usuarioNombre == null) {
            ctx.redirect("/login");
            return;
        }

        Long eventoId;

        try {
            eventoId = Long.parseLong(ctx.pathParam("id"));
        } catch (NumberFormatException e) {
            ctx.status(400).result("ID de evento inválido");
            return;
        }

        Evento evento = eventoRepository.findById(eventoId);

        if (evento == null) {
            ctx.status(404).result("Evento no encontrado");
            return;
        }

        // Solo el organizador puede consultar los participantes.
        if (evento.getOrganizador() == null ||
                !cl.ucn.app.service.PermisosEventos.consultaInscritos(ctx.sessionAttribute("usuarioRol"), usuarioId, evento.getOrganizador().getId(),evento.getEstado())) {

            ctx.status(403).result(
                    "No tienes permisos para consultar los participantes");
            return;
        }

        List<Inscripcion> inscripciones = repository.listarPorEvento(eventoId);
        if ("true".equals(ctx.queryParam("activos"))) inscripciones = inscripciones.stream().filter(i -> "INSCRITO".equals(i.getEstado())).toList();

        ctx.render("eventos-inscritos.jte", Map.of(
                "admin", "ADMIN".equals(ctx.sessionAttribute("usuarioRol")),
                "title", "Participantes del evento",
                "usuarioNombre", usuarioNombre,
                "evento", evento,
                "inscripciones", inscripciones,
                "csrfToken", ctx.sessionAttribute("csrfToken"),
                "puedeRegistrarAsistencia", cl.ucn.app.service.PermisosEventos.registraAsistencia(ctx.sessionAttribute("usuarioRol"),usuarioId,evento.getOrganizador().getId())));
    }

    public void registrarAsistencia(Context ctx) {
        Long usuarioId=ctx.sessionAttribute("usuarioId");
        if(usuarioId==null) { ctx.redirect("/login");return; }
        String token=ctx.sessionAttribute("csrfToken"), recibido=ctx.formParam("csrfToken");
        if(token==null || recibido==null || !MessageDigest.isEqual(token.getBytes(StandardCharsets.UTF_8),recibido.getBytes(StandardCharsets.UTF_8))) {
            ctx.status(403).result("Token de seguridad inválido");return;
        }
        try {
            Long evento=Long.valueOf(ctx.pathParam("id")), inscripcion=Long.valueOf(ctx.pathParam("inscripcionId"));
            String valor=ctx.formParam("asistencia");
            if(!"true".equals(valor) && !"false".equals(valor)) throw new IllegalArgumentException("Selecciona un valor válido de asistencia.");
            repository.registrarAsistencia(evento,inscripcion,usuarioId,ctx.sessionAttribute("usuarioRol"),Boolean.parseBoolean(valor));
            ctx.redirect("/eventos/"+evento+"/inscritos");
        } catch(SecurityException ex) { ctx.status(403).result(ex.getMessage()); }
        catch(IllegalArgumentException ex) { mostrarError(ctx,400,ex.getMessage()); }
        catch(IllegalStateException ex) { mostrarError(ctx,409,ex.getMessage()); }
    }
    private void mostrarError(Context ctx, int estado, String mensaje) {
        ctx.status(estado);
        ctx.render("eventos-error.jte", Map.of(
            "admin", "ADMIN".equals(ctx.sessionAttribute("usuarioRol")),
            "title", "Eventos acad\u00e9micos", "usuarioNombre", ctx.sessionAttribute("usuarioNombre"),
            "csrfToken", ctx.sessionAttribute("csrfToken"), "error", mensaje, "aviso", ""));
    }

}
