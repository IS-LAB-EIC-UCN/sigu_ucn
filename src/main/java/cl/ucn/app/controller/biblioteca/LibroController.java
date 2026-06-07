package cl.ucn.app.controller.biblioteca;

import cl.ucn.app.model.biblioteca.Ejemplar;
import cl.ucn.app.model.biblioteca.Lector;
import cl.ucn.app.model.biblioteca.Libro;
import cl.ucn.app.model.biblioteca.PrestamoLibro;
import cl.ucn.app.repository.biblioteca.EjemplarRepository;
import cl.ucn.app.repository.biblioteca.PrestamoLibroRepository;
import cl.ucn.app.service.biblioteca.BusquedaService;
import cl.ucn.app.service.biblioteca.DevolucionService;
import cl.ucn.app.service.biblioteca.EjemplarService;
import cl.ucn.app.service.biblioteca.HistorialService;
import cl.ucn.app.service.biblioteca.LectorService;
import cl.ucn.app.service.biblioteca.LibroService;
import cl.ucn.app.service.biblioteca.PrestamoLibroService;
import io.javalin.http.Context;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibroController {

    private final LibroService libroService = new LibroService();
    private final LectorService lectorService = new LectorService();
    private final PrestamoLibroService prestamoService = new PrestamoLibroService();
    private final BusquedaService busquedaService = new BusquedaService();
    private final EjemplarRepository ejemplarRepository = new EjemplarRepository();
    private final PrestamoLibroRepository prestamoLibroRepository = new PrestamoLibroRepository();
    private final DevolucionService devolucionService = new DevolucionService();
    private final HistorialService historialService = new HistorialService();
    private final EjemplarService ejemplarService = new EjemplarService();

    public void listar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) { ctx.redirect("/login"); return; }

        List<Libro> libros = libroService.listarTodos();
        Map<Long, Integer> totalEjemplares = new HashMap<>();
        Map<Long, Integer> disponibles = new HashMap<>();
        for (Libro libro : libros) {
            List<Ejemplar> ejemplares = ejemplarRepository.findByLibro(libro);
            totalEjemplares.put(libro.getId(), ejemplares.size());
            int disp = 0;
            for (Ejemplar e : ejemplares) {
                if ("DISPONIBLE".equalsIgnoreCase(e.getEstado())) {
                    disp++;
                }
            }
            disponibles.put(libro.getId(), disp);
        }

        Map<String, Object> model = new HashMap<>();
        model.put("usuarioNombre", usuarioNombre);
        model.put("libros", libros);
        model.put("totalEjemplares", totalEjemplares);
        model.put("disponibles", disponibles);
        ctx.render("biblioteca/libros.jte", model);
    }

    public void buscar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) { ctx.redirect("/login"); return; }

        String termino = ctx.queryParam("termino");
        List<Libro> resultados = new ArrayList<>();
        if (termino != null && !termino.isBlank()) {
            resultados = busquedaService.buscar(termino);
        }

        Map<String, Object> model = new HashMap<>();
        model.put("usuarioNombre", usuarioNombre);
        model.put("resultados", resultados);
        model.put("termino", termino != null ? termino : "");
        ctx.render("biblioteca/buscar.jte", model);
    }

    public void formularioPrestamo(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) { ctx.redirect("/login"); return; }

        List<Lector> lectores = lectorService.listarTodos();
        List<Ejemplar> disponibles = new ArrayList<>();
        for (Ejemplar e : ejemplarRepository.findAll()) {
            if ("DISPONIBLE".equalsIgnoreCase(e.getEstado())) {
                disponibles.add(e);
            }
        }

        Map<String, Object> model = new HashMap<>();
        model.put("usuarioNombre", usuarioNombre);
        model.put("lectores", lectores);
        model.put("ejemplares", disponibles);
        model.put("error", null);
        ctx.render("biblioteca/prestamo-form.jte", model);
    }

    public void solicitarPrestamo(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) { ctx.redirect("/login"); return; }

        try {
            Long lectorId = Long.parseLong(ctx.formParam("lectorId"));
            Long ejemplarId = Long.parseLong(ctx.formParam("ejemplarId"));
            LocalDate fechaVencimiento = LocalDate.parse(ctx.formParam("fechaVencimiento"));

            prestamoService.solicitarPrestamo(lectorId, ejemplarId, fechaVencimiento);
            ctx.redirect("/biblioteca/historial");
        } catch (Exception e) {
            List<Lector> lectores = lectorService.listarTodos();
            List<Ejemplar> disponibles = new ArrayList<>();
            for (Ejemplar ej : ejemplarRepository.findAll()) {
                if ("DISPONIBLE".equalsIgnoreCase(ej.getEstado())) {
                    disponibles.add(ej);
                }
            }
            Map<String, Object> model = new HashMap<>();
            model.put("usuarioNombre", usuarioNombre);
            model.put("lectores", lectores);
            model.put("ejemplares", disponibles);
            model.put("error", e.getMessage());
            ctx.render("biblioteca/prestamo-form.jte", model);
        }
    }

    public void formularioDevolucion(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) { ctx.redirect("/login"); return; }

        List<PrestamoLibro> activos = new ArrayList<>();
        for (PrestamoLibro p : prestamoLibroRepository.findAll()) {
            if ("ACTIVO".equalsIgnoreCase(p.getEstado())) {
                activos.add(p);
            }
        }

        Map<String, Object> model = new HashMap<>();
        model.put("usuarioNombre", usuarioNombre);
        model.put("prestamos", activos);
        model.put("error", null);
        model.put("mensaje", null);
        ctx.render("biblioteca/devolucion-form.jte", model);
    }

    public void registrarDevolucion(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) { ctx.redirect("/login"); return; }

        try {
            Long prestamoId = Long.parseLong(ctx.formParam("prestamoId"));
            devolucionService.registrarDevolucion(prestamoId);

            List<PrestamoLibro> activos = new ArrayList<>();
            for (PrestamoLibro p : prestamoLibroRepository.findAll()) {
                if ("ACTIVO".equalsIgnoreCase(p.getEstado())) {
                    activos.add(p);
                }
            }
            Map<String, Object> model = new HashMap<>();
            model.put("usuarioNombre", usuarioNombre);
            model.put("prestamos", activos);
            model.put("error", null);
            model.put("mensaje", "Devolucion registrada exitosamente");
            ctx.render("biblioteca/devolucion-form.jte", model);
        } catch (Exception e) {
            List<PrestamoLibro> activos = new ArrayList<>();
            for (PrestamoLibro p : prestamoLibroRepository.findAll()) {
                if ("ACTIVO".equalsIgnoreCase(p.getEstado())) {
                    activos.add(p);
                }
            }
            Map<String, Object> model = new HashMap<>();
            model.put("usuarioNombre", usuarioNombre);
            model.put("prestamos", activos);
            model.put("error", e.getMessage());
            model.put("mensaje", null);
            ctx.render("biblioteca/devolucion-form.jte", model);
        }
    }

    public void historial(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) { ctx.redirect("/login"); return; }

        List<Lector> lectores = lectorService.listarTodos();
        List<PrestamoLibro> historialLista = new ArrayList<>();
        String lectorNombre = "";

        String lectorIdParam = ctx.queryParam("lectorId");
        if (lectorIdParam != null && !lectorIdParam.isBlank()) {
            try {
                Long lectorId = Long.parseLong(lectorIdParam);
                historialLista = historialService.obtenerHistorial(lectorId);
                for (Lector l : lectores) {
                    if (l.getId().equals(lectorId)) {
                        lectorNombre = l.getNombre();
                        break;
                    }
                }
            } catch (Exception e) {
                // historial vacío
            }
        }

        Map<String, Object> model = new HashMap<>();
        model.put("usuarioNombre", usuarioNombre);
        model.put("lectores", lectores);
        model.put("historial", historialLista);
        model.put("lectorNombre", lectorNombre);
        ctx.render("biblioteca/historial.jte", model);
    }

    public void formularioRegistrarLibro(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) { ctx.redirect("/login"); return; }

        Map<String, Object> model = new HashMap<>();
        model.put("usuarioNombre", usuarioNombre);
        model.put("error", null);
        ctx.render("biblioteca/registrar-libro.jte", model);
    }

    public void registrarLibro(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) { ctx.redirect("/login"); return; }

        String titulo = ctx.formParam("titulo");
        String autor = ctx.formParam("autor");
        String categoria = ctx.formParam("categoria");
        String isbn = ctx.formParam("isbn");

        if (titulo == null || titulo.isBlank() || autor == null || autor.isBlank()
                || categoria == null || categoria.isBlank() || isbn == null || isbn.isBlank()) {
            Map<String, Object> model = new HashMap<>();
            model.put("usuarioNombre", usuarioNombre);
            model.put("error", "Todos los campos son obligatorios");
            ctx.render("biblioteca/registrar-libro.jte", model);
            return;
        }

        try {
            libroService.registrarLibro(titulo.trim(), autor.trim(), categoria.trim(), isbn.trim());
            ctx.redirect("/biblioteca/libros");
        } catch (Exception e) {
            Map<String, Object> model = new HashMap<>();
            model.put("usuarioNombre", usuarioNombre);
            model.put("error", e.getMessage());
            ctx.render("biblioteca/registrar-libro.jte", model);
        }
    }

    public void formularioRegistrarEjemplar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) { ctx.redirect("/login"); return; }

        List<Libro> libros = libroService.listarTodos();
        Map<String, Object> model = new HashMap<>();
        model.put("usuarioNombre", usuarioNombre);
        model.put("libros", libros);
        model.put("error", null);
        model.put("mensaje", null);
        ctx.render("biblioteca/registrar-ejemplar.jte", model);
    }

    public void registrarEjemplar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) { ctx.redirect("/login"); return; }

        try {
            Long libroId = Long.parseLong(ctx.formParam("libroId"));
            ejemplarService.agregarEjemplar(libroId);

            List<Libro> libros = libroService.listarTodos();
            Map<String, Object> model = new HashMap<>();
            model.put("usuarioNombre", usuarioNombre);
            model.put("libros", libros);
            model.put("error", null);
            model.put("mensaje", "Ejemplar registrado exitosamente");
            ctx.render("biblioteca/registrar-ejemplar.jte", model);
        } catch (Exception e) {
            List<Libro> libros = libroService.listarTodos();
            Map<String, Object> model = new HashMap<>();
            model.put("usuarioNombre", usuarioNombre);
            model.put("libros", libros);
            model.put("error", e.getMessage());
            model.put("mensaje", null);
            ctx.render("biblioteca/registrar-ejemplar.jte", model);
        }
    }
}
