package cl.ucn.app.controller;

import cl.ucn.app.security.AutorizacionAdmin;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class HomeController {

    public void showHome(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        if (usuarioNombre == null) {
            ctx.redirect("/login");
            return;
        }

        String usuarioRol = ctx.sessionAttribute("usuarioRol");

        Map<String, Object> model = new HashMap<>();
        model.put("title", "Inicio - SIGU-UCN");
        model.put("usuarioNombre", usuarioNombre);
        model.put("esAdmin", AutorizacionAdmin.esAdmin(usuarioRol));

        ctx.render("home.jte", model);
    }
}