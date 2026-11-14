package cl.ucn.app.exception;

/** Se lanza cuando se busca una entidad por id y no existe. */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
