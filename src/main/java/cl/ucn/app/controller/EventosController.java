
package cl.ucn.app.controller;

import io.javalin.http.Context;
import java.util.Map;

public class EventosController {

    public void mostrarEventos(Context ctx) {

        String usuarioNombre =
                ctx.sessionAttribute("usuarioNombre");

        if (usuarioNombre == null) {
            ctx.redirect("/login");
            return;
        }

        ctx.render("eventos.jte", Map.of(
                "title", "Gestión de Eventos",
                "usuarioNombre", usuarioNombre
        ));
    }
}
