package cl.ucn.app.service;

import cl.ucn.app.model.Recurso;
import cl.ucn.app.service.Interfaces.IRecursoFactory;

public class RecursoFactoryService implements IRecursoFactory {

    @Override
    public Recurso crearInsumo(String nombre, int stock) {
        return new Recurso(nombre, stock, "INSUMO");
    }

    @Override
    public Recurso crearEquipo(String nombre, int stock) {
        return new Recurso(nombre, stock, "EQUIPO");
    }
    
}
