package cl.ucn.app.controller.biblioteca;

import cl.ucn.app.service.biblioteca.LibroService;
import cl.ucn.app.service.biblioteca.LectorService;
import cl.ucn.app.service.biblioteca.PrestamoLibroService;
import io.javalin.http.Context;

import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;

public class LibroController {

    private final LibroService libroService = new LibroService();
    private final LectorService lectorService = new LectorService();
    private final PrestamoLibroService prestamoService = new PrestamoLibroService();

    public void listar(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        model.put("usuarioNombre", "Usuario"); // Ajustar si el sistema base maneja sesiones
        model.put("libros", libroService.listarTodos());
        ctx.render("biblioteca/libros.jte", model);
    }

    public void buscar(Context ctx) {
        String termino = ctx.queryParam("termino");
        Map<String, Object> model = new HashMap<>();
        model.put("usuarioNombre", "Usuario");
        model.put("termino", termino != null ? termino : "");

        if (termino != null && !termino.trim().isEmpty()) {
            // Buscamos por título (podrías mezclar con autor luego)
            model.put("resultados", libroService.buscarPorTitulo(termino));
        } else {
            model.put("resultados", java.util.Collections.emptyList());
        }
        ctx.render("biblioteca/buscar.jte", model);
    }

    // --- Métodos dummy para formularios de registro que pide BibliotecaRoutes ---
    public void formularioRegistrarLibro(Context ctx) { ctx.result("Formulario Libro Pendiente"); }
    public void registrarLibro(Context ctx) { ctx.result("Registrar Libro Pendiente"); }
    public void formularioRegistrarEjemplar(Context ctx) { ctx.result("Formulario Ejemplar Pendiente"); }
    public void registrarEjemplar(Context ctx) { ctx.result("Registrar Ejemplar Pendiente"); }
    public void historial(Context ctx) { ctx.result("Historial Pendiente"); }
    public void formularioDevolucion(Context ctx) { ctx.result("Formulario Devolucion Pendiente"); }
    public void registrarDevolucion(Context ctx) { ctx.result("Registrar Devolucion Pendiente"); }

    // --- Préstamos ---
    public void formularioPrestamo(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        model.put("usuarioNombre", "Usuario");
        model.put("lectores", lectorService.listarTodos());
        // Pasamos lista vacía temporalmente para ejemplares hasta que exista EjemplarService
        model.put("ejemplares", java.util.Collections.emptyList());

        if (ctx.sessionAttribute("error") != null) {
            model.put("error", ctx.sessionAttribute("error"));
            ctx.consumeSessionAttribute("error");
        }
        ctx.render("biblioteca/prestamo-form.jte", model);
    }

    public void solicitarPrestamo(Context ctx) {
        try {
            Long lectorId = Long.parseLong(ctx.formParam("lectorId"));
            Long ejemplarId = Long.parseLong(ctx.formParam("ejemplarId"));
            LocalDate fechaVencimiento = LocalDate.parse(ctx.formParam("fechaVencimiento"));

            prestamoService.solicitarPrestamo(lectorId, ejemplarId, fechaVencimiento);
            ctx.redirect("/biblioteca/historial");
        } catch (Exception e) {
            ctx.sessionAttribute("error", e.getMessage());
            ctx.redirect("/biblioteca/prestamo/nuevo");
        }
    }
}