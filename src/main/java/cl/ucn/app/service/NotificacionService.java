package cl.ucn.app.service;

public class NotificacionService {
    private String mensaje;

    NotificacionService(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String mostrar() {
        return "Notificacion: " + mensaje;
    }

    //Por si se necesita
    @Override
    public String toString() {
        return "Notificacion: " + mensaje;
    }
    
    
}
