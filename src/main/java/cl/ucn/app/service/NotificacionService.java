package cl.ucn.app.service;

import cl.ucn.app.model.Notificacion;

public class NotificacionService {
    private final Notificacion notificacion;

    public NotificacionService(String mensaje) {
        this.notificacion = new Notificacion(mensaje);
    }

    //(...)
    public void mostrar() {
        System.out.println(notificacion);
    }
    
}
