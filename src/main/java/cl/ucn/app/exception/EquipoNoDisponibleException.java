package cl.ucn.app.exception;
/**
 * Regla de negocio: un equipo prestado no puede prestarse nuevamente.
 */

public class EquipoNoDisponibleException extends ReglaNegocioException {

    public EquipoNoDisponibleException(String codigo) {
        super("El equipo " + codigo + " no está disponible para préstamos.");
    }
}