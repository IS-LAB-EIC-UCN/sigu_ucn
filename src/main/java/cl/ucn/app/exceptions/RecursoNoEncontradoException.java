package cl.ucn.app.exceptions;

import io.javalin.http.HttpStatus;

public class RecursoNoEncontradoException extends BusinessException {

    public RecursoNoEncontradoException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
