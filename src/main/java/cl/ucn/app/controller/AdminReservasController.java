package cl.ucn.app.controller;

import cl.ucn.app.model.Reserva;
import cl.ucn.app.security.AutorizacionAdmin;
import cl.ucn.app.service.AdminReservaService;
import cl.ucn.app.service.ReservaException;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador del panel de administración para aprobar o rechazar reservas pendientes (HU-02, RF-07).
 */
public class AdminReservasController {

    private static final Logger log = LoggerFactory.getLogger(AdminReservasController.class);
    private static final String RUTA_PENDIENTES = "/admin/reservas/pendientes";

    private final AdminReservaService adminReservaService;

    public AdminReservasController(AdminReservaService adminReservaService) {
        this.adminReservaService = adminReservaService;
    }

    public void listarPendientes(Context ctx) {
        if (!verificarAdmin(ctx)) {
            return;
        }
        List<Reserva> pendientes = adminReservaService.listarPendientes();
        Map<String, Object> model = new HashMap<>();
        model.put("title", "Reservas pendientes - SIGU-UCN");
        model.put("usuarioNombre", ctx.sessionAttribute("usuarioNombre"));
        model.put("reservas", pendientes);
        model.put("mensaje", consumirFlash(ctx, "flashMensaje"));
        model.put("error", consumirFlash(ctx, "flashError"));

        ctx.render("admin/pendientes.jte", model);
    }

    public void aprobar(Context ctx) {
        if (!verificarAdmin(ctx)) {
            return;
        }
        try {
            Long reservaId = Long.valueOf(ctx.pathParam("id"));
            adminReservaService.aprobar(reservaId);
            ctx.sessionAttribute("flashMensaje", "Reserva aprobada.");
        } catch (NumberFormatException e) {
            ctx.sessionAttribute("flashError", "Identificador de reserva inválido.");
        } catch (ReservaException e) {
            ctx.sessionAttribute("flashError", e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al aprobar la reserva {}", ctx.pathParam("id"), e);
            ctx.sessionAttribute("flashError", "Ocurrió un error inesperado. Intente nuevamente.");
        }
        ctx.redirect(RUTA_PENDIENTES);
    }

    public void rechazar(Context ctx) {
        if (!verificarAdmin(ctx)) {
            return;
        }
        try {
            Long reservaId = Long.valueOf(ctx.pathParam("id"));
            String motivo = ctx.formParam("motivo");
            adminReservaService.rechazar(reservaId, motivo);
            ctx.sessionAttribute("flashMensaje", "Reserva rechazada.");
        } catch (NumberFormatException e) {
            ctx.sessionAttribute("flashError", "Identificador de reserva inválido.");
        } catch (ReservaException e) {
            ctx.sessionAttribute("flashError", e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al rechazar la reserva {}", ctx.pathParam("id"), e);
            ctx.sessionAttribute("flashError", "Ocurrió un error inesperado. Intente nuevamente.");
        }
        ctx.redirect(RUTA_PENDIENTES);
    }

    private boolean verificarAdmin(Context ctx) {
        Long usuarioId = ctx.sessionAttribute("usuarioId");
        if (usuarioId == null) {
            ctx.redirect("/login");
            return false;
        }
        String usuarioRol = ctx.sessionAttribute("usuarioRol");
        if (!AutorizacionAdmin.esAdmin(usuarioRol)) {
            Map<String, Object> model = new HashMap<>();
            model.put("title", "Acceso denegado - SIGU-UCN");
            model.put("usuarioNombre", ctx.sessionAttribute("usuarioNombre"));
            ctx.status(403).render("admin/acceso-denegado.jte", model);
            return false;
        }
        return true;
    }

    private String consumirFlash(Context ctx, String clave) {
        String valor = ctx.sessionAttribute(clave);
        ctx.sessionAttribute(clave, null);
        return valor == null ? "" : valor;
    }
}
