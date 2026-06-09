package cl.ucn.app.controller;

import cl.ucn.app.service.EspacioService;
import io.javalin.http.Context;

public class EspacioController {
    private final EspacioService espacioService;

    public EspacioController(EspacioService espacioService) {
        this.espacioService = espacioService;
    }

    public void getAll(Context ctx) {
        String tipo = ctx.queryParam("tipo");
        if (tipo != null) {
            ctx.json(espacioService.obtenerPorTipo(tipo));
        } else {
            ctx.json(espacioService.obtenerTodos());
        }
    }
}
