package cl.ucn.app.controller.talleres;

import cl.ucn.app.dto.talleres.ResultadoInscripcion;
import cl.ucn.app.exception.RecursoNoEncontradoException;
import cl.ucn.app.exception.ReglaNegocioException;
import cl.ucn.app.service.talleres.InscripcionService;
import io.javalin.http.Context;


/** Controlador de inscripciones: inscribir, anular y ver mis inscripciones. */
public class InscripcionController extends ControladorBase {

    private final InscripcionService inscripcionService;

    public InscripcionController(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    /** POST /talleres/{id}/inscribir */
    public void inscribir(Context ctx) {
        var sesion = requerirSesion(ctx);
        if (sesion.isEmpty()) return;

        Long tallerId = idDesdeRuta(ctx, "id");
        try {
            if (tallerId == null) throw new RecursoNoEncontradoException("Taller no válido.");
            ResultadoInscripcion r = inscripcionService.inscribir(tallerId, sesion.get().id());
            if (r.quedoEnEspera()) {
                flashOk(ctx, "El taller está lleno: quedaste en la lista de espera (posición " + r.posicionEspera() + ").");
            } else {
                flashOk(ctx, "¡Inscripción realizada! Tienes tu cupo asegurado.");
            }
        } catch (ReglaNegocioException | RecursoNoEncontradoException e) {
            flashError(ctx, e.getMessage());
        }
        ctx.redirect(tallerId == null ? "/talleres" : "/talleres/" + tallerId);
    }

    // TODO [Tarea 3 - RF5 y RF7]: agregar aquí
    //  - anular(Context ctx): POST /inscripciones/{id}/anular. Llamar
    //    inscripcionService.anular(id, sesion.id(), sesion.esAdmin()); mensaje de éxito que diga si alguien
    //    subió desde la lista de espera; capturar ReglaNegocioException, RecursoNoEncontradoException y
    //    AccesoDenegadoException con flashError. Redirigir a la página desde donde se anuló
    //    (form param "volver", aceptando solo rutas que empiecen con "/" y no con "//").
    //  - misInscripciones(Context ctx): GET /mis-inscripciones. Usar inscripcionService.misInscripciones(...)
    //    y renderizar "talleres/mis-inscripciones.jte" (crear la vista: taller, fecha, horario, estado y
    //    botón Anular si no está anulada).
    //  - Registrar ambas rutas en TallerRoutes.
}
