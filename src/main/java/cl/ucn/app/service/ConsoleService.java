package cl.ucn.app.service;

import java.util.List;

import cl.ucn.app.service.Interfaces.IConsole;

/**
 * Debiera enviar a la capa de Navegador (...)
 */
public class ConsoleService implements IConsole {
    @Override
    public void log(Object salida) {
        System.out.println(salida);
    }

    @Override
    public void log(String salida) {
        System.out.println(salida);
    }

    @Override
    public void log(List<Object> salida) {
        for (Object o: salida) {
            System.out.println(o);
        }
    }
    
}
