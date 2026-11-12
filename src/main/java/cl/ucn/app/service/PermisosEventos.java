package cl.ucn.app.service;
import java.util.Objects;
/** Permisos del módulo: estudiante participante; docente organizador; administrador supervisor. */
public final class PermisosEventos {
    private PermisosEventos() {}
    public static boolean organiza(String rol) { return "ADMIN".equals(rol) || "DOCENTE".equals(rol); }
    public static boolean administra(String rol, Long usuario, Long organizador) {
        return usuario != null && ("ADMIN".equals(rol) || ("DOCENTE".equals(rol) && Objects.equals(usuario, organizador)));
    }
}
