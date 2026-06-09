package cl.ucn.app.routes;

import cl.ucn.app.controller.EstacionamientoController;
import io.javalin.config.JavalinConfig;

public final class EstacionamientoRoutes {

    private EstacionamientoRoutes() {
    }

    public static void register(JavalinConfig config) {

        EstacionamientoController controller =
                new EstacionamientoController();

        config.routes.get(
                "/estacionamientos",
                controller::showEstacionamientos
        );

        config.routes.get(
                "/estacionamientos/nuevo",
                controller::showNuevo
        );

        config.routes.post(
                "/estacionamientos",
                controller::guardar
        );

        config.routes.get(
                "/estacionamientos/eliminar/{id}",
                controller::eliminar
        );

        config.routes.get(
                "/estacionamientos/editar/{id}",
                controller::showEditar
        );

        config.routes.post(
                "/estacionamientos/editar/{id}",
                controller::actualizar
        );

        config.routes.get(
                "/reservas/estacionamientos",
                controller::showReservar
        );

        config.routes.post(
                "/reservas/estacionamientos",
                controller::reservar
        );

        config.routes.get(
                "/mis-reservas/estacionamientos",
                controller::misReservas
        );
    }
}