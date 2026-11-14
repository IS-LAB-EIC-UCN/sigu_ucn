package cl.ucn.app.controller.talleres;

import io.javalin.http.Context;

import java.util.Optional;

/** Datos del usuario en sesión (los guarda AuthController al iniciar sesión). */
public record SesionUsuario(Long id, String nombre, String rol) {

    public static Optional<SesionUsuario> desde(Context ctx) {
        Long id = ctx.sessionAttribute("usuarioId");
        if (id == null) {
            return Optional.empty();
        }
        return Optional.of(new SesionUsuario(id, ctx.sessionAttribute("usuarioNombre"), ctx.sessionAttribute("usuarioRol")));
    }

    public boolean esAdmin() {
        return "ADMIN".equals(rol);
    }
}
