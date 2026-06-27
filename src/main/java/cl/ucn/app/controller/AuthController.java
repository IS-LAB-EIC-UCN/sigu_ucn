package cl.ucn.app.controller;

import cl.ucn.app.service.AuthService;
import cl.ucn.app.model.Usuario;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import java.util.Map;

public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public void renderLogin(Context ctx) {
        ctx.render("jte/login.jte");
    }

    public void handleLogin(Context ctx) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.render("jte/login.jte", Map.of("error", "Todos los campos son obligatorios."));
            return;
        }

        if (!email.endsWith("@alumnos.ucn.cl") && !email.endsWith("@ucn.cl")) {
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.render("jte/login.jte", Map.of("error", "Debe utilizar un correo institucional de la UCN."));
            return;
        }

        try {
            Usuario usuario = authService.authenticate(email, password);
            if (usuario != null) {
                ctx.sessionAttribute("currentUser", usuario);
                ctx.redirect("/home");
            } else {
                ctx.status(HttpStatus.UNAUTHORIZED);
                ctx.render("jte/login.jte", Map.of("error", "Credenciales inválidas. Intente nuevamente."));
            }
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
            ctx.render("jte/login.jte", Map.of("error", "Ocurrió un error interno. Intente más tarde."));
        }
    }

    public void handleLogout(Context ctx) {
        ctx.consumeSessionAttribute("currentUser");
        ctx.redirect("/login");
    }
}
