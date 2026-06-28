package cl.ucn.app.repository.biblioteca.api;

import cl.ucn.app.model.biblioteca.Ejemplar;
import cl.ucn.app.model.biblioteca.Libro;

import java.util.List;

public interface IEjemplarRepository {
    void save(Ejemplar ejemplar);
    void save(Ejemplar ejemplar, jakarta.persistence.EntityManager em);
    void delete(Ejemplar ejemplar);
    Ejemplar findById(Long id);
    List<Ejemplar> findByLibro(Libro libro);
    List<Ejemplar> findDisponiblesByLibro(Libro libro);
    List<Ejemplar> findAll();
}
