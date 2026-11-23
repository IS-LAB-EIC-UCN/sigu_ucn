package cl.ucn.app.controller;

import cl.ucn.app.security.AutorizacionAdmin;

import cl.ucn.app.model.Reserva;
import cl.ucn.app.service.EspacioService;
import cl.ucn.app.service.ReservaException;
import cl.ucn.app.service.ReservaService;
import io.javalin.http.Context;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MisReservasController {

    private static final List<String> ESTADOS = List.of("PENDIENTE", "APROBADA", "RECHAZADA", "CANCELADA");
    private final EspacioService espacioService;
    private final ReservaService reservaService;

    public MisReservasController(EspacioService espacioService, ReservaService reservaService) {
        this.espacioService = espacioService;
        this.reservaService = reservaService;
    }

    public void misReservas(Context ctx) {
        Long usuarioId = ctx.sessionAttribute("usuarioId");
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioId == null) {
            ctx.redirect("/login");
            return;
        }
        Long salaId = parsearLong(ctx.queryParam("sala"));
        LocalDate fecha = parsearFecha(ctx.queryParam("fecha"));
        String estado = parsearEstado(ctx.queryParam("estado"));
        List<Reserva> reservas = reservaService.listarMisReservas(usuarioId, salaId, fecha, estado);
        Set<Long> idsCancelables = reservas.stream()
                .filter(reservaService::puedeCancelarse)
                .map(Reserva::getId)
                .collect(Collectors.toSet());
        Map<String, Object> model = new HashMap<>();
        model.put("title", "Mis reservas - SIGU-UCN");
        model.put("usuarioNombre", usuarioNombre);
        model.put("reservas", reservas);
        model.put("idsCancelables", idsCancelables);
        model.put("salas", espacioService.listarDisponibles());
        model.put("estados", ESTADOS);
        model.put("salaSeleccionada", salaId == null ? "" : String.valueOf(salaId));
        model.put("fechaSeleccionada", fecha == null ? "" : fecha.toString());
        model.put("estadoSeleccionado", estado == null ? "" : estado);
        model.put("mensaje", consumirFlash(ctx, "flashMensaje"));
        model.put("error", consumirFlash(ctx, "flashError"));

        model.put("esAdmin", AutorizacionAdmin.esAdmin(ctx.sessionAttribute("usuarioRol")));
        ctx.render("reservas/mis-reservas.jte", model);
    }

    public void cancelar(Context ctx) {
        Long usuarioId = ctx.sessionAttribute("usuarioId");

        if (usuarioId == null) {
            ctx.redirect("/login");
            return;
        }
        try {
            Long reservaId = Long.valueOf(ctx.pathParam("id"));
            reservaService.cancelar(reservaId, usuarioId);
            ctx.sessionAttribute("flashMensaje", "La reserva fue cancelada y el horario quedó libre.");
        } catch (NumberFormatException e) {
            ctx.sessionAttribute("flashError", "La reserva solicitada no existe.");
        } catch (ReservaException e) {
            ctx.sessionAttribute("flashError", e.getMessage());
        }
        ctx.redirect("/reservas/mis-reservas");
    }

    private String consumirFlash(Context ctx, String clave) {
        String valor = ctx.sessionAttribute(clave);
        ctx.sessionAttribute(clave, null);
        return valor == null ? "" : valor;
    }
    private Long parsearLong(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(valor.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    private LocalDate parsearFecha(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(valor.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    private String parsearEstado(String valor) {
        return valor != null && ESTADOS.contains(valor) ? valor : null;
    }
}