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

        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("El ISBN es obligatorio");
        }

        String isbnLimpio = isbn.replaceAll("[\\s-]", "");
        if (isbnLimpio.length() != 10 && isbnLimpio.length() != 13) {
            throw new IllegalArgumentException("El ISBN debe tener 10 o 13 digitos (sin contar guiones)");
        }
        if (isbnLimpio.length() == 10 && !isbnLimpio.matches("^\\d{9}[\\dX]$")) {
            throw new IllegalArgumentException("El ISBN-10 no tiene un formato valido");
        }
        if (isbnLimpio.length() == 13 && !isbnLimpio.matches("^\\d{13}$")) {
            throw new IllegalArgumentException("El ISBN-13 no tiene un formato valido");
        }

        if (titulo.length() > 200 || autor.length() > 100 || categoria.length() > 100 || isbn.length() > 20) {
            throw new IllegalArgumentException("Algun campo supera el largo maximo permitido");
        }

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