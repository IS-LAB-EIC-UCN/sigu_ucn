package cl.ucn.app.repository;

import cl.ucn.app.model.Libro;
import java.util.List;
import java.util.Optional;

public interface LibroRepository {
    Optional<Libro> findById(Long id);
    Optional<Libro> findByIsbn(String isbn);
    List<Libro> findAll();
    List<Libro> buscarPorCriterio(String termino);
    Libro save(Libro libro);
    void update(Libro libro);
}
