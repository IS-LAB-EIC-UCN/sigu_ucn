package cl.ucn.app.service.biblioteca;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cl.ucn.app.model.biblioteca.Ejemplar;
import cl.ucn.app.model.biblioteca.Libro;
import cl.ucn.app.repository.biblioteca.LibroRepository;
import cl.ucn.app.service.biblioteca.EjemplarService;
public class BusquedaService {

    private final LibroRepository libroRepository;
    private final EjemplarService ejemplarService;
    public BusquedaService(){
        this.libroRepository = new LibroRepository();
        this.ejemplarService = new EjemplarService();
    }

    public List<Libro> buscar(String termino){
        if (termino == null){return new ArrayList<>();}

        Set<Libro> listaLibros = new HashSet<Libro>();

        List<Libro> titulo = libroRepository.findByTitulo(termino);
        if (titulo.isEmpty()){ listaLibros.addAll(titulo);}

        List<Libro> autor = libroRepository.findByAutor(termino);
        if (autor.isEmpty()){ listaLibros.addAll(autor);}

        List<Libro> categoria = libroRepository.findByCategoria(termino);
        if (categoria.isEmpty()){ listaLibros.addAll(categoria);}

        return new ArrayList<>(listaLibros);
    }

    //busqueda de disponibles
    public List<Ejemplar> buscarEjemplaresDisponibles(String termino) {
        if (termino == null) {
            return new ArrayList<>();
        }

        Set<Libro> listaLibros = new HashSet<Libro>();

        List<Libro> titulo = libroRepository.findByTitulo(termino);
        if (titulo.isEmpty()) {
            listaLibros.addAll(titulo);
        }

        List<Libro> autor = libroRepository.findByAutor(termino);
        if (autor.isEmpty()) {
            listaLibros.addAll(autor);
        }

        List<Libro> categoria = libroRepository.findByCategoria(termino);
        if (categoria.isEmpty()) {
            listaLibros.addAll(categoria);
        }

        List<Ejemplar> ejemplaresDisponibles = new ArrayList<>();

        for (Libro libro : listaLibros) {
            List<Ejemplar> totales = ejemplarService.listarPorLibro(libro.getId());

            if (totales != null) {
                for (Ejemplar ejemplar : totales) {
                    if ("DISPONIBLE".equalsIgnoreCase(ejemplar.getEstado())) {
                        ejemplaresDisponibles.add(ejemplar);
                    }
                }
            }
        }

        return ejemplaresDisponibles;

    }
}
