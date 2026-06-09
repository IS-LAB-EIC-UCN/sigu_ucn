package cl.ucn.app.controller;

import cl.ucn.app.model.Espacio;
import cl.ucn.app.model.Reserva;
import cl.ucn.app.model.Vehiculo;
import cl.ucn.app.service.EstacionamientoService;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EstacionamientoController {

    private final EstacionamientoService estacionamientoService;

    public EstacionamientoController() {
        this.estacionamientoService = new EstacionamientoService();
    }

    private boolean esAdmin(Context ctx) {
        String usuarioRol = ctx.sessionAttribute("usuarioRol");
        return "ADMIN".equals(usuarioRol);
    }

    private boolean bloquearSiNoEsAdmin(Context ctx) {
        if (!esAdmin(ctx)) {
            ctx.status(403);
            ctx.result("Acceso denegado");
            return true;
        }
        return false;
    }

    public void showEstacionamientos(Context ctx) {

        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        String usuarioRol = ctx.sessionAttribute("usuarioRol");

        List<Espacio> espacios = estacionamientoService.obtenerEspacios();

        Map<String, Object> model = new HashMap<>();

        model.put("title", "Estacionamientos");
        model.put("usuarioNombre", usuarioNombre);
        model.put("usuarioRol", usuarioRol);
        model.put("espacios", espacios);

        ctx.render("estacionamientos.jte", model);
    }

    public void showNuevo(Context ctx) {

        if (bloquearSiNoEsAdmin(ctx)) {
            return;
        }

        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        Map<String, Object> model = new HashMap<>();

        model.put("title", "Nuevo Estacionamiento");
        model.put("usuarioNombre", usuarioNombre);

        ctx.render("nuevo-estacionamiento.jte", model);
    }

    public void guardar(Context ctx) {

        if (bloquearSiNoEsAdmin(ctx)) {
            return;
        }

        Espacio espacio = new Espacio(
                ctx.formParam("nombre"),
                ctx.formParam("tipo"),
                Integer.parseInt(ctx.formParam("capacidad")),
                true
        );

        estacionamientoService.guardar(espacio);

        ctx.redirect("/estacionamientos");
    }

    public void eliminar(Context ctx) {

        if (bloquearSiNoEsAdmin(ctx)) {
            return;
        }

        Long id = Long.parseLong(ctx.pathParam("id"));

        estacionamientoService.eliminar(id);

        ctx.redirect("/estacionamientos");
    }

    public void showEditar(Context ctx) {

        if (bloquearSiNoEsAdmin(ctx)) {
            return;
        }

        Long id = Long.parseLong(ctx.pathParam("id"));

        Espacio espacio = estacionamientoService.obtenerPorId(id);

        Map<String, Object> model = new HashMap<>();

        model.put("title", "Editar Estacionamiento");
        model.put("usuarioNombre", ctx.sessionAttribute("usuarioNombre"));
        model.put("espacio", espacio);

        ctx.render("editar-estacionamiento.jte", model);
    }

    public void actualizar(Context ctx) {

        if (bloquearSiNoEsAdmin(ctx)) {
            return;
        }

        Long id = Long.parseLong(ctx.pathParam("id"));

        Espacio espacio = estacionamientoService.obtenerPorId(id);

        espacio.setNombre(ctx.formParam("nombre"));
        espacio.setTipo(ctx.formParam("tipo"));
        espacio.setCapacidad(Integer.parseInt(ctx.formParam("capacidad")));

        estacionamientoService.actualizar(espacio);

        ctx.redirect("/estacionamientos");
    }

    public void showReservar(Context ctx) {

        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        String usuarioRol = ctx.sessionAttribute("usuarioRol");
        Long usuarioId = ctx.sessionAttribute("usuarioId");

        if (usuarioId == null) {
            ctx.redirect("/login");
            return;
        }

        List<Espacio> espacios = estacionamientoService.obtenerEspaciosDisponibles();
        List<Vehiculo> vehiculos = estacionamientoService.obtenerVehiculosPorUsuario(usuarioId);

        Map<String, Object> model = new HashMap<>();

        model.put("title", "Reservar Estacionamiento");
        model.put("usuarioNombre", usuarioNombre);
        model.put("usuarioRol", usuarioRol);
        model.put("espacios", espacios);
        model.put("vehiculos", vehiculos);

        ctx.render("reservar-estacionamiento.jte", model);
    }

    public void reservar(Context ctx) {

        try {
            Long usuarioId = ctx.sessionAttribute("usuarioId");

            if (usuarioId == null) {
                ctx.redirect("/login");
                return;
            }

            Long espacioId = Long.parseLong(ctx.formParam("espacioId"));
            Long vehiculoId = Long.parseLong(ctx.formParam("vehiculoId"));

            estacionamientoService.reservarEspacio(usuarioId, espacioId, vehiculoId);

            ctx.redirect("/mis-reservas/estacionamientos");

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500);
            ctx.result("Error al reservar: " + e.getMessage());
        }
    }

    public void misReservas(Context ctx) {

        Long usuarioId = ctx.sessionAttribute("usuarioId");

        if (usuarioId == null) {
            ctx.redirect("/login");
            return;
        }

        List<Reserva> reservas = estacionamientoService.obtenerReservasPorUsuario(usuarioId);

        Map<String, Object> model = new HashMap<>();

        model.put("title", "Mis Reservas");
        model.put("usuarioNombre", ctx.sessionAttribute("usuarioNombre"));
        model.put("usuarioRol", ctx.sessionAttribute("usuarioRol"));
        model.put("reservas", reservas);

        ctx.render("mis-reservas-estacionamiento.jte", model);
    }
}