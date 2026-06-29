package cl.ucn.app.controller;

import cl.ucn.app.model.Espacio;
import cl.ucn.app.service.EspacioService;
import io.javalin.http.Context;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EspacioController {

    private final EspacioService espacioService;

    public EspacioController() {
        this.espacioService = new EspacioService();
    }

    public void listar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) {
            ctx.redirect("/login");
            return;
        }
        String usuarioRol = ctx.sessionAttribute("usuarioRol");
        if (!"ADMIN".equals(usuarioRol)) {
            ctx.status(403).result("Acceso denegado: Se requieren privilegios de administrador.");
            return;
        }

        List<Espacio> espacios = espacioService.listarTodos();
        Map<String, Object> model = new HashMap<>();
        model.put("title", "Ubicaciones - SIGU-UCN");
        model.put("usuarioNombre", usuarioNombre);
        model.put("usuarioRol", usuarioRol);
        model.put("espacios", espacios);
        model.put("error", ctx.queryParam("error"));
        model.put("success", ctx.queryParam("success"));
        ctx.render("espacios/lista.jte", model);
    }

    public void mostrarFormularioCrear(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) {
            ctx.redirect("/login");
            return;
        }
        String usuarioRol = ctx.sessionAttribute("usuarioRol");
        if (!"ADMIN".equals(usuarioRol)) {
            ctx.status(403).result("Acceso denegado: Se requieren privilegios de administrador.");
            return;
        }

        Map<String, Object> model = new HashMap<>();
        model.put("title", "Registrar Espacio - SIGU-UCN");
        model.put("usuarioNombre", usuarioNombre);
        model.put("usuarioRol", usuarioRol);
        model.put("error", "");
        ctx.render("espacios/formulario.jte", model);
    }

    public void mostrarFormularioEditar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) {
            ctx.redirect("/login");
            return;
        }
        String usuarioRol = ctx.sessionAttribute("usuarioRol");
        if (!"ADMIN".equals(usuarioRol)) {
            ctx.status(403).result("Acceso denegado: Se requieren privilegios de administrador.");
            return;
        }

        try {
            Long id = Long.parseLong(ctx.pathParam("id"));
            Espacio espacio = espacioService.buscarPorId(id);

            Map<String, Object> model = new HashMap<>();
            model.put("title", "Editar Espacio - SIGU-UCN");
            model.put("usuarioNombre", usuarioNombre);
            model.put("usuarioRol", usuarioRol);
            model.put("espacio", espacio);
            model.put("error", "");
            ctx.render("espacios/formulario.jte", model);

        } catch (IllegalArgumentException e) {
            ctx.redirect("/espacios?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        }
    }

    public void registrar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) {
            ctx.redirect("/login");
            return;
        }
        String usuarioRol = ctx.sessionAttribute("usuarioRol");
        if (!"ADMIN".equals(usuarioRol)) {
            ctx.status(403).result("Acceso denegado: Se requieren privilegios de administrador.");
            return;
        }

        try {
            String idStr = ctx.formParam("id");
            String nombre = ctx.formParam("nombre");
            String tipo = ctx.formParam("tipo");
            String capacidadStr = ctx.formParam("capacidad");
            String disponibleStr = ctx.formParam("disponible");

            if (nombre == null || nombre.isBlank()) {
                throw new IllegalArgumentException("El nombre del espacio es obligatorio.");
            }
            if (tipo == null || tipo.isBlank()) {
                throw new IllegalArgumentException("El tipo de espacio es obligatorio.");
            }
            if (capacidadStr == null || capacidadStr.isBlank()) {
                throw new IllegalArgumentException("La capacidad es obligatoria.");
            }

            Integer capacidad = Integer.parseInt(capacidadStr);
            boolean disponible = "on".equals(disponibleStr) || "true".equals(disponibleStr);

            if (idStr != null && !idStr.isBlank()) {
                Long id = Long.parseLong(idStr);
                espacioService.actualizar(id, nombre, tipo, capacidad, disponible);
                ctx.redirect("/espacios?success=" + URLEncoder.encode("Espacio actualizado exitosamente.", StandardCharsets.UTF_8));
            } else {
                Espacio espacio = new Espacio(nombre.trim(), tipo, capacidad, disponible);
                espacioService.registrar(espacio);
                ctx.redirect("/espacios?success=" + URLEncoder.encode("Espacio registrado exitosamente.", StandardCharsets.UTF_8));
            }

        } catch (NumberFormatException e) {
            ctx.redirect("/espacios?error=" + URLEncoder.encode("Capacidad o ID inválido.", StandardCharsets.UTF_8));
        } catch (IllegalArgumentException e) {
            ctx.redirect("/espacios?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            ctx.redirect("/espacios?error=" + URLEncoder.encode("Error inesperado: " + e.getMessage(), StandardCharsets.UTF_8));
        }
    }

    public void actualizar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) {
            ctx.redirect("/login");
            return;
        }
        String usuarioRol = ctx.sessionAttribute("usuarioRol");
        if (!"ADMIN".equals(usuarioRol)) {
            ctx.status(403).result("Acceso denegado: Se requieren privilegios de administrador.");
            return;
        }

        try {
            String idStr = ctx.formParam("id");
            String nombre = ctx.formParam("nombre");
            String tipo = ctx.formParam("tipo");
            String capacidadStr = ctx.formParam("capacidad");
            String disponibleStr = ctx.formParam("disponible");

            if (idStr == null || idStr.isBlank()) {
                throw new IllegalArgumentException("El ID del espacio es obligatorio.");
            }
            if (nombre == null || nombre.isBlank()) {
                throw new IllegalArgumentException("El nombre del espacio es obligatorio.");
            }
            if (tipo == null || tipo.isBlank()) {
                throw new IllegalArgumentException("El tipo de espacio es obligatorio.");
            }
            if (capacidadStr == null || capacidadStr.isBlank()) {
                throw new IllegalArgumentException("La capacidad es obligatoria.");
            }

            Long id = Long.parseLong(idStr);
            Integer capacidad = Integer.parseInt(capacidadStr);
            boolean disponible = "on".equals(disponibleStr) || "true".equals(disponibleStr);

            espacioService.actualizar(id, nombre, tipo, capacidad, disponible);

            ctx.redirect("/espacios?success=" + URLEncoder.encode("Espacio actualizado exitosamente.", StandardCharsets.UTF_8));

        } catch (NumberFormatException e) {
            ctx.redirect("/espacios?error=" + URLEncoder.encode("Capacidad o ID inválido.", StandardCharsets.UTF_8));
        } catch (IllegalArgumentException e) {
            ctx.redirect("/espacios?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            ctx.redirect("/espacios?error=" + URLEncoder.encode("Error inesperado: " + e.getMessage(), StandardCharsets.UTF_8));
        }
    }

    public void eliminar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) {
            ctx.redirect("/login");
            return;
        }
        String usuarioRol = ctx.sessionAttribute("usuarioRol");
        if (!"ADMIN".equals(usuarioRol)) {
            ctx.status(403).result("Acceso denegado: Se requieren privilegios de administrador.");
            return;
        }

        try {
            String idStr = ctx.formParam("id");
            if (idStr == null || idStr.isBlank()) {
                throw new IllegalArgumentException("El ID del espacio es obligatorio.");
            }

            Long id = Long.parseLong(idStr);
            espacioService.eliminar(id);
            ctx.redirect("/espacios?success=" + URLEncoder.encode("Espacio eliminado exitosamente.", StandardCharsets.UTF_8));

        } catch (IllegalArgumentException e) {
            ctx.redirect("/espacios?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            ctx.redirect("/espacios?error=" + URLEncoder.encode("Error inesperado: " + e.getMessage(), StandardCharsets.UTF_8));
        }
    }
}
