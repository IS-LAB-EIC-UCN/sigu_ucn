package cl.ucn.app.util;

import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public final class AuthMiddleware {

    private AuthMiddleware() {
    }

    public static void requireLogin(Context ctx) {
        if (SessionUtil.usuarioId(ctx) == null) {
            ctx.redirect("/login");
            ctx.skipRemainingHandlers();
        }
    }

    public static void requireAdmin(Context ctx) {
        if (SessionUtil.usuarioId(ctx) == null) {
            ctx.redirect("/login");
            ctx.skipRemainingHandlers();
            return;
        }
        if (!SessionUtil.esAdmin(ctx)) {
            forbidden(ctx, "Solo el administrador puede acceder a esta sección.");
        }
    }

    public static void requireTutor(Context ctx) {
        if (SessionUtil.usuarioId(ctx) == null) {
            ctx.redirect("/login");
            ctx.skipRemainingHandlers();
            return;
        }
        if (!SessionUtil.esTutor(ctx)) {
            forbidden(ctx, "Solo los tutores habilitados pueden acceder a esta sección.");
        }
    }

    private static void forbidden(Context ctx, String mensaje) {
        Map<String, Object> model = new HashMap<>();
        model.put("title", "Acceso restringido - SIGU-UCN");
        model.put("usuarioNombre", SessionUtil.usuarioNombre(ctx));
        model.put("mensaje", mensaje);
        ctx.status(403).render("tutorias/error.jte", model);
        ctx.skipRemainingHandlers();
    }
}
