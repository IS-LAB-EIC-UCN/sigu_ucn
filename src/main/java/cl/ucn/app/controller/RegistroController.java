package cl.ucn.app.controller;

import cl.ucn.app.dto.RegistroForm;
import cl.ucn.app.service.RegistroService;
import cl.ucn.app.service.TutoriaValidacionException;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class RegistroController {

    private final RegistroService registroService;

    public RegistroController(RegistroService registroService) {
        this.registroService = registroService;
    }

    public void mostrar(Context ctx) {
        ctx.render("registro.jte", modelo(ctx, null, formularioVacio()));
    }

    public void registrar(Context ctx) {
        RegistroForm form = new RegistroForm(
                ctx.formParam("nombre"),
                ctx.formParam("correo"),
                ctx.formParam("carrera"),
                ctx.formParam("semestre"),
                ctx.formParam("password"),
                ctx.formParam("confirmacion")
        );
        try {
            registroService.registrar(form);
            ctx.sessionAttribute("registroMensaje", "Registro exitoso. Ahora puede iniciar sesión.");
            ctx.redirect("/login");
        } catch (TutoriaValidacionException e) {
            ctx.status(400);
            ctx.render("registro.jte", modelo(ctx, e, form));
        }
    }

    private Map<String, Object> modelo(Context ctx, TutoriaValidacionException error, RegistroForm form) {
        Map<String, Object> model = new HashMap<>();
        model.put("form", form);
        model.put("error", error == null ? null : error.getMessage());
        model.put("campoError", error == null ? null : error.getCampo());
        return model;
    }

    private RegistroForm formularioVacio() {
        return new RegistroForm("", "", "", "", "", "");
    }
}
