package cl.ucn.app.service;

public class ReglaNegocioException extends RuntimeException {
    private final String codigoRegla;

    public ReglaNegocioException(String codigoRegla, String mensaje) {
        super(mensaje);
        this.codigoRegla = codigoRegla;
    }

    public String getCodigoRegla() {
        return codigoRegla;
    }
}
