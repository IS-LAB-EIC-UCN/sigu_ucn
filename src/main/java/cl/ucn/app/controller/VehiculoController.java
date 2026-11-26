package cl.ucn.app.controller;

import java.util.List;
import java.util.Map;

import cl.ucn.app.model.Estacionamiento;
import cl.ucn.app.model.Vehiculo;
import cl.ucn.app.service.ReservaEstacionamientoService;
import cl.ucn.app.service.VehiculoService;
import io.javalin.http.Context;

public class VehiculoController {
    private final VehiculoService vehiculoService;
    private final ReservaEstacionamientoService reservaService;

    public VehiculoController() {
        this.vehiculoService = new VehiculoService();
        this.reservaService = new ReservaEstacionamientoService();
    }

    // formulario del registro
    public void mostrarFormulario(Context ctx) {

        Long usuarioId = ctx.sessionAttribute("usuarioId");
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        if (usuarioId == null || usuarioNombre == null) {
            ctx.redirect("/login");
            return;
        }

        List<Vehiculo> vehiculos =
            vehiculoService.listarVehiculos(usuarioId);
        List<Estacionamiento> estacionamientos =
            reservaService.listarDisponiblesParaUsuario(usuarioId);

        String reservaError = ctx.sessionAttribute("reservaError");
        ctx.sessionAttribute("reservaError", null);

        ctx.render("vehiculos/registrar.jte", Map.of(
                "title", "Mis vehículos",
                "usuarioNombre", usuarioNombre,
                "mostrarAccesoAdmin", "ADMIN".equals(ctx.sessionAttribute("usuarioRol")),
                "patente", "",
                "error", "",
                "mensaje", "1".equals(ctx.queryParam("creado"))
                        ? "Vehículo registrado correctamente."
                        : "",
                "vehiculos", vehiculos,
                "estacionamientos", estacionamientos,
                "reservaMensaje", "1".equals(ctx.queryParam("reserva"))
                        ? "Reserva solicitada correctamente."
                        : "",
                "reservaError", "1".equals(ctx.queryParam("errorReserva"))
                        ? (reservaError == null
                            ? "No se pudo registrar la reserva. Revisa los datos y la disponibilidad."
                            : reservaError)
                        : ""
        ));
    }

    public void registrarVehiculo(Context ctx) {

        Long usuarioId = ctx.sessionAttribute("usuarioId");
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        if (usuarioId == null || usuarioNombre == null) {
            ctx.redirect("/login");
            return;
        }

        String patente = ctx.formParam("patente");

        try {
            vehiculoService.registrarVehiculo(patente, usuarioId);

            ctx.redirect("/estacionamientos/vehiculos/nuevo?creado=1");

        } catch (IllegalArgumentException e) {

            ctx.status(400);

            List<Vehiculo> vehiculos =
            vehiculoService.listarVehiculos(usuarioId);
            List<Estacionamiento> estacionamientos =
            reservaService.listarDisponiblesParaUsuario(usuarioId);

            ctx.render("vehiculos/registrar.jte", Map.of(
                    "title", "Registrar vehículo",
                    "usuarioNombre", usuarioNombre,
                    "mostrarAccesoAdmin", "ADMIN".equals(ctx.sessionAttribute("usuarioRol")),
                    "patente", patente == null ? "" : patente,
                    "error", e.getMessage(),
                    "mensaje", "",
                    "vehiculos", vehiculos,
                    "estacionamientos", estacionamientos,
                    "reservaMensaje", "",
                    "reservaError", ""
            ));
        }
    }
}
