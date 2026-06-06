package cl.ucn.app.service.biblioteca;

import cl.ucn.app.repository.biblioteca.LibroRepository;
import cl.ucn.app.model.biblioteca.Libro;
import java.util.List;

public class LibroService {

    private final LibroRepository libroRepository;

    public LibroService() {
        this.libroRepository = new LibroRepository();
    }

    public Libro registrarLibro(String titulo, String autor, String categoria, String isbn) {

        if (libroRepository.findByIsbn(isbn) != null) {
            throw new IllegalArgumentException("Ya existe un libro registrado con el ISBN: " + isbn);
        }

        Libro nuevoLibro = new Libro();
        nuevoLibro.setTitulo(titulo);
        nuevoLibro.setAutor(autor);
        nuevoLibro.setCategoria(categoria);
        nuevoLibro.setIsbn(isbn);

        libroRepository.save(nuevoLibro);
        return nuevoLibro;
    }

    public List<Libro> buscarPorTitulo(String titulo){
        return libroRepository.findByTitulo(titulo);
    }

    public List<Libro> buscarPorAutor(String autor){
        return libroRepository.findByAutor(autor);
    }

    public List<Libro> listarTodos(){
        return libroRepository.findAll();
    }
}