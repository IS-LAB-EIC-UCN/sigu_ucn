package cl.ucn.app.exception;

/** Se lanza cuando una operación viola una regla de negocio. El mensaje se muestra al usuario. */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
