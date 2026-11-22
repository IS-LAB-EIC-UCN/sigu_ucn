package cl.ucn.app.model.soporte;

import cl.ucn.app.model.Usuario;

public final class RolesSoporte {
    public static final String ROL_ADMIN = "ADMIN";
    public static final String ROL_TECNICO = "TECNICO";

    private RolesSoporte() {}

    public static boolean esAdministrador(Usuario usuario) {
        return tieneRol(usuario, ROL_ADMIN);
    }

    public static boolean esTecnico(Usuario usuario) {
        return tieneRol(usuario, ROL_TECNICO);
    }

    private static boolean tieneRol(Usuario usuario, String nombreRol) {
        return usuario != null
                && Boolean.TRUE.equals(usuario.getActivo())
                && usuario.getRol() != null
                && nombreRol.equals(usuario.getRol().getNombre());
    }
}
