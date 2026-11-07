package cl.ucn.app.service;

public class AccesoDenegadoException extends RuntimeException {
    private final int statusCode;

    public AccesoDenegadoException(int statusCode, String mensaje) {
        super(mensaje);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
