package cl.ucn.app.controller.talleres;

import cl.ucn.app.dto.talleres.ResultadoAnulacion;
import cl.ucn.app.dto.talleres.ResultadoInscripcion;
import cl.ucn.app.exception.AccesoDenegadoException;
import cl.ucn.app.exception.RecursoNoEncontradoException;
import cl.ucn.app.exception.ReglaNegocioException;
import cl.ucn.app.service.talleres.InscripcionService;
import io.javalin.http.Context;

import java.util.Map;

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

    /** POST /inscripciones/{id}/anular */
    public void anular(Context ctx) {
        var sesion = requerirSesion(ctx);
        if (sesion.isEmpty()) return;

        Long inscripcionId = idDesdeRuta(ctx, "id");
        String destino = rutaRetornoSegura(ctx.formParam("volver"));

        try {
            if (inscripcionId == null) {
                throw new RecursoNoEncontradoException("Inscripción no válida.");
            }
            ResultadoAnulacion resultado = inscripcionService.anular(
                    inscripcionId, sesion.get().id(), sesion.get().esAdmin()
            );
            if (resultado.huboPromocion()) {
                flashOk(ctx, "Inscripción anulada. Se promovió desde la lista de espera a: " + resultado.promovidoNombre() + ".");
            } else {
                flashOk(ctx, "Inscripción anulada correctamente.");
            }
        } catch (ReglaNegocioException | RecursoNoEncontradoException | AccesoDenegadoException e) {
            flashError(ctx, e.getMessage());
        }
        ctx.redirect(destino);
    }

    /** GET /mis-inscripciones */
    public void misInscripciones(Context ctx) {
        var sesion = requerirSesion(ctx);
        if (sesion.isEmpty()) return;

        Map<String, Object> model = modeloBase(ctx, sesion.get(), "Mis inscripciones");
        model.put("inscripciones", inscripcionService.misInscripciones(sesion.get().id()));
        ctx.render("talleres/mis-inscripciones.jte", model);
    }

    private String rutaRetornoSegura(String volver) {
        if (volver == null || volver.isBlank()) {
            return "/mis-inscripciones";
        }
        String limpia = volver.trim();
        if (limpia.startsWith("/") && !limpia.startsWith("//")) {
            return limpia;
        }
        return "/mis-inscripciones";
    }
}
