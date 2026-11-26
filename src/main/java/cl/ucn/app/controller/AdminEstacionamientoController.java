package cl.ucn.app.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.ucn.app.model.Estacionamiento;
import cl.ucn.app.service.EstacionamientoService;
import cl.ucn.app.service.EstacionamientoService.AccesoDenegadoException;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class AdminEstacionamientoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminEstacionamientoController.class);
    private static final String RUTA = "/estacionamientos/admin";
    private static final String ATRIBUTO_AVISO = "adminEstacionamientoAviso";

    private final EstacionamientoService service;

    public AdminEstacionamientoController() {
        this(new EstacionamientoService());
    }

    public AdminEstacionamientoController(EstacionamientoService service) {
        this.service = Objects.requireNonNull(service);
    }

    public void mostrarCatalogo(Context ctx) {
        Long usuarioId = ctx.sessionAttribute("usuarioId");
        if (usuarioId == null) {
            ctx.redirect("/login");
            return;
        }

        try {
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("estacionamientos", service.listar(usuarioId));
            modelo.put("usuarioNombre", ctx.sessionAttribute("usuarioNombre"));
            Aviso aviso = ctx.sessionAttribute(ATRIBUTO_AVISO);
            ctx.sessionAttribute(ATRIBUTO_AVISO, null);
            modelo.put("mensaje", aviso != null && !aviso.error() ? aviso.texto() : "");
            modelo.put("error", aviso != null && aviso.error() ? aviso.texto() : "");
            ctx.render("estacionamientos/admin.jte", modelo);
        } catch (AccesoDenegadoException error) {
            ctx.status(403).result(error.getMessage());
        }
    }

    public void registrar(Context ctx) {
        procesarCambio(ctx, usuarioId -> {
            Estacionamiento espacio = service.registrar(usuarioId, ctx.formParam("codigo"),
                    ctx.formParam("tipo"), ctx.formParam("habilitado"));
            return "El estacionamiento " + espacio.getCodigo() + " se registró correctamente.";
        });
    }

    public void cambiarHabilitacion(Context ctx) {
        procesarCambio(ctx, usuarioId -> {
            Estacionamiento espacio = service.cambiarHabilitacion(usuarioId, leerId(ctx.pathParam("id")),
                    ctx.formParam("habilitado"));
            return "El estacionamiento " + espacio.getCodigo()
                    + (espacio.getHabilitado() ? " quedó habilitado." : " quedó deshabilitado.");
        });
    }

    private void procesarCambio(Context ctx, Function<Long, String> operacion) {
        Long usuarioId = ctx.sessionAttribute("usuarioId");
        if (usuarioId == null) {
            ctx.redirect("/login");
            return;
        }

        Aviso aviso;
        try {
            aviso = new Aviso(operacion.apply(usuarioId), false);
        } catch (AccesoDenegadoException error) {
            ctx.status(403).result(error.getMessage());
            return;
        } catch (IllegalArgumentException error) {
            aviso = new Aviso(error.getMessage(), true);
        } catch (RuntimeException error) {
            LOGGER.error("No se pudo guardar el cambio de estacionamiento del usuario {}", usuarioId, error);
            aviso = new Aviso("No se pudo guardar el cambio. Inténtalo nuevamente.", true);
        }
        ctx.sessionAttribute(ATRIBUTO_AVISO, aviso);
        ctx.redirect(RUTA, HttpStatus.SEE_OTHER);
    }

    private Long leerId(String id) {
        try {
            return Long.valueOf(id);
        } catch (NumberFormatException error) {
            return null;
        }
    }

    private record Aviso(String texto, boolean error) {
    }
}
