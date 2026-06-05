package cl.ucn.app.service.biblioteca;

import cl.ucn.app.repository.biblioteca.EjemplarRepository;
import cl.ucn.app.repository.biblioteca.LibroRepository;
import cl.ucn.app.model.biblioteca.Ejemplar;
import cl.ucn.app.model.biblioteca.Libro;

import java.util.ArrayList;
import java.util.List;

public class EjemplarService {

    private final EjemplarRepository ejemplarRepository;
    private final LibroRepository libroRepository; // Necesario para validar el libro

    public EjemplarService() {
        this.ejemplarRepository = new EjemplarRepository();
        this.libroRepository = new LibroRepository();
    }

    public Ejemplar agregarEjemplar(Long libroId) {
        Libro libro = libroRepository.findById(libroId);

        if (libro == null) {
            throw new IllegalArgumentException("No se puede agregar un ejemplar: El libro con ID " + libroId + " no existe.");
        }

        Ejemplar nuevoEjemplar = new Ejemplar();
        nuevoEjemplar.setLibro(libro);
        nuevoEjemplar.setEstado("DISPONIBLE");

        ejemplarRepository.save(nuevoEjemplar);

        return nuevoEjemplar;
    }

    public List<Ejemplar> listarPorLibro(Long libroId) {
        List<Ejemplar> todosLosEjemplares = ejemplarRepository.findAll();
        Libro libro = libroRepository.findById(libroId);
        List<Ejemplar> ejemplaresDelLibro = new ArrayList<>();

        if (todosLosEjemplares == null || libroId == null) {
            return ejemplaresDelLibro;
        }

        for (Ejemplar ejemplar : todosLosEjemplares) {

            if (ejemplar.getLibro() != null && libro.getId().equals(ejemplar.getLibro().getId())) {
                ejemplaresDelLibro.add(ejemplar);
            }
        }
        return ejemplaresDelLibro;

    }
}