package cl.ucn.app.controller;

import cl.ucn.app.model.Usuario;
import cl.ucn.app.service.AuthService;
import cl.ucn.app.util.SessionUtil;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class AuthController {

    private final AuthService authService;

    public AuthController() {
        this.authService = new AuthService();
    }

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public void showLogin(Context ctx) {
        String mensaje = ctx.consumeSessionAttribute("registroMensaje");
        Map<String, Object> model = new HashMap<>();
        model.put("error", "");
        model.put("mensaje", mensaje);
        ctx.render("login.jte", model);
    }

    public void doLogin(Context ctx) {
        ctx.req().getSession().invalidate();
        String correo = ctx.formParam("correo");
        String password = ctx.formParam("password");

        if (correo == null || correo.isBlank() || password == null || password.isBlank()) {
            ctx.status(400);
            ctx.render("login.jte", Map.of("error", "Debe ingresar correo y contraseña."));
            return;
        }

        Usuario usuario = authService.autenticar(correo, password);

        if (usuario == null) {
            ctx.status(401);
            ctx.render("login.jte", Map.of("error", "Credenciales inválidas o usuario inactivo."));
            return;
        }

        ctx.sessionAttribute(SessionUtil.USUARIO_ID, usuario.getId());
        ctx.sessionAttribute(SessionUtil.USUARIO_NOMBRE, usuario.getNombre());
        ctx.sessionAttribute(SessionUtil.USUARIO_CORREO, usuario.getCorreo());
        ctx.sessionAttribute(SessionUtil.USUARIO_ROL, usuario.getRol().getNombre());
        authService.tutorRepository().findByUsuarioId(usuario.getId()).ifPresentOrElse(
                tutor -> {
                    ctx.sessionAttribute(SessionUtil.ES_TUTOR, true);
                    ctx.sessionAttribute(SessionUtil.TUTOR_ID, tutor.getId());
                },
                () -> {
                    ctx.sessionAttribute(SessionUtil.ES_TUTOR, false);
                    ctx.sessionAttribute(SessionUtil.TUTOR_ID, null);
                }
        );

        ctx.redirect("/home");
    }

    public void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/login");
    }
}