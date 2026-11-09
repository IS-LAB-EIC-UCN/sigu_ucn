
package cl.ucn.app.routes;

import cl.ucn.app.controller.EventosController;
import io.javalin.config.JavalinConfig;

public final class EventosRoutes {

    private EventosRoutes() {
    }

    public static void register(JavalinConfig config) {

        EventosController controller = new EventosController();

        // Mostrar listado de eventos
        config.routes.get(
                "/eventos",
                controller::mostrarEventos);

        // Mostrar formulario de creación
        config.routes.get(
                "/eventos/nuevo",
                controller::mostrarFormulario);

        // Guardar nuevo evento
        config.routes.post(
                "/eventos/crear",
                controller::crearEvento);
    }
}
