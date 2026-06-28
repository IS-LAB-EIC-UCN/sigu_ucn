package cl.ucn.app.exceptions;

import io.javalin.http.HttpStatus;

public class ValidacionException extends BusinessException {

    public ValidacionException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
