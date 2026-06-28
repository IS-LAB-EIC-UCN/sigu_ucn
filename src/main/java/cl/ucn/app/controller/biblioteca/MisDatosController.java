package cl.ucn.app.controller.biblioteca;

import cl.ucn.app.auth.LectorSessionHelper;
import cl.ucn.app.exceptions.ValidacionException;
import cl.ucn.app.model.biblioteca.Lector;
import cl.ucn.app.service.biblioteca.LectorService;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class MisDatosController {

    private final LectorService lectorService = new LectorService();

    public void formulario(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        String usuarioCorreo = ctx.sessionAttribute("usuarioCorreo");
        String returnTo = ctx.queryParam("returnTo");
        if (returnTo == null || returnTo.isEmpty()) {
            returnTo = "/biblioteca/historial";
        }

        Lector existente = LectorSessionHelper.obtenerExistente(ctx);
        if (existente != null) {
            Map<String, Object> model = new HashMap<>();
            model.put("usuarioNombre", usuarioNombre);
            model.put("usuarioRol", ctx.sessionAttribute("usuarioRol"));
            model.put("lector", existente);
            ctx.render("biblioteca/mis-datos.jte", model);
            return;
        }

        Map<String, Object> model = new HashMap<>();
        model.put("usuarioNombre", usuarioNombre);
        model.put("usuarioRol", ctx.sessionAttribute("usuarioRol"));
        model.put("returnTo", returnTo);
        model.put("error", null);
        model.put("nombre", usuarioNombre != null ? usuarioNombre : "");
        model.put("correo", usuarioCorreo != null ? usuarioCorreo : "");
        model.put("rut", "");
        model.put("lector", null);
        ctx.render("biblioteca/mis-datos.jte", model);
    }

    public void guardar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        String returnTo = ctx.formParam("returnTo");
        if (returnTo == null || returnTo.isEmpty()) {
            returnTo = ctx.queryParam("returnTo");
        }
        if (returnTo == null || returnTo.isEmpty()) {
            returnTo = "/biblioteca/historial";
        }

        String nombre = ctx.formParam("nombre");
        String correo = ctx.formParam("correo");
        String rut = ctx.formParam("rut");

        if (nombre == null || nombre.isBlank()
                || correo == null || correo.isBlank()
                || rut == null || rut.isBlank()) {
            Map<String, Object> model = new HashMap<>();
            model.put("usuarioNombre", usuarioNombre);
            model.put("usuarioRol", ctx.sessionAttribute("usuarioRol"));
            model.put("returnTo", returnTo);
            model.put("error", "Todos los campos son obligatorios");
            model.put("nombre", nombre);
            model.put("correo", correo);
            model.put("rut", rut);
            model.put("lector", null);
            ctx.render("biblioteca/mis-datos.jte", model);
            return;
        }

        try {
            Lector nuevo = lectorService.crearDesdeUsuario(nombre.trim(), correo.trim(), rut.trim());
            LectorSessionHelper.guardarEnSesion(ctx, nuevo);
        } catch (ValidacionException e) {
            String msg = e.getMessage();
            String correoPersistido = msg.toLowerCase().contains("correo") ? "" : correo;
            String rutPersistido = msg.toLowerCase().contains("rut") ? "" : rut;
            String nombrePersistido = msg.toLowerCase().contains("nombre") ? "" : nombre;
            Map<String, Object> model = new HashMap<>();
            model.put("usuarioNombre", usuarioNombre);
            model.put("usuarioRol", ctx.sessionAttribute("usuarioRol"));
            model.put("returnTo", returnTo);
            model.put("error", e.getMessage());
            model.put("nombre", nombrePersistido);
            model.put("correo", correoPersistido);
            model.put("rut", rutPersistido);
            model.put("lector", null);
            ctx.render("biblioteca/mis-datos.jte", model);
            return;
        }
        ctx.redirect(returnTo);
    }
}
