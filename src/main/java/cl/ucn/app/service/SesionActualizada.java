package cl.ucn.app.service;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.UsuarioRepository;
import io.javalin.http.Context;
/** Consulta el estado y rol actuales antes de ejecutar rutas dinámicas. */
public class SesionActualizada {
    private final UsuarioRepository repo;
    public SesionActualizada(UsuarioRepository repo) {this.repo=repo;}
    public void verificar(Context ctx) {
        Long id=ctx.sessionAttribute("usuarioId");if(id==null) return;
        Usuario usuario=repo.findById(id);
        if(usuario==null || !Boolean.TRUE.equals(usuario.getActivo())) {
            var sesion=ctx.req().getSession(false);if(sesion!=null) sesion.invalidate();
            ctx.redirect("/login");ctx.skipRemainingHandlers();return;
        }
        ctx.sessionAttribute("usuarioNombre",usuario.getNombre());ctx.sessionAttribute("usuarioCorreo",usuario.getCorreo());ctx.sessionAttribute("usuarioRol",usuario.getRol().getNombre());
    }
}
