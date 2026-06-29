package cl.ucn.app.controller;

import cl.ucn.app.service.InventarioService;
import cl.ucn.app.model.Recurso;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import java.util.List;
import java.util.Map;

public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    public void listarInventario(Context ctx) {
        try {
            String categoria = ctx.queryParam("categoria");
            List<Recurso> recursos;

            if (categoria != null && !categoria.isBlank()) {
                recursos = inventarioService.filtrarRecursosPorCategoria(categoria.trim());
            } else {
                recursos = inventarioService.obtenerInventarioCompleto();
            }

            ctx.render("jte/inventario.jte", Map.of("recursos", recursos));

        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
            ctx.render("jte/error.jte", Map.of("error", "Error al cargar el inventario."));
        }
    }
}
