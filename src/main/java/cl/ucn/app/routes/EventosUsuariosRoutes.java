package cl.ucn.app.routes;
import cl.ucn.app.controller.EventosUsuariosController;
import io.javalin.config.JavalinConfig;
public final class EventosUsuariosRoutes {
    private EventosUsuariosRoutes() {}
    public static void register(JavalinConfig config) {
        EventosUsuariosController c=new EventosUsuariosController();
        config.routes.get("/eventos/usuarios",c::listar);config.routes.get("/eventos/roles",c::roles);
        config.routes.get("/eventos/usuarios/nuevo",c::nuevo);config.routes.post("/eventos/usuarios/crear",c::crear);
        config.routes.get("/eventos/usuarios/{id}/editar",c::editar);config.routes.post("/eventos/usuarios/{id}/editar",c::actualizar);
    }
}
