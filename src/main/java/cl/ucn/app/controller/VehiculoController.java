package cl.ucn.app.controller;

import java.util.Map;
import java.util.Objects;

import cl.ucn.app.service.ReservaEstacionamientoService;
import cl.ucn.app.service.ReservaEstacionamientoService.AccesoDenegadoException;
import cl.ucn.app.service.VehiculoService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import static java.util.Map.entry;

public class VehiculoController {
    private final VehiculoService vehiculoService;
    private final ReservaEstacionamientoService reservaService;

    public VehiculoController() {
        this(new VehiculoService(), new ReservaEstacionamientoService());
    }

    public VehiculoController(VehiculoService vehiculoService, ReservaEstacionamientoService reservaService) {
        this.vehiculoService = Objects.requireNonNull(vehiculoService);
        this.reservaService = Objects.requireNonNull(reservaService);
    }

    // formulario del registro
    public void mostrarFormulario(Context ctx) {

        Long usuarioId = ctx.sessionAttribute("usuarioId");
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        if (usuarioId == null || usuarioNombre == null) {
            ctx.redirect("/login");
            return;
        }

        String reservaError = ctx.sessionAttribute("reservaError");
        ctx.sessionAttribute("reservaError", null);

        mostrarVista(ctx, usuarioId, usuarioNombre, "Mis vehículos", "", "",
                "1".equals(ctx.queryParam("creado")) ? "Vehículo registrado correctamente." : "",
                "1".equals(ctx.queryParam("reserva")) ? "Reserva solicitada correctamente." : "",
                "1".equals(ctx.queryParam("errorReserva"))
                        ? (reservaError == null
                            ? "No se pudo registrar la reserva. Revisa los datos y la disponibilidad."
                            : reservaError)
                        : "");
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
            mostrarVista(ctx, usuarioId, usuarioNombre, "Registrar vehículo", patente == null ? "" : patente,
                    e.getMessage(), "", "", "");
        }
    }

    private void mostrarVista(Context ctx, Long usuarioId, String usuarioNombre, String titulo, String patente,
            String error, String mensaje, String reservaMensaje, String reservaError) {
        try {
            var reservas = reservaService.listarPropias(usuarioId);
            var aviso = ReservaEstacionamientoController.consumirAviso(ctx,
                    ReservaEstacionamientoController.AVISO_PERSONAL);
            ctx.render("vehiculos/registrar.jte", Map.ofEntries(
                    entry("title", titulo),
                    entry("usuarioNombre", usuarioNombre),
                    entry("mostrarAccesoAdmin", "ADMIN".equals(ctx.sessionAttribute("usuarioRol"))),
                    entry("patente", patente),
                    entry("error", error),
                    entry("mensaje", mensaje),
                    entry("vehiculos", vehiculoService.listarVehiculos(usuarioId)),
                    entry("estacionamientos", reservaService.listarDisponiblesParaUsuario(usuarioId)),
                    entry("reservaMensaje", reservaMensaje),
                    entry("reservaError", reservaError),
                    entry("reservas", reservas),
                    entry("mensajeCancelacion", aviso.mensaje()),
                    entry("errorCancelacion", aviso.error())
            ));
        } catch (AccesoDenegadoException accesoDenegado) {
            ctx.status(HttpStatus.FORBIDDEN).result(accesoDenegado.getMessage());
        }
    }
}
