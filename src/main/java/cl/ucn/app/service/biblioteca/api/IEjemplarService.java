package cl.ucn.app.service.biblioteca.api;

import cl.ucn.app.model.biblioteca.Ejemplar;

import java.util.List;

public interface IEjemplarService {
    Ejemplar agregarEjemplar(Long libroId);
    int agregarEjemplares(Long libroId, int cantidad);
    Ejemplar actualizarEjemplar(Long id, String estado);
    void eliminarEjemplar(Long id);
    List<Ejemplar> listarPorLibro(Long libroId);
}
