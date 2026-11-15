package cl.ucn.app.controller;

import cl.ucn.app.dto.RegistroForm;
import cl.ucn.app.exception.RegistroInvalidoException;
import cl.ucn.app.service.RegistroService;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * RF-01: pantalla y envío del formulario de registro.
 * Solo traduce HTTP <-> servicio; las reglas viven en RegistroService.
 */
public class RegistroController {

    private static final Logger LOG = LoggerFactory.getLogger(RegistroController.class);

    private final RegistroService registroService;

    public RegistroController(RegistroService registroService) {
        this.registroService = registroService;
    }

    public void showRegistro(Context ctx) {
        if (ctx.sessionAttribute("usuarioId") != null) {
            ctx.redirect("/home");
            return;
        }
        render(ctx, RegistroForm.vacio(), Map.of(), "");
    }

    public void doRegistro(Context ctx) {
        RegistroForm form = new RegistroForm(
                ctx.formParam("nombre"),
                ctx.formParam("correo"),
                ctx.formParam("password"),
                ctx.formParam("confirmacion"),
                ctx.formParam("rol"));
        try {
            registroService.registrar(form);
            ctx.redirect("/login?registrado=1");
        } catch (RegistroInvalidoException e) {
            ctx.status(400);
            render(ctx, form, e.getErrores(), "");
        } catch (RuntimeException e) {
            // RNF-07: se registra el detalle técnico y el usuario ve un mensaje genérico.
            LOG.error("Error inesperado al registrar usuario {}", form.correo(), e);
            ctx.status(500);
            render(ctx, form, Map.of(), "No fue posible completar el registro. Intente nuevamente más tarde.");
        }
    }

    private void render(Context ctx, RegistroForm form, Map<String, String> errores, String errorGeneral) {
        Map<String, Object> model = new HashMap<>();
        // Se conservan nombre, correo y rol; las contraseñas nunca se devuelven a la vista (RNF-06).
        model.put("nombre", form.nombreOVacio());
        model.put("correo", form.correoOVacio());
        model.put("rolSeleccionado", form.rolOVacio());
        model.put("roles", RegistroService.ROLES_REGISTRABLES);
        model.put("errores", errores);
        model.put("errorGeneral", errorGeneral);
        ctx.render("registro.jte", model);
    }
}
