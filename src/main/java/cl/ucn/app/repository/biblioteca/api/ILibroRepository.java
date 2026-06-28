package cl.ucn.app.repository.biblioteca.api;

import cl.ucn.app.model.biblioteca.Libro;

import java.util.List;

public interface ILibroRepository {
    void save(Libro libro);
    void save(Libro libro, jakarta.persistence.EntityManager em);
    void delete(Libro libro);
    Libro findById(Long id);
    List<Libro> findByTitulo(String titulo);
    List<Libro> findByAutor(String autor);
    List<Libro> findByCategoria(String categoria);
    Libro findByIsbn(String isbn);
    List<Libro> findAll();
}
