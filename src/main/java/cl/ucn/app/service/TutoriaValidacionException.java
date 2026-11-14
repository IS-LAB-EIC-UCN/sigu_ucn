package cl.ucn.app.service;

public class TutoriaValidacionException extends RuntimeException {

    private final String campo;

    public TutoriaValidacionException(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}
