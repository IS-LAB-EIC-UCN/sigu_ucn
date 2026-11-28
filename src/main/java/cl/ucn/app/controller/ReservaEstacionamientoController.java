package cl.ucn.app.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.ucn.app.service.ReservaEstacionamientoService;
import cl.ucn.app.service.ReservaEstacionamientoService.AccesoDenegadoException;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class ReservaEstacionamientoController {
    static final String AVISO_PERSONAL = "cancelacionReservaPersonal";
    private static final String AVISO_ADMIN = "cancelacionReservaAdmin";
    private static final String RUTA_PERSONAL = "/estacionamientos/vehiculos/nuevo#mis-reservas";
    private static final String RUTA_ADMIN = "/estacionamientos/admin/reservas";
    private static final Logger LOGGER = LoggerFactory.getLogger(ReservaEstacionamientoController.class);

    private final ReservaEstacionamientoService service;

    public ReservaEstacionamientoController() {
        this(new ReservaEstacionamientoService());
    }

    public ReservaEstacionamientoController(ReservaEstacionamientoService service) {
        this.service = Objects.requireNonNull(service);
    }

    public void mostrarAdministracion(Context ctx) {
        Long usuarioId = ctx.sessionAttribute("usuarioId");
        if (usuarioId == null) {
            ctx.redirect("/login");
            return;
        }

        try {
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("reservas", service.listarParaAdministracion(usuarioId));
            modelo.put("usuarioNombre", ctx.sessionAttribute("usuarioNombre"));
            Aviso aviso = consumirAviso(ctx, AVISO_ADMIN);
            modelo.put("mensajeCancelacion", aviso.mensaje());
            modelo.put("errorCancelacion", aviso.error());
            ctx.render("estacionamientos/reservas-admin.jte", modelo);
        } catch (AccesoDenegadoException error) {
            ctx.status(HttpStatus.FORBIDDEN).result(error.getMessage());
        }
    }

    public void cancelarPropia(Context ctx) {
        cancelar(ctx, service::cancelarPropia, AVISO_PERSONAL, RUTA_PERSONAL);
    }

    public void cancelarComoAdministrador(Context ctx) {
        cancelar(ctx, service::cancelarComoAdministrador, AVISO_ADMIN, RUTA_ADMIN);
    }

    private void cancelar(Context ctx, BiConsumer<Long, Long> operacion, String atributoAviso, String destino) {
        Long usuarioId = ctx.sessionAttribute("usuarioId");
        if (usuarioId == null) {
            ctx.redirect("/login");
            return;
        }

        Long reservaId = leerId(ctx.pathParam("id"));
        Aviso aviso;
        try {
            operacion.accept(usuarioId, reservaId);
            aviso = new Aviso("Reserva cancelada correctamente.", "");
        } catch (AccesoDenegadoException error) {
            ctx.status(HttpStatus.FORBIDDEN).result(error.getMessage());
            return;
        } catch (IllegalArgumentException error) {
            aviso = new Aviso("", error.getMessage());
        } catch (RuntimeException error) {
            LOGGER.error("No se pudo cancelar la reserva {} para el usuario {}", reservaId, usuarioId, error);
            aviso = new Aviso("", "No se pudo cancelar la reserva. Inténtalo nuevamente.");
        }
        ctx.sessionAttribute(atributoAviso, aviso);
        ctx.redirect(destino, HttpStatus.SEE_OTHER);
    }

    private Long leerId(String id) {
        try {
            return Long.valueOf(id);
        } catch (NumberFormatException error) {
            return null;
        }
    }

    static Aviso consumirAviso(Context ctx, String atributo) {
        Aviso aviso = ctx.sessionAttribute(atributo);
        ctx.sessionAttribute(atributo, null);
        return aviso == null ? new Aviso("", "") : aviso;
    }

    record Aviso(String mensaje, String error) {
    }
}
