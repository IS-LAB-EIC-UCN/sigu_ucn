package cl.ucn.app.controller.biblioteca;

import cl.ucn.app.exceptions.ValidacionException;
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

        List<Lector> lectores = lectorService.listarTodos();
        Map<String, Object> model = new HashMap<>();
        model.put("usuarioNombre", usuarioNombre);
        model.put("usuarioRol", ctx.sessionAttribute("usuarioRol"));
        model.put("lectores", lectores);
        model.put("error", null);
        ctx.render("biblioteca/lectores.jte", model);
    }

    public void formulario(Context ctx) {
        ctx.redirect("/biblioteca/lectores");
    }

    public void registrar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

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
            model.put("nombre", nombre);
            model.put("correo", correo);
            model.put("rut", rut);
            ctx.render("biblioteca/lectores.jte", model);
            return;
        }

        try {
            lectorService.registrarLector(nombre, correo, rut);
            ctx.redirect("/biblioteca/lectores");
        } catch (ValidacionException e) {
            String msg = e.getMessage();
            // Limpiar el campo en error para que el placeholder con formato de ejemplo sea visible
            String rutPersistido = msg.toLowerCase().contains("rut") ? "" : rut;
            String correoPersistido = msg.toLowerCase().contains("correo") ? "" : correo;
            String nombrePersistido = msg.toLowerCase().contains("nombre") ? "" : nombre;
            List<Lector> lectores = lectorService.listarTodos();
            Map<String, Object> model = new HashMap<>();
            model.put("usuarioNombre", usuarioNombre);
            model.put("lectores", lectores);
            model.put("error", e.getMessage());
            model.put("nombre", nombrePersistido);
            model.put("correo", correoPersistido);
            model.put("rut", rutPersistido);
            ctx.render("biblioteca/lectores.jte", model);
        }
    }

    public void formularioEditar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

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
    }

    public void editar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

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

        try {
            lectorService.actualizarLector(id, nombre.trim(), correo.trim(), rut.trim());
            ctx.redirect("/biblioteca/lectores");
        } catch (ValidacionException e) {
            Lector lector = lectorService.buscarPorId(id);
            String msg = e.getMessage();
            if (lector != null) {
                lector.setNombre(msg.toLowerCase().contains("nombre") ? "" : nombre);
                lector.setCorreo(msg.toLowerCase().contains("correo") ? "" : correo);
                lector.setRut(msg.toLowerCase().contains("rut") ? "" : rut);
            }
            Map<String, Object> model = new HashMap<>();
            model.put("usuarioNombre", usuarioNombre);
            model.put("lector", lector);
            model.put("error", e.getMessage());
            ctx.render("biblioteca/editar-lector.jte", model);
        }
    }

    public void eliminar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        Long id = Long.parseLong(ctx.queryParam("id"));
        lectorService.eliminarLector(id);
        ctx.redirect("/biblioteca/lectores");
    }
}
