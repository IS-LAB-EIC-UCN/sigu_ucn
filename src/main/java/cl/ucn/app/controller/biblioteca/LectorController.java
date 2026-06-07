package cl.ucn.app.controller.biblioteca;

import cl.ucn.app.model.biblioteca.Lector;
import cl.ucn.app.service.biblioteca.LectorService;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LectorController {

    private final LectorService lectorService = new LectorService();

    public void listar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) { ctx.redirect("/login"); return; }

        List<Lector> lectores = lectorService.listarTodos();
        Map<String, Object> model = new HashMap<>();
        model.put("usuarioNombre", usuarioNombre);
        model.put("lectores", lectores);
        model.put("error", null);
        ctx.render("biblioteca/lectores.jte", model);
    }

    public void formulario(Context ctx) {
        ctx.redirect("/biblioteca/lectores");
    }

    public void registrar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) { ctx.redirect("/login"); return; }

        String nombre = ctx.formParam("nombre");
        String correo = ctx.formParam("correo");
        String rut = ctx.formParam("rut");

        if (nombre == null || nombre.isBlank() || correo == null || correo.isBlank()
                || rut == null || rut.isBlank()) {
            List<Lector> lectores = lectorService.listarTodos();
            Map<String, Object> model = new HashMap<>();
            model.put("usuarioNombre", usuarioNombre);
            model.put("lectores", lectores);
            model.put("error", "Todos los campos son obligatorios");
            ctx.render("biblioteca/lectores.jte", model);
            return;
        }

        Lector nuevo = null;
        try {
            nuevo = lectorService.registrarLector(nombre, correo, rut);
        } catch (Exception e) {
            List<Lector> lectores = lectorService.listarTodos();
            Map<String, Object> model = new HashMap<>();
            model.put("usuarioNombre", usuarioNombre);
            model.put("lectores", lectores);
            model.put("error", e.getMessage());
            ctx.render("biblioteca/lectores.jte", model);
            return;
        }

        if (nuevo == null) {
            List<Lector> lectores = lectorService.listarTodos();
            Map<String, Object> model = new HashMap<>();
            model.put("usuarioNombre", usuarioNombre);
            model.put("lectores", lectores);
            model.put("error", "El RUT o correo ya se encuentra registrado");
            ctx.render("biblioteca/lectores.jte", model);
            return;
        }

        ctx.redirect("/biblioteca/lectores");
    }

    public void formularioEditar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) { ctx.redirect("/login"); return; }

        try {
            Long id = Long.parseLong(ctx.queryParam("id"));
            Lector lector = lectorService.buscarPorId(id);
            if (lector == null) {
                ctx.redirect("/biblioteca/lectores");
                return;
            }

            Map<String, Object> model = new HashMap<>();
            model.put("usuarioNombre", usuarioNombre);
            model.put("lector", lector);
            model.put("error", null);
            ctx.render("biblioteca/editar-lector.jte", model);
        } catch (Exception e) {
            ctx.redirect("/biblioteca/lectores");
        }
    }

    public void editar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) { ctx.redirect("/login"); return; }

        try {
            Long id = Long.parseLong(ctx.formParam("id"));
            String nombre = ctx.formParam("nombre");
            String correo = ctx.formParam("correo");
            String rut = ctx.formParam("rut");

            if (nombre == null || nombre.isBlank() || correo == null || correo.isBlank()
                    || rut == null || rut.isBlank()) {
                Lector lector = lectorService.buscarPorId(id);
                Map<String, Object> model = new HashMap<>();
                model.put("usuarioNombre", usuarioNombre);
                model.put("lector", lector);
                model.put("error", "Todos los campos son obligatorios");
                ctx.render("biblioteca/editar-lector.jte", model);
                return;
            }

            lectorService.actualizarLector(id, nombre.trim(), correo.trim(), rut.trim());
            ctx.redirect("/biblioteca/lectores");
        } catch (Exception e) {
            try {
                Long id = Long.parseLong(ctx.formParam("id"));
                Lector lector = lectorService.buscarPorId(id);
                Map<String, Object> model = new HashMap<>();
                model.put("usuarioNombre", usuarioNombre);
                model.put("lector", lector);
                model.put("error", e.getMessage());
                ctx.render("biblioteca/editar-lector.jte", model);
            } catch (Exception ex) {
                ctx.redirect("/biblioteca/lectores");
            }
        }
    }
}
