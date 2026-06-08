package cl.ucn.app.service;

import cl.ucn.app.model.Notificacion;

public class NotificacionService {
    private Notificacion notificacion;

    public NotificacionService() {}

    public NotificacionService(String mensaje) {
        this.notificacion = new Notificacion(mensaje);
    }

    public Notificacion getNotificacion() {
        return notificacion;
    }

    public void setNotificacion(Notificacion notificacion) {
        this.notificacion = notificacion;
    }

    //pasar a controlador
    public void mostrar() {
        System.out.println(notificacion);
    }
    
}
