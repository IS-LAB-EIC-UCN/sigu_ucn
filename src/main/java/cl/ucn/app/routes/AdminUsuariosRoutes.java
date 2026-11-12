package cl.ucn.app.routes;
import cl.ucn.app.controller.AdminUsuariosController;
import io.javalin.config.JavalinConfig;
public final class AdminUsuariosRoutes {
    private AdminUsuariosRoutes() {}
    public static void register(JavalinConfig config) {
        AdminUsuariosController c=new AdminUsuariosController();
        config.routes.get("/admin/usuarios",c::listar);config.routes.get("/admin/roles",c::roles);
        config.routes.get("/admin/usuarios/nuevo",c::nuevo);config.routes.post("/admin/usuarios/crear",c::crear);
        config.routes.get("/admin/usuarios/{id}/editar",c::editar);config.routes.post("/admin/usuarios/{id}/editar",c::actualizar);
    }
}
