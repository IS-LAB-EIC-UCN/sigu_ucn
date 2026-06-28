package cl.ucn.app.service.biblioteca.api;

import cl.ucn.app.model.biblioteca.Libro;
import cl.ucn.app.model.biblioteca.Ejemplar;

import java.util.List;

public interface IBusquedaService {
    List<Libro> buscar(String termino);
    List<Ejemplar> buscarEjemplaresDisponibles(String termino);
}
