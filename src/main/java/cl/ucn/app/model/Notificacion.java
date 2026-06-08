package cl.ucn.app.model;

public class Notificacion {
    private String mensaje;

    public Notificacion(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    @Override
    public String toString() {
        return "Notificacion: " + mensaje;
    }
}