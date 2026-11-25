
package cl.ucn.app.controller;

import java.time.LocalDateTime;

import cl.ucn.app.service.ReservaEstacionamientoService;
import io.javalin.http.Context;

public class EstacionamientoController {

    private final ReservaEstacionamientoService reservaService;

    public EstacionamientoController() {
        this.reservaService = new ReservaEstacionamientoService();
    }

    public void registrarReserva(Context ctx) {

        Long usuarioId = ctx.sessionAttribute("usuarioId");

        if (usuarioId == null) {
            ctx.redirect("/login");
            return;
        }

        try {
            Long vehiculoId = Long.valueOf(ctx.formParam("vehiculoId"));
            Long estacionamientoId = Long.valueOf(ctx.formParam("estacionamientoId"));

            LocalDateTime inicio = LocalDateTime.parse(
                    ctx.formParam("fechaHoraInicio"));

            LocalDateTime fin = LocalDateTime.parse(
                    ctx.formParam("fechaHoraFin"));

            reservaService.registrarReserva(
                    usuarioId,
                    vehiculoId,
                    estacionamientoId,
                    inicio,
                    fin
            );

            ctx.redirect("/estacionamientos/vehiculos/nuevo?reserva=1");

        } catch (IllegalArgumentException | NullPointerException e) {
            ctx.redirect("/estacionamientos/vehiculos/nuevo?errorReserva=1");
        }
    }
}
