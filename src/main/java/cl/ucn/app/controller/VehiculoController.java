package cl.ucn.app.controller;

import cl.ucn.app.service.VehiculoService;
import io.javalin.http.Context;
import java.util.Map;

public class VehiculoController {
    private final VehiculoService vehiculoService;

    public VehiculoController() {
        this.vehiculoService = new VehiculoService();
    }

    // formulario del registro
    public void mostrarFormulario(Context ctx) {

        Long usuarioId = ctx.sessionAttribute("usuarioId");
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        if (usuarioId == null || usuarioNombre == null) {
            ctx.redirect("/login");
            return;
        }

        ctx.render("vehiculos/registrar.jte", Map.of(
                "title", "Registrar vehículo",
                "usuarioNombre", usuarioNombre,
                "patente", "",
                "error", "",
                "mensaje", "1".equals(ctx.queryParam("creado"))
                        ? "Vehículo registrado correctamente."
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

            ctx.render("vehiculos/registrar.jte", Map.of(
                    "title", "Registrar vehículo",
                    "usuarioNombre", usuarioNombre,
                    "patente", patente == null ? "" : patente,
                    "error", e.getMessage(),
                    "mensaje", ""
            ));
        }
    }
}
