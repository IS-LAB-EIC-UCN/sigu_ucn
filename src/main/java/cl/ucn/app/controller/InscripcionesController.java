
package cl.ucn.app.controller;

import cl.ucn.app.repository.InscripcionRepository;
import io.javalin.http.Context;

public class InscripcionesController {

    private final InscripcionRepository repository = new InscripcionRepository();

    public void inscribir(Context ctx) {

        Long usuarioId = ctx.sessionAttribute("usuarioId");

        if (usuarioId == null) {
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
