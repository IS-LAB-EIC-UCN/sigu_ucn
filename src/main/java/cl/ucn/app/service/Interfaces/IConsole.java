package cl.ucn.app.service.Interfaces;

import java.util.List;

import cl.ucn.app.model.Salida;

public interface IConsole {
    public void log(Object salida);
    public void log(String salida);
    public void log(List<Object> salida);
    public void log_inventory(String categoria);
    public void log_alerta(Salida salida);
}
