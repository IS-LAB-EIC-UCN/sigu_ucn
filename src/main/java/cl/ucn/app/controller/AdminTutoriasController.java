package cl.ucn.app.controller;

import cl.ucn.app.dto.AsignaturaForm;
import cl.ucn.app.dto.TutorForm;
import cl.ucn.app.model.Asignatura;
import cl.ucn.app.model.Tutor;
import cl.ucn.app.service.AsignaturaService;
import cl.ucn.app.service.TutorService;
import cl.ucn.app.service.TutoriaValidacionException;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class AdminTutoriasController {

    private static final String RUTA_ASIGNATURAS = "/tutorias/admin/asignaturas";
    private static final String RUTA_TUTORES = "/tutorias/admin/tutores";
    private static final String MENSAJE = "tutoriasMensaje";
    private static final String ERROR = "tutoriasError";

    private final AsignaturaService asignaturaService;
    private final TutorService tutorService;

    public AdminTutoriasController(AsignaturaService asignaturaService, TutorService tutorService) {
        this.asignaturaService = asignaturaService;
        this.tutorService = tutorService;
    }

    public void verificarAdministrador(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        if (usuarioNombre == null) {
            ctx.redirect("/login");
            ctx.skipRemainingHandlers();
            return;
        }

        if (!"ADMIN".equals(ctx.sessionAttribute("usuarioRol"))) {
            Map<String, Object> model = crearModelo(ctx, "Acceso restringido - SIGU-UCN");
            model.put("mensaje", "Solo el administrador puede gestionar asignaturas y tutores.");
            ctx.status(403);
            ctx.render("tutorias/error.jte", model);
            ctx.skipRemainingHandlers();
        }
    }

    public void listarAsignaturas(Context ctx) {
        Map<String, Object> model = crearModelo(ctx, "Asignaturas - SIGU-UCN");
        model.put("asignaturas", asignaturaService.listar());
        model.put("mensaje", ctx.consumeSessionAttribute(MENSAJE));
        model.put("error", ctx.consumeSessionAttribute(ERROR));
        ctx.render("tutorias/admin/asignaturas.jte", model);
    }

    public void mostrarNuevaAsignatura(Context ctx) {
        mostrarFormularioAsignatura(ctx, AsignaturaForm.vacio(), null, null);
    }

    public void crearAsignatura(Context ctx) {
        AsignaturaForm form = leerFormularioAsignatura(ctx);

        try {
            Asignatura asignatura = asignaturaService.crear(form);
            ctx.sessionAttribute(MENSAJE, "Se creó la asignatura " + asignatura.getCodigo() + ".");
            ctx.redirect(RUTA_ASIGNATURAS);
        } catch (TutoriaValidacionException e) {
            ctx.status(400);
            mostrarFormularioAsignatura(ctx, form, null, e);
        }
    }

    public void mostrarEditarAsignatura(Context ctx) {
        try {
            Asignatura asignatura = asignaturaService.buscarPorId(leerId(ctx));
            mostrarFormularioAsignatura(ctx, AsignaturaForm.desde(asignatura), asignatura.getId(), null);
        } catch (TutoriaValidacionException e) {
            ctx.sessionAttribute(ERROR, e.getMessage());
            ctx.redirect(RUTA_ASIGNATURAS);
        }
    }

    public void actualizarAsignatura(Context ctx) {
        Long id = leerId(ctx);
        AsignaturaForm form = leerFormularioAsignatura(ctx);

        try {
            Asignatura asignatura = asignaturaService.actualizar(id, form);
            ctx.sessionAttribute(MENSAJE, "Se actualizó la asignatura " + asignatura.getCodigo() + ".");
            ctx.redirect(RUTA_ASIGNATURAS);
        } catch (TutoriaValidacionException e) {
            if (e.getCampo() == null) {
                ctx.sessionAttribute(ERROR, e.getMessage());
                ctx.redirect(RUTA_ASIGNATURAS);
                return;
            }
            ctx.status(400);
            mostrarFormularioAsignatura(ctx, form, id, e);
        }
    }

    public void eliminarAsignatura(Context ctx) {
        try {
            Asignatura asignatura = asignaturaService.eliminar(leerId(ctx));
            ctx.sessionAttribute(MENSAJE, "Se eliminó la asignatura " + asignatura.getCodigo() + ".");
        } catch (TutoriaValidacionException e) {
            ctx.sessionAttribute(ERROR, e.getMessage());
        }
        ctx.redirect(RUTA_ASIGNATURAS);
    }

    public void listarTutores(Context ctx) {
        Map<String, Object> model = crearModelo(ctx, "Tutores - SIGU-UCN");
        model.put("tutores", tutorService.listar());
        model.put("mensaje", ctx.consumeSessionAttribute(MENSAJE));
        ctx.render("tutorias/admin/tutores.jte", model);
    }

    public void mostrarNuevoTutor(Context ctx) {
        mostrarFormularioTutor(ctx, TutorForm.vacio(), null);
    }

    public void registrarTutor(Context ctx) {
        TutorForm form = new TutorForm(ctx.formParam("correo"), ctx.formParams("asignaturaIds"));

        try {
            Tutor tutor = tutorService.registrar(form);
            ctx.sessionAttribute(MENSAJE, "Tutor registrado: " + tutor.getEstudiante().getNombre() + ".");
            ctx.redirect(RUTA_TUTORES);
        } catch (TutoriaValidacionException e) {
            ctx.status(400);
            mostrarFormularioTutor(ctx, form, e);
        }
    }

    private void mostrarFormularioAsignatura(Context ctx, AsignaturaForm form, Long asignaturaId,
                                             TutoriaValidacionException error) {
        String titulo = asignaturaId == null ? "Nueva asignatura - SIGU-UCN" : "Editar asignatura - SIGU-UCN";
        Map<String, Object> model = crearModelo(ctx, titulo);
        model.put("form", form);
        model.put("asignaturaId", asignaturaId);
        agregarError(model, error);
        ctx.render("tutorias/admin/asignatura-form.jte", model);
    }

    private void mostrarFormularioTutor(Context ctx, TutorForm form, TutoriaValidacionException error) {
        Map<String, Object> model = crearModelo(ctx, "Registrar tutor - SIGU-UCN");
        model.put("form", form);
        model.put("asignaturas", asignaturaService.listar());
        agregarError(model, error);
        ctx.render("tutorias/admin/tutor-form.jte", model);
    }

    private void agregarError(Map<String, Object> model, TutoriaValidacionException error) {
        if (error != null) {
            model.put("error", error.getMessage());
            model.put("campoError", error.getCampo());
        }
    }

    private AsignaturaForm leerFormularioAsignatura(Context ctx) {
        return new AsignaturaForm(
                ctx.formParam("codigo"),
                ctx.formParam("nombre"),
                ctx.formParam("carrera"),
                ctx.formParam("semestre")
        );
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
