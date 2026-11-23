package cl.ucn.app.routes;
import cl.ucn.app.controller.VehiculoController;
import io.javalin.config.JavalinConfig;
public final class VehiculoRoutes {

    private VehiculoRoutes() {
    }

    public static void register(JavalinConfig config) {

        VehiculoController controller = new VehiculoController();

        config.routes.get(
                "/estacionamientos/vehiculos/nuevo",
                controller::mostrarFormulario
        );

        config.routes.post(
                "/estacionamientos/vehiculos",
                controller::registrarVehiculo
        );
    }
}