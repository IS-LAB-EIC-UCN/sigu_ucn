package cl.ucn.app.exceptions;

import io.javalin.http.HttpStatus;

public class ConflictoEstadoException extends BusinessException {

    public ConflictoEstadoException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
