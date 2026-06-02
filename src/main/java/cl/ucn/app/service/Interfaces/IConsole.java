package cl.ucn.app.service.Interfaces;

import java.util.List;

public interface IConsole {
    public void log(Object salida);
    public void log(String salida);
    public void log(List<Object> salida);
}
