package cl.ucn.app.controller;

import io.javalin.http.Handler;

import java.util.Set;

/**
 * Seguridad básica de acceso del módulo de inventario (Grupo 5).
 * Envuelve un handler y solo lo ejecuta si hay sesión iniciada y el rol del
 * usuario está permitido. Un conjunto de roles vacío significa "cualquier
 * usuario autenticado".
 */
public final class ControlAcceso {

    private ControlAcceso() {
    }

    public static Handler requiere(Set<String> rolesPermitidos, Handler handler) {
        return ctx -> {
            Long usuarioId = ctx.sessionAttribute("usuarioId");
            if (usuarioId == null) {
                ctx.redirect("/login");
                return;
            }
            String rol = ctx.sessionAttribute("usuarioRol");
            if (!rolesPermitidos.isEmpty() && (rol == null || !rolesPermitidos.contains(rol))) {
                ctx.sessionAttribute(InventarioControllerBase.FLASH_ERROR,
                        "No tiene permisos para acceder a esa opción.");
                ctx.redirect("/inventario");
                return;
            }
            handler.handle(ctx);
        };
    }
}
