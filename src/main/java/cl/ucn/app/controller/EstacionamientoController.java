
package cl.ucn.app.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.ucn.app.service.ReservaEstacionamientoService;
import io.javalin.http.Context;

public class EstacionamientoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EstacionamientoController.class);

    private final ReservaEstacionamientoService reservaService;

    public EstacionamientoController() {
        this(new ReservaEstacionamientoService());
    }

    public EstacionamientoController(ReservaEstacionamientoService reservaService) {
        this.reservaService = Objects.requireNonNull(reservaService);
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

        } catch (DateTimeParseException | NumberFormatException | NullPointerException e) {
            mostrarError(ctx, "Revisa el vehículo, el estacionamiento y las fechas de la reserva.");
        } catch (IllegalArgumentException e) {
            mostrarError(ctx, e.getMessage());
        } catch (RuntimeException e) {
            LOGGER.error("No se pudo registrar la reserva del usuario {}", usuarioId, e);
            mostrarError(ctx, "No se pudo guardar la reserva. Inténtalo nuevamente.");
        }
    }

    private void mostrarError(Context ctx, String mensaje) {
        ctx.sessionAttribute("reservaError", mensaje);
        ctx.redirect("/estacionamientos/vehiculos/nuevo?errorReserva=1");
    }
}
