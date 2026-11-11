
package cl.ucn.app.routes;

import cl.ucn.app.controller.EventosController;
import io.javalin.config.JavalinConfig;

public final class EventosRoutes {

        private EventosRoutes() {
        }

        public static void register(JavalinConfig config) {

                EventosController controller = new EventosController();

                config.routes.get("/eventos/catalogos", controller::catalogos);
                config.routes.post("/eventos/expositores", controller::guardarExpositor);
                config.routes.post("/eventos/ubicaciones", controller::guardarUbicacion);
                config.routes.post("/eventos/ubicaciones/{id}/disponibilidad", controller::cambiarUbicacion);
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

                // Mostrar formulario para editar un evento
                config.routes.get(
                                "/eventos/{id}/editar",
                                controller::mostrarFormularioEditar);

                // Guardar los cambios de un evento
                config.routes.post(
                                "/eventos/{id}/editar",
                                controller::actualizarEvento);

                config.routes.post("/eventos/{id}/reactivar", controller::reactivarEvento);

                // Cancelar un evento académico
                config.routes.post(
                                "/eventos/{id}/cancelar",
                                controller::cancelarEvento);

                config.routes.post(
                                "/eventos/{id}/publicar",
                                controller::publicarEvento);

        }
}
