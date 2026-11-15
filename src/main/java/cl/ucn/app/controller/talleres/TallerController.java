package cl.ucn.app.controller.talleres;

import cl.ucn.app.dto.talleres.NuevoTaller;
import cl.ucn.app.dto.talleres.TallerResumen;
import cl.ucn.app.exception.RecursoNoEncontradoException;
import cl.ucn.app.exception.ReglaNegocioException;
import cl.ucn.app.model.talleres.Taller;
import cl.ucn.app.service.talleres.InscripcionService;
import cl.ucn.app.service.talleres.TallerService;
import io.javalin.http.Context;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

/** Controlador del catálogo de talleres: listado con filtros, detalle y registro (admin). */
public class TallerController extends ControladorBase {

    private final TallerService tallerService;
    private final InscripcionService inscripcionService;

    public TallerController(TallerService tallerService, InscripcionService inscripcionService) {
        this.tallerService = tallerService;
        this.inscripcionService = inscripcionService;
    }

    /** GET /talleres?categoria=X&fecha=yyyy-MM-dd */
    public void listar(Context ctx) {
        var sesion = requerirSesion(ctx);
        if (sesion.isEmpty()) return;

        String categoria = ctx.queryParam("categoria");
        String fechaTexto = ctx.queryParam("fecha");
        LocalDate fecha = null;
        Map<String, Object> model = modeloBase(ctx, sesion.get(), "Talleres");
        if (fechaTexto != null && !fechaTexto.isBlank()) {
            try {
                fecha = LocalDate.parse(fechaTexto);
            } catch (DateTimeParseException e) {
                model.put("flashError", "La fecha del filtro no es válida.");
            }
        }

        model.put("talleres", tallerService.listarDisponibles(categoria, fecha));
        model.put("categorias", tallerService.listarCategorias());
        model.put("categoriaSeleccionada", categoria == null ? "" : categoria);
        model.put("fechaSeleccionada", fecha == null ? "" : fecha.toString());
        ctx.render("talleres/lista.jte", model);
    }

    /** GET /talleres/nuevo — formulario de creación (solo admin). */
    public void formularioNuevo(Context ctx) {
        var sesion = requerirAdmin(ctx);
        if (sesion.isEmpty()) return;

        Map<String, Object> model = modeloBase(ctx, sesion.get(), "Nuevo taller");
        agregarDatosFormulario(model, "", "", "", "", "", "", "", "");
        ctx.render("talleres/form.jte", model);
    }

    /** POST /talleres — valida y registra un nuevo taller (solo admin). */
    public void registrar(Context ctx) {
        var sesion = requerirAdmin(ctx);
        if (sesion.isEmpty()) return;

        String nombre = valorForm(ctx, "nombre");
        String categoria = valorForm(ctx, "categoria");
        String descripcion = valorForm(ctx, "descripcion");
        String fechaTxt = valorForm(ctx, "fecha");
        String horaInicioTxt = valorForm(ctx, "horaInicio");
        String horaFinTxt = valorForm(ctx, "horaFin");
        String lugar = valorForm(ctx, "lugar");
        String cupoTxt = valorForm(ctx, "cupoMaximo");

        try {
            LocalDate fecha = parsearFecha(fechaTxt);
            LocalTime horaInicio = parsearHora(horaInicioTxt, "hora de inicio");
            LocalTime horaFin = parsearHora(horaFinTxt, "hora de término");
            Integer cupo = parsearEntero(cupoTxt, "cupo máximo");

            Taller creado = tallerService.registrar(new NuevoTaller(
                    nombre, descripcion, categoria, fecha, horaInicio, horaFin, lugar, cupo
            ));
            flashOk(ctx, "Taller \"" + creado.getNombre() + "\" registrado correctamente.");
            ctx.redirect("/talleres/" + creado.getId());
        } catch (ReglaNegocioException e) {
            ctx.status(400);
            Map<String, Object> model = modeloBase(ctx, sesion.get(), "Nuevo taller");
            model.put("flashError", e.getMessage());
            agregarDatosFormulario(model, nombre, categoria, descripcion, fechaTxt, horaInicioTxt, horaFinTxt, lugar, cupoTxt);
            ctx.render("talleres/form.jte", model);
        }
    }

    /** GET /talleres/{id} — detalle; el admin ve además inscritos y lista de espera. */
    public void detalle(Context ctx) {
        var sesion = requerirSesion(ctx);
        if (sesion.isEmpty()) return;

        Long id = idDesdeRuta(ctx, "id");
        try {
            if (id == null) throw new RecursoNoEncontradoException("Taller no válido.");
            TallerResumen taller = tallerService.obtenerResumen(id);
            Map<String, Object> model = modeloBase(ctx, sesion.get(), taller.nombre());
            model.put("taller", taller);
            // TODO [Tarea 4 - RF6]: si sesion.get().esAdmin(), agregar al modelo
            //  inscripcionService.listarInscritos(id) y mostrarlo en detalle.jte (inscritos y lista de espera).
            ctx.render("talleres/detalle.jte", model);
        } catch (RecursoNoEncontradoException e) {
            flashError(ctx, e.getMessage());
            ctx.redirect("/talleres");
        }
    }

    private void agregarDatosFormulario(Map<String, Object> model, String nombre, String categoria,
                                        String descripcion, String fecha, String horaInicio,
                                        String horaFin, String lugar, String cupoMaximo) {
        model.put("nombre", nombre);
        model.put("categoria", categoria);
        model.put("descripcion", descripcion);
        model.put("fecha", fecha);
        model.put("horaInicio", horaInicio);
        model.put("horaFin", horaFin);
        model.put("lugar", lugar);
        model.put("cupoMaximo", cupoMaximo);
    }

    private String valorForm(Context ctx, String campo) {
        String v = ctx.formParam(campo);
        return v == null ? "" : v.trim();
    }

    private LocalDate parsearFecha(String texto) {
        if (texto.isEmpty()) {
            throw new ReglaNegocioException("La fecha del taller es obligatoria.");
        }
        try {
            return LocalDate.parse(texto);
        } catch (DateTimeParseException e) {
            throw new ReglaNegocioException("El formato de la fecha no es válido.");
        }
    }

    private LocalTime parsearHora(String texto, String etiqueta) {
        if (texto.isEmpty()) {
            throw new ReglaNegocioException("La " + etiqueta + " es obligatoria.");
        }
        try {
            return LocalTime.parse(texto);
        } catch (DateTimeParseException e) {
            throw new ReglaNegocioException("El formato de la " + etiqueta + " no es válido.");
        }
    }

    private Integer parsearEntero(String texto, String etiqueta) {
        if (texto.isEmpty()) {
            throw new ReglaNegocioException("El " + etiqueta + " es obligatorio.");
        }
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new ReglaNegocioException("El " + etiqueta + " debe ser un número entero válido.");
        }
    }
}
