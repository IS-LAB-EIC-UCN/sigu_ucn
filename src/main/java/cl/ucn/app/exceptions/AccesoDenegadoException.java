package cl.ucn.app.exceptions;

import io.javalin.http.HttpStatus;

public class AccesoDenegadoException extends BusinessException {

    public AccesoDenegadoException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
