package cl.ucn.app.controller;

import cl.ucn.app.security.AutorizacionAdmin;

import cl.ucn.app.service.EspacioService;
import cl.ucn.app.service.ReservaException;
import cl.ucn.app.service.ReservaService;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

public class ReservaController {

    private static final Logger LOG = LoggerFactory.getLogger(ReservaController.class);

    // Claves de sesión para mostrar el resultado tras el redirect (patrón POST-redirect-GET).
    private static final String FLASH_EXITO = "flashMensaje";        // la consume "Mis reservas"
    private static final String FLASH_ERROR = "flashErrorReserva";
    private static final String FLASH_FORM = "flashFormReserva";

    private final EspacioService espacioService;
    private final ReservaService reservaService;

    public ReservaController(EspacioService espacioService, ReservaService reservaService) {
        this.espacioService = espacioService;
        this.reservaService = reservaService;
    }

    public void listarSalas(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        if (usuarioNombre == null) {
            ctx.redirect("/login");
            return;
        }

        String error = ctx.sessionAttribute(FLASH_ERROR);
        Map<String, String> form = ctx.sessionAttribute(FLASH_FORM);
        ctx.sessionAttribute(FLASH_ERROR, null);
        ctx.sessionAttribute(FLASH_FORM, null);
        if (form == null) {
            form = Map.of();
        }

        Map<String, Object> model = new HashMap<>();
        model.put("title", "Salas - SIGU-UCN");
        model.put("usuarioNombre", usuarioNombre);
        model.put("salas", espacioService.listarDisponibles());
        model.put("hoy", LocalDate.now().toString());
        model.put("error", error == null ? "" : error);
        // RNF-06: se conservan los datos ingresados en la sala donde ocurrió el error.
        model.put("salaConError", form.getOrDefault("espacioId", ""));
        model.put("fecha", form.getOrDefault("fecha", ""));
        model.put("horaInicio", form.getOrDefault("horaInicio", ""));
        model.put("horaFin", form.getOrDefault("horaFin", ""));
        model.put("motivo", form.getOrDefault("motivo", ""));

        model.put("esAdmin", AutorizacionAdmin.esAdmin(ctx.sessionAttribute("usuarioRol")));
        ctx.render("reservas/salas.jte", model);
    }

    public void crearReserva(Context ctx) {
        Long usuarioId = ctx.sessionAttribute("usuarioId");

        if (usuarioId == null) {
            ctx.redirect("/login");
            return;
        }

        Map<String, String> form = new HashMap<>();
        for (String campo : new String[]{"espacioId", "fecha", "horaInicio", "horaFin", "motivo"}) {
            String valor = ctx.formParam(campo);
            form.put(campo, valor == null ? "" : valor.trim());
        }

        try {
            Long espacioId = Long.valueOf(form.get("espacioId"));
            LocalDate fecha = LocalDate.parse(form.get("fecha"));
            LocalTime horaInicio = LocalTime.parse(form.get("horaInicio"));
            LocalTime horaFin = LocalTime.parse(form.get("horaFin"));

            reservaService.crearReserva(usuarioId, espacioId, fecha, horaInicio, horaFin, form.get("motivo"));

            ctx.sessionAttribute(FLASH_EXITO,
                    "Reserva creada correctamente. Quedó PENDIENTE hasta que el administrador la revise.");
            ctx.redirect("/reservas/mis-reservas");

        } catch (NumberFormatException | DateTimeParseException e) {
            volverConError(ctx, form, "Revise la sala, la fecha y las horas ingresadas.");
        } catch (ReservaException e) {
            volverConError(ctx, form, e.getMessage());
        } catch (RuntimeException e) {
            // RNF-07: se registra el detalle técnico y el usuario recibe un mensaje genérico.
            LOG.error("Error inesperado al crear una reserva para el usuario {}", usuarioId, e);
            volverConError(ctx, form, "No fue posible crear la reserva. Intente nuevamente más tarde.");
        }
    }

    private void volverConError(Context ctx, Map<String, String> form, String mensaje) {
        ctx.sessionAttribute(FLASH_ERROR, mensaje);
        ctx.sessionAttribute(FLASH_FORM, form);
        ctx.redirect("/reservas");
    }
}
