package cl.ucn.app.controller.biblioteca;

import cl.ucn.app.exceptions.RecursoNoEncontradoException;
import cl.ucn.app.exceptions.ValidacionException;
import cl.ucn.app.model.biblioteca.Libro;
import cl.ucn.app.model.biblioteca.Ejemplar;
import cl.ucn.app.repository.biblioteca.EjemplarRepository;
import cl.ucn.app.repository.biblioteca.LibroRepository;
import cl.ucn.app.service.biblioteca.EjemplarService;
import cl.ucn.app.service.biblioteca.LibroService;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistroController {

    private final LibroService libroService = new LibroService();
    private final EjemplarService ejemplarService = new EjemplarService();
    private final LibroRepository libroRepository = new LibroRepository();
    private final EjemplarRepository ejemplarRepository = new EjemplarRepository();

    public void formularioEditarLibro(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) { ctx.redirect("/login"); return; }

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
        if (usuarioNombre == null) { ctx.redirect("/login"); return; }

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
        if (usuarioNombre == null) { ctx.redirect("/login"); return; }

        Long id = Long.parseLong(ctx.queryParam("id"));
        libroService.eliminarLibro(id);
        ctx.redirect("/biblioteca/libros");
    }

    public void formularioEditarEjemplar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) { ctx.redirect("/login"); return; }

        Long id = Long.parseLong(ctx.queryParam("id"));
        Ejemplar ejemplar = ejemplarRepository.findById(id);
        if (ejemplar == null) {
            throw new RecursoNoEncontradoException("No existe el ejemplar con ID " + id);
        }

        Map<String, Object> model = new HashMap<>();
        model.put("usuarioNombre", usuarioNombre);
        model.put("ejemplar", ejemplar);
        model.put("error", null);
        model.put("estado", ejemplar.getEstado());
        ctx.render("biblioteca/editar-ejemplar.jte", model);
    }

    public void editarEjemplar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) { ctx.redirect("/login"); return; }

        Long id = Long.parseLong(ctx.formParam("id"));
        String estado = ctx.formParam("estado");

        try {
            ejemplarService.actualizarEjemplar(id, estado);
            ctx.redirect("/biblioteca/libros");
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
        if (usuarioNombre == null) { ctx.redirect("/login"); return; }

        Long id = Long.parseLong(ctx.queryParam("id"));
        ejemplarService.eliminarEjemplar(id);
        ctx.redirect("/biblioteca/libros");
    }
}
