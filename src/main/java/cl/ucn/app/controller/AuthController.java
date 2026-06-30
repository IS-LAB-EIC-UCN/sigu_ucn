package cl.ucn.app.controller;

import cl.ucn.app.model.Usuario;
import cl.ucn.app.service.AuthService;
import io.javalin.http.Context;
import java.util.Map;

public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public void showLogin(Context ctx) {
        ctx.render("login.jte", Map.of("error", ""));
    }

    public void doLogin(Context ctx) {
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

        ctx.sessionAttribute("usuarioId", usuario.getId());
        ctx.sessionAttribute("usuarioNombre", usuario.getNombre());
        ctx.sessionAttribute("usuarioCorreo", usuario.getCorreo());
        ctx.sessionAttribute("usuarioRol", usuario.getRol().getNombre());

        ctx.redirect("/home");
    }

    public void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/login");
    }

    // REST API Endpoint
    public void register(Context ctx) {
        Map<String, String> body = ctx.bodyAsClass(Map.class);
        try {
            Usuario usuario = authService.registrar(
                body.get("nombre"),
                body.get("email"),
                body.get("password"),
                body.get("rol")
            );
            ctx.status(201).json(usuario);
        } catch (Exception e) {
            ctx.status(400).result(e.getMessage());
        }
    }

    // REST API Endpoint
    public void login(Context ctx) {
        Map<String, String> body = ctx.bodyAsClass(Map.class);
        authService.login(body.get("email"), body.get("password"))
            .ifPresentOrElse(
                u -> ctx.json(u),
                () -> ctx.status(401).result("Credenciales inválidas")
            );
    }
}