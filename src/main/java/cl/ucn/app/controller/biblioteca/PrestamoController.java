package cl.ucn.app.controller.biblioteca;

import cl.ucn.app.exceptions.ValidacionException;
import cl.ucn.app.model.biblioteca.Ejemplar;
import cl.ucn.app.model.biblioteca.Lector;
import cl.ucn.app.model.biblioteca.Libro;
import cl.ucn.app.model.biblioteca.PrestamoLibro;
import cl.ucn.app.repository.biblioteca.EjemplarRepository;
import cl.ucn.app.repository.biblioteca.PrestamoLibroRepository;
import cl.ucn.app.repository.biblioteca.api.IEjemplarRepository;
import cl.ucn.app.repository.biblioteca.api.IPrestamoLibroRepository;
import cl.ucn.app.service.biblioteca.DevolucionService;
import cl.ucn.app.service.biblioteca.HistorialService;
import cl.ucn.app.service.biblioteca.LectorService;
import cl.ucn.app.service.biblioteca.PrestamoLibroService;
import cl.ucn.app.service.biblioteca.api.IDevolucionService;
import cl.ucn.app.service.biblioteca.api.IHistorialService;
import cl.ucn.app.service.biblioteca.api.ILectorService;
import cl.ucn.app.service.biblioteca.api.IPrestamoService;
import io.javalin.http.Context;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrestamoController {

    private final IPrestamoService prestamoService = new PrestamoLibroService();
    private final ILectorService lectorService = new LectorService();
    private final IHistorialService historialService = new HistorialService();
    private final IDevolucionService devolucionService = new DevolucionService();
    private final IEjemplarRepository ejemplarRepository = new EjemplarRepository();
    private final IPrestamoLibroRepository prestamoLibroRepository = new PrestamoLibroRepository();

    public void formularioPrestamo(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) { ctx.redirect("/login"); return; }
        reRenderFormularioPrestamo(ctx, usuarioNombre, null, "", "", "");
    }

    public void solicitarPrestamo(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) { ctx.redirect("/login"); return; }

        String lectorIdStr = ctx.formParam("lectorId");
        String libroIdStr = ctx.formParam("libroId");
        String fechaVencimientoStr = ctx.formParam("fechaVencimiento");

        if (lectorIdStr == null || lectorIdStr.isBlank()
                || libroIdStr == null || libroIdStr.isBlank()
                || fechaVencimientoStr == null || fechaVencimientoStr.isBlank()) {
            reRenderFormularioPrestamo(ctx, usuarioNombre,
                "Debe completar todos los campos del formulario",
                "", "", "");
            return;
        }

        Long lectorId = Long.parseLong(lectorIdStr);
        Long libroId = Long.parseLong(libroIdStr);
        LocalDate fechaVencimiento = LocalDate.parse(fechaVencimientoStr);

        try {
            prestamoService.solicitarPrestamo(lectorId, libroId, fechaVencimiento);
            ctx.redirect("/biblioteca/historial");
        } catch (ValidacionException e) {
            reRenderFormularioPrestamo(ctx, usuarioNombre, e.getMessage(),
                lectorIdStr, libroIdStr, fechaVencimientoStr);
        }
    }

    private void reRenderFormularioPrestamo(Context ctx, String usuarioNombre,
                                           String errorMsg, String lectorId,
                                           String libroId, String fechaVencimiento) {
        List<Lector> lectores = lectorService.listarTodos();
        Map<Libro, Integer> disponiblesPorLibro = new HashMap<>();
        for (Ejemplar ej : ejemplarRepository.findAll()) {
            if ("DISPONIBLE".equalsIgnoreCase(ej.getEstado())) {
                disponiblesPorLibro.merge(ej.getLibro(), 1, Integer::sum);
            }
        }
        List<LibroDisponible> librosDisponibles = new ArrayList<>();
        for (Map.Entry<Libro, Integer> entry : disponiblesPorLibro.entrySet()) {
            librosDisponibles.add(new LibroDisponible(entry.getKey(), entry.getValue()));
        }
        Map<String, Object> model = new HashMap<>();
        model.put("usuarioNombre", usuarioNombre);
        model.put("lectores", lectores);
        model.put("librosDisponibles", librosDisponibles);
        model.put("error", errorMsg);
        model.put("lectorId", lectorId != null ? lectorId : "");
        model.put("libroId", libroId != null ? libroId : "");
        model.put("fechaVencimiento", fechaVencimiento != null ? fechaVencimiento : "");
        ctx.render("biblioteca/prestamo-form.jte", model);
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
}
