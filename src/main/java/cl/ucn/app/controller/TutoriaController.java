package cl.ucn.app.controller;

import cl.ucn.app.dto.TutoriaDetalle;
import cl.ucn.app.dto.TutoriaFiltro;
import cl.ucn.app.dto.TutoriaForm;
import cl.ucn.app.model.Tutoria;
import cl.ucn.app.service.ReservaTutoriaService;
import cl.ucn.app.service.TutoriaService;
import cl.ucn.app.service.TutoriaValidacionException;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class TutoriaController {

    private static final String RUTA_LISTADO = "/tutorias";
    private static final String MENSAJE = "tutoriasMensaje";
    private static final String ERROR = "tutoriasError";

    private final TutoriaService tutoriaService;
    private final ReservaTutoriaService reservaService;

    public TutoriaController(TutoriaService tutoriaService, ReservaTutoriaService reservaService) {
        this.tutoriaService = tutoriaService;
        this.reservaService = reservaService;
    }

    public void verificarSesion(Context ctx) {
        if (ctx.sessionAttribute("usuarioId") == null) {
            ctx.redirect("/login");
            ctx.skipRemainingHandlers();
        }
    }

    // ------------------------------------------------------------ listado y detalle

    public void listar(Context ctx) {
        Long usuarioId = ctx.sessionAttribute("usuarioId");
        TutoriaFiltro filtro = TutoriaFiltro.desde(
                ctx.queryParam("asignaturaId"), ctx.queryParam("tutorId"), ctx.queryParam("fecha"));

        Map<String, Object> model = crearModelo(ctx, "Tutorías disponibles - SIGU-UCN");
        model.put("tutorias", tutoriaService.listarDisponibles(filtro));
        model.put("filtro", filtro);
        model.put("esTutor", tutoriaService.esTutor(usuarioId));
        model.put("esAdmin", "ADMIN".equals(ctx.sessionAttribute("usuarioRol")));
        model.put("mensaje", ctx.consumeSessionAttribute(MENSAJE));
        model.put("error", ctx.consumeSessionAttribute(ERROR));
        ctx.render("tutorias/listado.jte", model);
    }

    public void detalle(Context ctx) {
        Long usuarioId = ctx.sessionAttribute("usuarioId");
        Long id = leerId(ctx);

        try {
            TutoriaDetalle tutoria = tutoriaService.buscarDetalle(id);

            Map<String, Object> model = crearModelo(ctx, "Detalle de tutoría - SIGU-UCN");
            model.put("tutoria", tutoria);
            model.put("yaReservada", reservaService.tieneReservaActiva(usuarioId, id));
            model.put("mensaje", ctx.consumeSessionAttribute(MENSAJE));
            model.put("error", ctx.consumeSessionAttribute(ERROR));
            ctx.render("tutorias/detalle.jte", model);
        } catch (TutoriaValidacionException e) {
            mostrarErrorGeneral(ctx, 404, e.getMessage());
        }
    }

    // ------------------------------------------------------------ publicar

    public void mostrarPublicar(Context ctx) {
        Long usuarioId = ctx.sessionAttribute("usuarioId");

        try {
            mostrarFormularioPublicar(ctx, usuarioId, TutoriaForm.vacio(), Map.of());
        } catch (TutoriaValidacionException e) {
            mostrarErrorGeneral(ctx, 403, e.getMessage());
        }
    }

    public void publicar(Context ctx) {
        Long usuarioId = ctx.sessionAttribute("usuarioId");
        TutoriaForm form = new TutoriaForm(
                ctx.formParam("asignaturaId"),
                ctx.formParam("fecha"),
                ctx.formParam("horaInicio"),
                ctx.formParam("horaFin"),
                ctx.formParam("modalidad"),
                ctx.formParam("sala"),
                ctx.formParam("capacidad")
        );

        try {
            Tutoria tutoria = tutoriaService.publicar(usuarioId, form);
            ctx.sessionAttribute(MENSAJE, "Tutoría publicada: " + tutoria.getInicio().toLocalDate()
                    + " a las " + tutoria.getInicio().toLocalTime() + ".");
            ctx.redirect(RUTA_LISTADO);
        } catch (TutoriaValidacionException e) {
            if (e.getErrores().isEmpty()) {
                // Error sin campo asociado (por ejemplo, el usuario no es tutor).
                mostrarErrorGeneral(ctx, 403, e.getMessage());
                return;
            }
            ctx.status(400);
            try {
                mostrarFormularioPublicar(ctx, usuarioId, form, e.getErrores());
            } catch (TutoriaValidacionException sinPerfil) {
                mostrarErrorGeneral(ctx, 403, sinPerfil.getMessage());
            }
        }
    }

    // ------------------------------------------------------------ reservar

    public void reservar(Context ctx) {
        Long usuarioId = ctx.sessionAttribute("usuarioId");
        Long id = leerId(ctx);

        try {
            reservaService.reservar(usuarioId, id);
            ctx.sessionAttribute(MENSAJE, "Reserva confirmada. Ya puedes verla en tu agenda.");
        } catch (TutoriaValidacionException e) {
            ctx.sessionAttribute(ERROR, e.getMessage());
        }
        ctx.redirect(id == null ? RUTA_LISTADO : RUTA_LISTADO + "/" + id);
    }

    // ------------------------------------------------------------ auxiliares

    private void mostrarFormularioPublicar(Context ctx, Long usuarioId, TutoriaForm form,
                                           Map<String, String> errores) {
        Map<String, Object> model = crearModelo(ctx, "Publicar tutoría - SIGU-UCN");
        model.put("form", form);
        model.put("asignaturas", tutoriaService.asignaturasCertificadas(usuarioId));
        model.put("errores", errores);
        ctx.render("tutorias/publicar.jte", model);
    }

    private void mostrarErrorGeneral(Context ctx, int estado, String mensaje) {
        Map<String, Object> model = crearModelo(ctx, "Acceso restringido - SIGU-UCN");
        model.put("mensaje", mensaje);
        ctx.status(estado);
        ctx.render("tutorias/error.jte", model);
    }

    private Long leerId(Context ctx) {
        try {
            return Long.parseLong(ctx.pathParam("id"));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Map<String, Object> crearModelo(Context ctx, String titulo) {
        Map<String, Object> model = new HashMap<>();
        model.put("title", titulo);
        model.put("usuarioNombre", ctx.sessionAttribute("usuarioNombre"));
        return model;
    }
}
