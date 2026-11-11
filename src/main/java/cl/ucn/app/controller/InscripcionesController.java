
package cl.ucn.app.controller;

import cl.ucn.app.repository.InscripcionRepository;
import io.javalin.http.Context;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

public class InscripcionesController {

    private final InscripcionRepository repository = new InscripcionRepository();

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
}
