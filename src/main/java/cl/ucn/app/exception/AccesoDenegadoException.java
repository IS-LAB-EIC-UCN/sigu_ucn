package cl.ucn.app.exception;

/** Se lanza cuando el usuario en sesión no tiene permiso para la operación. */
public class AccesoDenegadoException extends RuntimeException {
    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
