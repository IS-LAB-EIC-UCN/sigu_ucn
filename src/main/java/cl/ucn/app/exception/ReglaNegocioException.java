package cl.ucn.app.exception;

/**
 * Error esperado al violar una regla de negocio o una validación de entrada.
 * Su mensaje es seguro para mostrarlo directamente al usuario.
 */

public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}