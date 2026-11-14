package cl.ucn.app.util;

import java.util.Set;

/**
 * Mapeo de los actores del módulo de inventario (Grupo 5) a los roles del sistema.
 * <ul>
 *     <li>Administrador         -> ADMIN</li>
 *     <li>Encargado laboratorio -> ENCARGADO</li>
 *     <li>Usuario solicitante   -> DOCENTE, ESTUDIANTE</li>
 * </ul>
 */
public final class RolesInventario {

    public static final String ADMIN = "ADMIN";
    public static final String ENCARGADO = "ENCARGADO";
    public static final String DOCENTE = "DOCENTE";
    public static final String ESTUDIANTE = "ESTUDIANTE";

    /** Cualquier usuario autenticado. */
    public static final Set<String> TODOS = Set.of();
    public static final Set<String> SOLO_ADMIN = Set.of(ADMIN);
    /** Administrador y encargado: gestionan stock, devoluciones y movimientos. */
    public static final Set<String> GESTION = Set.of(ADMIN, ENCARGADO);
    /** Usuarios que pueden solicitar préstamos de equipos. */
    public static final Set<String> SOLICITANTES = Set.of(DOCENTE, ESTUDIANTE);

    private RolesInventario() {
    }

    public static boolean esGestion(String rol) {
        return rol != null && GESTION.contains(rol);
    }

    public static boolean esAdmin(String rol) {
        return ADMIN.equals(rol);
    }

    public static boolean esSolicitante(String rol) {
        return rol != null && SOLICITANTES.contains(rol);
    }
}
