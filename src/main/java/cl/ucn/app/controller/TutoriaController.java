package cl.ucn.app.controller;

import io.javalin.http.Context;
import java.util.HashMap;
import java.util.Map;

public class TutoriaController {

    public void showTutoria(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        if (usuarioNombre == null) {
            ctx.redirect("/login");
            return;
        }

        Map<String, Object> model = new HashMap<>();
        model.put("title", "Inicio - SIGU-UCN");
        model.put("usuarioNombre", usuarioNombre);

        ctx.render("tutorias.jte", model);
    }
}
