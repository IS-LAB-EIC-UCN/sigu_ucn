package cl.ucn.app.service.Interfaces;

import cl.ucn.app.model.Recurso;

public interface IRecursoFactory {
    public Recurso crearInsumo(String nombre, int stock);
    public Recurso crearEquipo(String nombre, int stock);
}
