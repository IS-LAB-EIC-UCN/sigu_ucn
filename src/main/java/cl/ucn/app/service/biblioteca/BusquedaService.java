package cl.ucn.app.service.biblioteca;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cl.ucn.app.model.biblioteca.Ejemplar;
import cl.ucn.app.model.biblioteca.Libro;
import cl.ucn.app.repository.biblioteca.EjemplarRepository;
import cl.ucn.app.repository.biblioteca.LibroRepository;
import cl.ucn.app.repository.biblioteca.api.IEjemplarRepository;
import cl.ucn.app.repository.biblioteca.api.ILibroRepository;
import cl.ucn.app.service.biblioteca.api.IBusquedaService;
public class BusquedaService implements IBusquedaService {

    private final ILibroRepository libroRepository;
    private final IEjemplarRepository ejemplarRepository;
    public BusquedaService(){
        this.libroRepository = new LibroRepository();
        this.ejemplarRepository = new EjemplarRepository();
    }

    public BusquedaService(ILibroRepository libroRepository, IEjemplarRepository ejemplarRepository) {
        this.libroRepository = libroRepository;
        this.ejemplarRepository = ejemplarRepository;
    }
    public List<Libro> buscar(String termino){
        if (termino == null){return new ArrayList<>();}

        Set<Libro> listaLibros = new HashSet<Libro>();

        List<Libro> titulo = libroRepository.findByTitulo(termino);
        if (!titulo.isEmpty()){ listaLibros.addAll(titulo);}

        List<Libro> autor = libroRepository.findByAutor(termino);
        if (!autor.isEmpty()){ listaLibros.addAll(autor);}

        List<Libro> categoria = libroRepository.findByCategoria(termino);
        if (!categoria.isEmpty()){ listaLibros.addAll(categoria);}

        return new ArrayList<>(listaLibros);
    }

    public List<Ejemplar> buscarEjemplaresDisponibles(String termino) {
        if (termino == null) {
            return new ArrayList<>();
        }

        List<Ejemplar> ejemplaresDisponibles = new ArrayList<>();

        for (Libro libro : buscar(termino)) {
            ejemplaresDisponibles.addAll(ejemplarRepository.findDisponiblesByLibro(libro));
        }

        return ejemplaresDisponibles;

    }
}
