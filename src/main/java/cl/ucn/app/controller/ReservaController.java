package cl.ucn.app.controller;

import cl.ucn.app.service.EspacioService;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

import cl.ucn.app.repository.ReservaRepository;
import cl.ucn.app.service.ReservaService;
import java.time.Clock;

import cl.ucn.app.service.ReservaException;
import java.time.LocalDate;
import java.time.LocalTime;

public class ReservaController {

    private final EspacioService espacioService;
    private final ReservaService reservaService;

    public ReservaController(EspacioService espacioService) {
        this.espacioService = espacioService;
        this.reservaService = new ReservaService(
            new ReservaRepository(),
            Clock.systemDefaultZone()
        );
    }


    public void listarSalas(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        if (usuarioNombre == null) {
            ctx.redirect("/login");
            return;
        }

        Map<String, Object> model = new HashMap<>();
        model.put("title", "Salas - SIGU-UCN");
        model.put("usuarioNombre", usuarioNombre);
        model.put("salas", espacioService.listarDisponibles());

        ctx.render("reservas/salas.jte", model);
    }


    public void crearReserva(Context ctx) {
        Long usuarioId = ctx.sessionAttribute("usuarioId");

        if (usuarioId == null) {
            ctx.redirect("/login");
            return;
        }

        try {
            Long espacioId = Long.valueOf(ctx.formParam("espacioId"));
            LocalDate fecha = LocalDate.parse(ctx.formParam("fecha"));
            LocalTime horaInicio = LocalTime.parse(ctx.formParam("horaInicio"));
            LocalTime horaFin = LocalTime.parse(ctx.formParam("horaFin"));
            String motivo = ctx.formParam("motivo");

            reservaService.crearReserva(
                usuarioId,
                espacioId,
                fecha,
                horaInicio,
                horaFin,
                motivo
            );

            ctx.redirect("/reservas/mis-reservas");

        } catch (ReservaException | IllegalArgumentException e) {
            ctx.status(400);
            ctx.result("No se pudo crear la reserva: " + e.getMessage());
        }
    }

}