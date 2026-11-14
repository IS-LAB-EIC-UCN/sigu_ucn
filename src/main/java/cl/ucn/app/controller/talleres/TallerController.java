package cl.ucn.app.controller.talleres;

import cl.ucn.app.dto.talleres.TallerResumen;
import cl.ucn.app.exception.RecursoNoEncontradoException;
import cl.ucn.app.service.talleres.InscripcionService;
import cl.ucn.app.service.talleres.TallerService;
import io.javalin.http.Context;

import java.time.LocalDate;
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

    // TODO [Tarea 1 - RF2 y RF3]: agregar aquí
    //  - formularioNuevo(Context ctx): GET /talleres/nuevo, solo admin (usar requerirAdmin),
    //    renderiza "talleres/form.jte" (crear esa vista con los campos de NuevoTaller).
    //  - registrar(Context ctx): POST /talleres, solo admin. Leer los formParam, convertir fecha/horas/cupo
    //    (si el formato es inválido, ReglaNegocioException), llamar tallerService.registrar(...),
    //    flashOk + redirect a /talleres/{id}. Si hay ReglaNegocioException: volver a mostrar el
    //    formulario con los datos ingresados y el error (status 400).
    //  - Registrar ambas rutas en TallerRoutes ANTES de "/talleres/{id}".
}
