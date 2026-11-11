
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

    private final InscripcionRepository repository = new InscripcionRepository();
    private final EventoRepository eventoRepository = new EventoRepository();

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

            ctx.redirect("/eventos");

        } catch (IllegalArgumentException e) {
            ctx.status(400).result(e.getMessage());

        } catch (IllegalStateException e) {
            ctx.status(409).result(e.getMessage());
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
            ctx.redirect("/eventos");

        } catch (IllegalArgumentException e) {
            ctx.status(400).result(e.getMessage());

        } catch (IllegalStateException e) {
            ctx.status(409).result(e.getMessage());
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
                !usuarioId.equals(evento.getOrganizador().getId())) {

            ctx.status(403).result(
                    "No tienes permisos para consultar los participantes");
            return;
        }

        List<Inscripcion> inscripciones = repository.listarPorEvento(eventoId);

        ctx.render("eventos-inscritos.jte", Map.of(
                "title", "Participantes del evento",
                "usuarioNombre", usuarioNombre,
                "evento", evento,
                "inscripciones", inscripciones));
    }

}
