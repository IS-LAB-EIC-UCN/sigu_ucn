package cl.ucn.app.exception;

/**
 * El producto, equipo, insumo o préstamo solicitado no existe.
 */

public class RecursoNoEncontradoException extends ReglaNegocioException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}