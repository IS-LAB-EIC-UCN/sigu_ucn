package cl.ucn.app.controller;

import cl.ucn.app.service.EspacioService;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class ReservaController {

    private final EspacioService espacioService;

    public ReservaController(EspacioService espacioService) {
        this.espacioService = espacioService;
    }

    public void listarSalas(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        if (usuarioNombre == null) {
            ctx.redirect("/login");
            return;
        }

        Map<String, Object> model = new HashMap<>();
        model.put("title", "Salas - SIGU-UCN");
        model.put("usuarioNombre", usuarioNombre);
        model.put("salas", espacioService.listarDisponibles());

        ctx.render("reservas/salas.jte", model);
    }
}