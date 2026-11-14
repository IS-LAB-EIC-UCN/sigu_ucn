package cl.ucn.app.controller.talleres;

import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Utilidades comunes de los controladores del módulo: sesión, mensajes flash y modelo base. */
abstract class ControladorBase {

    private static final String FLASH_OK = "talleresFlashOk";
    private static final String FLASH_ERROR = "talleresFlashError";

    /** Devuelve el usuario en sesión o redirige al login. */
    protected Optional<SesionUsuario> requerirSesion(Context ctx) {
        Optional<SesionUsuario> sesion = SesionUsuario.desde(ctx);
        if (sesion.isEmpty()) {
            ctx.redirect("/login");
        }
        return sesion;
    }

    /** Devuelve el usuario si es administrador; si no, redirige con un mensaje de error. */
    protected Optional<SesionUsuario> requerirAdmin(Context ctx) {
        Optional<SesionUsuario> sesion = requerirSesion(ctx);
        if (sesion.isPresent() && !sesion.get().esAdmin()) {
            flashError(ctx, "Solo el administrador puede realizar esta acción.");
            ctx.redirect("/talleres");
            return Optional.empty();
        }
        return sesion;
    }

    protected void flashOk(Context ctx, String mensaje) {
        ctx.sessionAttribute(FLASH_OK, mensaje);
    }

    protected void flashError(Context ctx, String mensaje) {
        ctx.sessionAttribute(FLASH_ERROR, mensaje);
    }

    /** Modelo con lo que necesitan todas las vistas: título, layout y mensajes flash (se consumen). */
    protected Map<String, Object> modeloBase(Context ctx, SesionUsuario sesion, String titulo) {
        Map<String, Object> model = new HashMap<>();
        model.put("title", titulo + " - SIGU-UCN");
        model.put("usuarioNombre", sesion.nombre());
        model.put("esAdmin", sesion.esAdmin());
        model.put("flashOk", consumir(ctx, FLASH_OK));
        model.put("flashError", consumir(ctx, FLASH_ERROR));
        return model;
    }

    protected Long idDesdeRuta(Context ctx, String nombre) {
        try {
            return Long.parseLong(ctx.pathParam(nombre));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String consumir(Context ctx, String clave) {
        String valor = ctx.sessionAttribute(clave);
        if (valor != null) {
            ctx.sessionAttribute(clave, null);
        }
        return valor == null ? "" : valor;
    }
}
