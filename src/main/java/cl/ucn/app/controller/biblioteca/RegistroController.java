package cl.ucn.app.controller.biblioteca;

import cl.ucn.app.exceptions.RecursoNoEncontradoException;
import cl.ucn.app.exceptions.ValidacionException;
import cl.ucn.app.model.biblioteca.EstadoEjemplar;
import cl.ucn.app.model.biblioteca.Ejemplar;
import cl.ucn.app.model.biblioteca.Libro;
import cl.ucn.app.repository.biblioteca.EjemplarRepository;
import cl.ucn.app.repository.biblioteca.LibroRepository;
import cl.ucn.app.service.biblioteca.EjemplarService;
import cl.ucn.app.service.biblioteca.LibroService;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cl.ucn.app.service.biblioteca.api.ILibroService;
import cl.ucn.app.service.biblioteca.api.IEjemplarService;
import cl.ucn.app.repository.biblioteca.api.ILibroRepository;
import cl.ucn.app.repository.biblioteca.api.IEjemplarRepository;

public class RegistroController {

    private final ILibroService libroService;
    private final IEjemplarService ejemplarService;
    private final ILibroRepository libroRepository;
    private final IEjemplarRepository ejemplarRepository;

    public RegistroController() {
        this(new LibroService(),
             new EjemplarService(),
             new LibroRepository(),
             new EjemplarRepository());
    }

    public RegistroController(ILibroService libroService,
                              IEjemplarService ejemplarService,
                              ILibroRepository libroRepository,
                              IEjemplarRepository ejemplarRepository) {
        this.libroService = libroService;
        this.ejemplarService = ejemplarService;
        this.libroRepository = libroRepository;
        this.ejemplarRepository = ejemplarRepository;
    }

    public void formularioEditarLibro(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        Long id = Long.parseLong(ctx.queryParam("id"));
        Libro libro = libroRepository.findById(id);
        if (libro == null) {
            throw new RecursoNoEncontradoException("No existe el libro con ID " + id);
        }
        List<Ejemplar> ejemplares = ejemplarRepository.findByLibro(libro);

        Map<String, Object> model = new HashMap<>();
        model.put("usuarioNombre", usuarioNombre);
        model.put("libro", libro);
        model.put("ejemplares", ejemplares);
        model.put("error", null);
        model.put("titulo", libro.getTitulo());
        model.put("autor", libro.getAutor());
        model.put("categoria", libro.getCategoria());
        model.put("isbn", libro.getIsbn());
        ctx.render("biblioteca/editar-libro.jte", model);
    }

    public void editarLibro(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        Long id = Long.parseLong(ctx.formParam("id"));
        String titulo = ctx.formParam("titulo");
        String autor = ctx.formParam("autor");
        String categoria = ctx.formParam("categoria");
        String isbn = ctx.formParam("isbn");

        try {
            libroService.actualizarLibro(id, titulo.trim(), autor.trim(), categoria.trim(), isbn.trim());
            ctx.redirect("/biblioteca/libros");
        } catch (ValidacionException e) {
            Libro libro = libroRepository.findById(id);
            List<Ejemplar> ejemplares = libro != null ? ejemplarRepository.findByLibro(libro) : List.of();
            Map<String, Object> model = new HashMap<>();
            model.put("usuarioNombre", usuarioNombre);
            model.put("libro", libro);
            model.put("ejemplares", ejemplares);
            model.put("error", e.getMessage());
            model.put("titulo", titulo);
            model.put("autor", autor);
            model.put("categoria", categoria);
            model.put("isbn", isbn);
            ctx.render("biblioteca/editar-libro.jte", model);
        }
    }

    public void eliminarLibro(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        Long id = Long.parseLong(ctx.queryParam("id"));
        libroService.eliminarLibro(id);
        ctx.redirect("/biblioteca/libros");
    }

    public void formularioEditarEjemplar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        Long id = Long.parseLong(ctx.queryParam("id"));
        Ejemplar ejemplar = ejemplarRepository.findById(id);
        if (ejemplar == null) {
            throw new RecursoNoEncontradoException("No existe el ejemplar con ID " + id);
        }

        Map<String, Object> model = new HashMap<>();
        model.put("usuarioNombre", usuarioNombre);
        model.put("ejemplar", ejemplar);
        model.put("error", null);
        model.put("estado", ejemplar.getEstado() != null ? ejemplar.getEstado().name() : "");
        ctx.render("biblioteca/editar-ejemplar.jte", model);
    }

    public void editarEjemplar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        Long id = Long.parseLong(ctx.formParam("id"));
        String estado = ctx.formParam("estado");

        try {
            EstadoEjemplar estadoEnum = null;
            if (estado != null) {
                try {
                    estadoEnum = EstadoEjemplar.valueOf(estado.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new ValidacionException("Estado invalido. Use DISPONIBLE o PRESTADO");
                }
            }
            Ejemplar ejemplar = ejemplarRepository.findById(id);
            Long libroId = (ejemplar != null && ejemplar.getLibro() != null) ? ejemplar.getLibro().getId() : null;
            ejemplarService.actualizarEjemplar(id, estadoEnum);
            if (libroId != null) {
                ctx.redirect("/biblioteca/libros/editar?id=" + libroId);
            } else {
                ctx.redirect("/biblioteca/libros");
            }
        } catch (ValidacionException e) {
            Ejemplar ejemplar = ejemplarRepository.findById(id);
            Map<String, Object> model = new HashMap<>();
            model.put("usuarioNombre", usuarioNombre);
            model.put("ejemplar", ejemplar);
            model.put("error", e.getMessage());
            model.put("estado", estado);
            ctx.render("biblioteca/editar-ejemplar.jte", model);
        }
    }

    public void eliminarEjemplar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        Long id = Long.parseLong(ctx.queryParam("id"));
        Ejemplar ejemplar = ejemplarRepository.findById(id);
        Long libroId = (ejemplar != null && ejemplar.getLibro() != null) ? ejemplar.getLibro().getId() : null;
        ejemplarService.eliminarEjemplar(id);
        if (libroId != null) {
            ctx.redirect("/biblioteca/libros/editar?id=" + libroId);
        } else {
            ctx.redirect("/biblioteca/libros");
        }
    }
}
