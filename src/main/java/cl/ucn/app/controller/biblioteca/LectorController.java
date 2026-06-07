package cl.ucn.app.controller.biblioteca;

import cl.ucn.app.service.biblioteca.LectorService;
import io.javalin.http.Context;
import java.util.Map;
import java.util.HashMap;

public class LectorController {

    private final LectorService lectorService = new LectorService();

    public void listar(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        model.put("usuarioNombre", "Usuario");
        model.put("lectores", lectorService.listarTodos());

        if (ctx.sessionAttribute("error") != null) {
            model.put("error", ctx.sessionAttribute("error"));
            ctx.consumeSessionAttribute("error");
        }
        ctx.render("biblioteca/lectores.jte", model);
    }

    public void formulario(Context ctx) {
        // En este diseño, el formulario de nuevo lector está en la misma vista de listar
        ctx.redirect("/biblioteca/lectores");
    }

    public void registrar(Context ctx) {
        String nombre = ctx.formParam("nombre");
        String correo = ctx.formParam("correo");
        String rut = ctx.formParam("rut");

        try {
            lectorService.registrarLector(nombre, correo, rut);
            ctx.redirect("/biblioteca/lectores");
        } catch (IllegalArgumentException e) {
            ctx.sessionAttribute("error", e.getMessage());
            ctx.redirect("/biblioteca/lectores");
        }
    }

    // --- Métodos dummy para edición que pide BibliotecaRoutes ---
    public void formularioEditar(Context ctx) { ctx.result("Formulario Editar Lector Pendiente"); }
    public void editar(Context ctx) { ctx.result("Editar Lector Pendiente"); }
}