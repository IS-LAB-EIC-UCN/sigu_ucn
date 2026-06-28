package cl.ucn.app.controller.biblioteca;

import cl.ucn.app.exceptions.ValidacionException;
import cl.ucn.app.model.biblioteca.Ejemplar;
import cl.ucn.app.model.biblioteca.Libro;
import cl.ucn.app.repository.biblioteca.EjemplarRepository;
import cl.ucn.app.repository.biblioteca.LibroRepository;
import cl.ucn.app.repository.biblioteca.api.IEjemplarRepository;
import cl.ucn.app.repository.biblioteca.api.ILibroRepository;
import cl.ucn.app.service.biblioteca.BusquedaService;
import cl.ucn.app.service.biblioteca.EjemplarService;
import cl.ucn.app.service.biblioteca.LibroService;
import cl.ucn.app.service.biblioteca.api.IEjemplarService;
import cl.ucn.app.service.biblioteca.api.ILibroService;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatalogoController {

    private final ILibroService libroService = new LibroService();
    private final IEjemplarService ejemplarService = new EjemplarService();
    private final ILibroRepository libroRepository = new LibroRepository();
    private final IEjemplarRepository ejemplarRepository = new EjemplarRepository();
    private final cl.ucn.app.service.biblioteca.api.IBusquedaService busquedaService = new BusquedaService();

    public void listar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        List<Libro> libros = libroService.listarTodos();
        Map<Long, Integer> totalEjemplares = new HashMap<>();
        Map<Long, Integer> disponibles = new HashMap<>();
        for (Libro libro : libros) {
            List<Ejemplar> ejemplares = ejemplarRepository.findByLibro(libro);
            totalEjemplares.put(libro.getId(), ejemplares.size());
            int disp = 0;
            for (Ejemplar e : ejemplares) {
                if (cl.ucn.app.model.biblioteca.EstadoEjemplar.DISPONIBLE == e.getEstado()) {
                    disp++;
                }
            }
            disponibles.put(libro.getId(), disp);
        }

        Map<String, Object> model = new HashMap<>();
        model.put("usuarioNombre", usuarioNombre);
        model.put("usuarioRol", ctx.sessionAttribute("usuarioRol"));
        model.put("libros", libros);
        model.put("totalEjemplares", totalEjemplares);
        model.put("disponibles", disponibles);
        ctx.render("biblioteca/libros.jte", model);
    }

    public void buscar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        String termino = ctx.queryParam("termino");
        List<Libro> resultados = new java.util.ArrayList<>();
        if (termino != null && !termino.isBlank()) {
            resultados = busquedaService.buscar(termino);
        }

        Map<String, Object> model = new HashMap<>();
        model.put("usuarioNombre", usuarioNombre);
        model.put("resultados", resultados);
        model.put("termino", termino != null ? termino : "");
        ctx.render("biblioteca/buscar.jte", model);
    }

    public void formularioRegistrarLibro(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        Map<String, Object> model = new HashMap<>();
        model.put("usuarioNombre", usuarioNombre);
        model.put("error", null);
        model.put("titulo", "");
        model.put("autor", "");
        model.put("categoria", "");
        model.put("isbn", "");
        ctx.render("biblioteca/registrar-libro.jte", model);
    }

    public void registrarLibro(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        String titulo = ctx.formParam("titulo");
        String autor = ctx.formParam("autor");
        String categoria = ctx.formParam("categoria");
        String isbn = ctx.formParam("isbn");

        if (titulo == null || titulo.isBlank() || autor == null || autor.isBlank()
                || categoria == null || categoria.isBlank() || isbn == null || isbn.isBlank()) {
            Map<String, Object> model = new HashMap<>();
            model.put("usuarioNombre", usuarioNombre);
            model.put("error", "Todos los campos son obligatorios");
            model.put("titulo", titulo);
            model.put("autor", autor);
            model.put("categoria", categoria);
            model.put("isbn", isbn);
            ctx.render("biblioteca/registrar-libro.jte", model);
            return;
        }

        try {
            libroService.registrarLibro(titulo.trim(), autor.trim(), categoria.trim(), isbn.trim());
            ctx.redirect("/biblioteca/libros");
        } catch (ValidacionException e) {
            String msg = e.getMessage();
            String isbnPersistido = msg.toLowerCase().contains("isbn") ? "" : isbn;
            Map<String, Object> model = new HashMap<>();
            model.put("usuarioNombre", usuarioNombre);
            model.put("error", e.getMessage());
            model.put("titulo", titulo);
            model.put("autor", autor);
            model.put("categoria", categoria);
            model.put("isbn", isbnPersistido);
            ctx.render("biblioteca/registrar-libro.jte", model);
        }
    }

    public void formularioRegistrarEjemplar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        List<Libro> libros = libroService.listarTodos();
        Map<String, Object> model = new HashMap<>();
        model.put("usuarioNombre", usuarioNombre);
        model.put("libros", libros);
        model.put("error", null);
        model.put("mensaje", null);
        model.put("libroId", "");
        ctx.render("biblioteca/registrar-ejemplar.jte", model);
    }

    public void registrarEjemplar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        String libroIdStr = ctx.formParam("libroId");
        String cantidadStr = ctx.formParam("cantidad");

        List<Libro> libros = libroService.listarTodos();
        try {
            Long libroId = Long.parseLong(libroIdStr);
            int cantidad = Integer.parseInt(cantidadStr != null ? cantidadStr : "1");
            if (cantidad < 1 || cantidad > 100) {
                throw new ValidacionException("La cantidad debe estar entre 1 y 100");
            }
            ejemplarService.agregarEjemplares(libroId, cantidad);
            Map<String, Object> model = new HashMap<>();
            model.put("usuarioNombre", usuarioNombre);
            model.put("libros", libros);
            model.put("error", null);
            model.put("mensaje", cantidad + (cantidad == 1 ? " ejemplar registrado" : " ejemplares registrados") + " exitosamente");
            model.put("libroId", libroIdStr);
            ctx.render("biblioteca/registrar-ejemplar.jte", model);
        } catch (ValidacionException e) {
            Map<String, Object> model = new HashMap<>();
            model.put("usuarioNombre", usuarioNombre);
            model.put("libros", libros);
            model.put("error", e.getMessage());
            model.put("mensaje", null);
            model.put("libroId", libroIdStr);
            ctx.render("biblioteca/registrar-ejemplar.jte", model);
        } catch (NumberFormatException e) {
            Map<String, Object> model = new HashMap<>();
            model.put("usuarioNombre", usuarioNombre);
            model.put("libros", libros);
            model.put("error", "Cantidad invalida");
            model.put("mensaje", null);
            model.put("libroId", libroIdStr);
            ctx.render("biblioteca/registrar-ejemplar.jte", model);
        }
    }
}
