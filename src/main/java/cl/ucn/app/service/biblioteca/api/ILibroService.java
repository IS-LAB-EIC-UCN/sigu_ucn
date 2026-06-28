package cl.ucn.app.service.biblioteca.api;

import cl.ucn.app.model.biblioteca.Libro;

import java.util.List;

public interface ILibroService {
    Libro registrarLibro(String titulo, String autor, String categoria, String isbn);
    Libro actualizarLibro(Long id, String titulo, String autor, String categoria, String isbn);
    void eliminarLibro(Long id);
    List<Libro> buscarPorTitulo(String titulo);
    List<Libro> buscarPorAutor(String autor);
    List<Libro> listarTodos();
}
