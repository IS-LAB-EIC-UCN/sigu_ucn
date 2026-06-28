package cl.ucn.app.auth;

import cl.ucn.app.exceptions.AccesoDenegadoException;
import io.javalin.http.Context;

import java.util.Set;

public class BibliotecaAuthFilter {

    private static final String SESSION_USER = "bibliotecaCurrentUser";

    private BibliotecaAuthFilter() {
    }

    public static boolean requireRol(Context ctx, Set<String> permitidos) {
        if (ctx.sessionAttribute("usuarioNombre") == null) {
            ctx.redirect("/login");
            return false;
        }
        String currentUser = ctx.sessionAttribute("usuarioNombre");
        String cachedUser = ctx.sessionAttribute(SESSION_USER);
        if (cachedUser == null || !cachedUser.equals(currentUser)) {
            LectorSessionHelper.limpiarSesion(ctx);
            ctx.sessionAttribute(SESSION_USER, currentUser);
        }
        Object rol = ctx.sessionAttribute("usuarioRol");
        if (rol == null) {
            throw new AccesoDenegadoException("Debe iniciar sesion");
        }
        if (!permitidos.contains(rol.toString())) {
            throw new AccesoDenegadoException("No tiene permisos para esta accion");
        }
        return true;
    }
}
