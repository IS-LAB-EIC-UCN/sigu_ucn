package cl.ucn.app.controller.biblioteca;

import cl.ucn.app.auth.LectorSessionHelper;
import cl.ucn.app.controller.biblioteca.PrestamoConMulta;
import cl.ucn.app.exceptions.ConflictoEstadoException;
import cl.ucn.app.exceptions.RecursoNoEncontradoException;
import cl.ucn.app.exceptions.ValidacionException;
import cl.ucn.app.model.biblioteca.Ejemplar;
import cl.ucn.app.model.biblioteca.Lector;
import cl.ucn.app.model.biblioteca.Libro;
import cl.ucn.app.model.biblioteca.Multa;
import cl.ucn.app.model.biblioteca.PrestamoLibro;
import cl.ucn.app.repository.biblioteca.EjemplarRepository;
import cl.ucn.app.repository.biblioteca.MultaRepository;
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
    private final MultaRepository multaRepository = new MultaRepository();

    public void formularioPrestamo(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        String usuarioRol = ctx.sessionAttribute("usuarioRol");
        if (!"ADMIN".equals(usuarioRol)) {
            Lector lector = LectorSessionHelper.obtenerExistente(ctx);
            if (lector == null) {
                String returnTo = "/biblioteca/prestamo/nuevo";
                String libroIdQuery = ctx.queryParam("libroId");
                if (libroIdQuery != null && !libroIdQuery.isBlank()) {
                    returnTo += "?libroId=" + libroIdQuery;
                }
                try {
                    ctx.redirect("/biblioteca/mis-datos?returnTo=" + java.net.URLEncoder.encode(returnTo, "UTF-8"));
                } catch (java.io.UnsupportedEncodingException e) {
                    ctx.redirect("/biblioteca/mis-datos?returnTo=" + returnTo);
                }
                return;
            }
        }
        String libroIdQuery = ctx.queryParam("libroId");
        reRenderFormularioPrestamo(ctx, usuarioNombre, null, libroIdQuery != null ? libroIdQuery : "", "");
    }

    public void solicitarPrestamo(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        String usuarioRol = ctx.sessionAttribute("usuarioRol");

        String libroIdStr = ctx.formParam("libroId");
        String fechaVencimientoStr = ctx.formParam("fechaVencimiento");

        if (libroIdStr == null || libroIdStr.isBlank()
                || fechaVencimientoStr == null || fechaVencimientoStr.isBlank()) {
            reRenderFormularioPrestamo(ctx, usuarioNombre,
                "Debe completar todos los campos del formulario",
                "", "");
            return;
        }

        Long libroId = Long.parseLong(libroIdStr);
        LocalDate fechaVencimiento = LocalDate.parse(fechaVencimientoStr);

        try {
            if ("ADMIN".equals(usuarioRol)) {
                String lectorIdStr = ctx.formParam("lectorId");
                if (lectorIdStr == null || lectorIdStr.isBlank()) {
                    reRenderFormularioPrestamo(ctx, usuarioNombre, "Debe seleccionar un lector", libroIdStr, fechaVencimientoStr);
                    return;
                }
                Long lectorId = Long.parseLong(lectorIdStr);
                prestamoService.solicitarPrestamoActivo(lectorId, libroId, fechaVencimiento);
            } else {
                Lector lector = LectorSessionHelper.obtenerExistente(ctx);
                if (lector == null) {
                    ctx.redirect("/biblioteca/mis-datos");
                    return;
                }
                if (lector.isBloqueado()) {
                    reRenderFormularioPrestamo(ctx, usuarioNombre,
                        "Su cuenta está bloqueada por deudas pendientes. Debe acercarse a la administración de la biblioteca a regularizar su situación.",
                        libroIdStr, fechaVencimientoStr);
                    return;
                }
                prestamoService.solicitarPrestamo(lector.getId(), libroId, fechaVencimiento);
            }
            if ("ADMIN".equals(usuarioRol)) {
                ctx.redirect("/biblioteca/libros?asignado=1");
            } else {
                ctx.redirect("/biblioteca/historial");
            }
        } catch (ValidacionException e) {
            reRenderFormularioPrestamo(ctx, usuarioNombre, e.getMessage(),
                libroIdStr, fechaVencimientoStr);
        } catch (Exception e) {
            reRenderFormularioPrestamo(ctx, usuarioNombre, e.getMessage(),
                libroIdStr, fechaVencimientoStr);
        }
    }

    private void reRenderFormularioPrestamo(Context ctx, String usuarioNombre,
                                           String errorMsg, String libroId,
                                           String fechaVencimiento) {
        Map<Libro, Integer> disponiblesPorLibro = new HashMap<>();
        for (Ejemplar ej : ejemplarRepository.findAll()) {
            if (cl.ucn.app.model.biblioteca.EstadoEjemplar.DISPONIBLE == ej.getEstado()) {
                disponiblesPorLibro.merge(ej.getLibro(), 1, Integer::sum);
            }
        }
        List<LibroDisponible> librosDisponibles = new ArrayList<>();
        for (Map.Entry<Libro, Integer> entry : disponiblesPorLibro.entrySet()) {
            librosDisponibles.add(new LibroDisponible(entry.getKey(), entry.getValue()));
        }
        Map<String, Object> model = new HashMap<>();
        model.put("usuarioNombre", usuarioNombre);
        model.put("usuarioRol", ctx.sessionAttribute("usuarioRol"));
        model.put("librosDisponibles", librosDisponibles);
        model.put("error", errorMsg);
        model.put("libroId", libroId != null ? libroId : "");
        model.put("fechaVencimiento", fechaVencimiento != null ? fechaVencimiento : "");

        if ("ADMIN".equals(ctx.sessionAttribute("usuarioRol"))) {
            model.put("lectores", new cl.ucn.app.repository.biblioteca.LectorRepository().findAll());
        }

        ctx.render("biblioteca/prestamo-form.jte", model);
    }

    public void formularioDevolucion(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        List<PrestamoLibro> activos = new ArrayList<>();
        for (PrestamoLibro p : prestamoLibroRepository.findAll()) {
            if (cl.ucn.app.model.biblioteca.EstadoPrestamo.ACTIVO == p.getEstado()) {
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
        Long prestamoId = Long.parseLong(ctx.formParam("prestamoId"));
        devolucionService.registrarDevolucion(prestamoId);
        ctx.redirect("/biblioteca/historial");
    }

    public void confirmarEntrega(Context ctx) {
        String usuarioRol = ctx.sessionAttribute("usuarioRol");
        if (!"ADMIN".equals(usuarioRol)) {
            ctx.status(403);
            return;
        }
        Long prestamoId = Long.parseLong(ctx.formParam("prestamoId"));
        prestamoService.confirmarEntrega(prestamoId);
        ctx.redirect("/biblioteca/historial");
    }

    public void solicitarDevolucion(Context ctx) {
        Lector lector = LectorSessionHelper.obtenerExistente(ctx);
        if (lector == null) {
            ctx.redirect("/biblioteca/historial");
            return;
        }
        Long prestamoId = Long.parseLong(ctx.formParam("prestamoId"));
        prestamoService.solicitarDevolucion(prestamoId);
        ctx.redirect("/biblioteca/historial");
    }

    public void confirmarDevolucion(Context ctx) {
        String usuarioRol = ctx.sessionAttribute("usuarioRol");
        if (!"ADMIN".equals(usuarioRol)) {
            ctx.status(403);
            return;
        }
        Long prestamoId = Long.parseLong(ctx.formParam("prestamoId"));
        prestamoService.confirmarDevolucion(prestamoId);
        ctx.redirect("/biblioteca/historial");
    }

    public void historial(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        String usuarioRol = ctx.sessionAttribute("usuarioRol");
        boolean esAdmin = "ADMIN".equals(usuarioRol);

        Map<String, Object> model = new HashMap<>();
        model.put("usuarioNombre", usuarioNombre);
        model.put("usuarioRol", usuarioRol);

        if (esAdmin) {
            List<PrestamoLibro> prestamos = historialService.obtenerTodosLosPrestamos();
            List<PrestamoConMulta> prestamosConMulta = new ArrayList<>();
            for (PrestamoLibro p : prestamos) {
                Multa multa = multaRepository.findByPrestamo(p);
                prestamosConMulta.add(new PrestamoConMulta(p, multa));
            }
            model.put("historial", prestamosConMulta);
            model.put("esAdmin", true);
        } else {
            Lector lector = LectorSessionHelper.obtenerExistente(ctx);
            if (lector == null) {
                ctx.redirect("/biblioteca/mis-datos?returnTo=/biblioteca/historial");
                return;
            }
            List<PrestamoLibro> historialLista = historialService.obtenerHistorial(lector.getId());
            List<PrestamoConMulta> prestamosConMulta = new ArrayList<>();
            for (PrestamoLibro p : historialLista) {
                Multa multa = multaRepository.findByPrestamo(p);
                prestamosConMulta.add(new PrestamoConMulta(p, multa));
            }
            model.put("historial", prestamosConMulta);
            model.put("esAdmin", false);
        }

        ctx.render("biblioteca/historial.jte", model);
    }
}
