package cl.ucn.app.service;
import java.util.Objects;
/** Permisos del módulo: estudiante participante; docente organizador; administrador supervisor. */
public final class PermisosEventos {
    private PermisosEventos() {}
    public static boolean organiza(String rol) { return "ADMIN".equals(rol) || "DOCENTE".equals(rol); }
    public static boolean registraAsistencia(String rol, Long usuario, Long organizador) {
        return organiza(rol) && usuario != null && Objects.equals(usuario, organizador);
    }
    public static boolean consultaInscritos(String rol, Long usuario, Long organizador, String estado) {
        return administra(rol,usuario,organizador) || (usuario != null && "ADMIN".equals(rol) && ("PUBLICADO".equals(estado) || "FINALIZADO".equals(estado)));
    }
    public static boolean administra(String rol, Long usuario, Long organizador) {
        return organiza(rol) && usuario != null && Objects.equals(usuario, organizador);
    }
}
