package cl.ucn.app.controller;

import cl.ucn.app.model.Expositor;
import cl.ucn.app.service.ExpositorService;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpositorController {

    private final ExpositorService expositorService;

    public ExpositorController() {
        this.expositorService = new ExpositorService();
    }

    public void listar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) {
            ctx.redirect("/login");
            return;
        }
        List<Expositor> expositores = expositorService.listarTodos();
        Map<String, Object> model = new HashMap<>();
        model.put("title", "Expositores - SIGU-UCN");
        model.put("usuarioNombre", usuarioNombre);
        model.put("expositores", expositores);
        model.put("error", ctx.queryParam("error"));
        model.put("success", ctx.queryParam("success"));
        ctx.render("expositores/lista.jte", model);
    }

    public void mostrarFormulario(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) {
            ctx.redirect("/login");
            return;
        }
        Map<String, Object> model = new HashMap<>();
        model.put("title", "Registrar Expositor - SIGU-UCN");
        model.put("usuarioNombre", usuarioNombre);
        model.put("error", "");
        ctx.render("expositores/formulario.jte", model);
    }

    public void registrar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) {
            ctx.redirect("/login");
            return;
        }
        try {
            String nombre = ctx.formParam("nombre");
            String email = ctx.formParam("email");
            String afiliacion = ctx.formParam("afiliacion");
            String telefono = ctx.formParam("telefono");
            String bio = ctx.formParam("bio");

            if (nombre == null || nombre.isBlank()) {
                throw new IllegalArgumentException("El nombre del expositor es obligatorio.");
            }

            Expositor expositor = new Expositor(nombre, email, afiliacion);
            expositor.setTelefono(telefono);
            expositor.setBio(bio);

            expositorService.registrar(expositor);

            ctx.redirect("/expositores?success=Expositor registrado exitosamente.");

        } catch (IllegalArgumentException e) {
            Map<String, Object> model = new HashMap<>();
            model.put("title", "Registrar Expositor - SIGU-UCN");
            model.put("usuarioNombre", usuarioNombre);
            model.put("error", e.getMessage());
            ctx.status(400);
            ctx.render("expositores/formulario.jte", model);
        } catch (Exception e) {
            Map<String, Object> model = new HashMap<>();
            model.put("title", "Registrar Expositor - SIGU-UCN");
            model.put("usuarioNombre", usuarioNombre);
            model.put("error", "Error inesperado: " + e.getMessage());
            ctx.status(500);
            ctx.render("expositores/formulario.jte", model);
        }
    }
}
