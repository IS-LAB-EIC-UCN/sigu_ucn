package cl.ucn.app.controller;

import cl.ucn.app.model.Rol;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.service.AuthService;
import io.javalin.http.Context;
import java.util.Map;

public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public void register(Context ctx) {
        Map<String, String> body = ctx.bodyAsClass(Map.class);
        Usuario usuario = authService.registrar(
            body.get("nombre"),
            body.get("email"),
            body.get("password"),
            Rol.valueOf(body.get("rol"))
        );
        ctx.status(201).json(usuario);
    }

    public void login(Context ctx) {
        Map<String, String> body = ctx.bodyAsClass(Map.class);
        authService.login(body.get("email"), body.get("password"))
            .ifPresentOrElse(
                u -> ctx.json(u),
                () -> ctx.status(401).result("Credenciales inválidas")
            );
    }
}
