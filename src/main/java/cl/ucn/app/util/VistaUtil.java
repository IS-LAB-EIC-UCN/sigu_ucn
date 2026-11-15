package cl.ucn.app.util;

import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Construye el modelo base que necesita {@code layout/main.jte} en todas las vistas:
 * título, nombre del usuario, rol y si tiene perfil de tutor (para la navegación).
 */
public final class VistaUtil {

    private VistaUtil() {
    }

    public static Map<String, Object> modeloBase(Context ctx, String title) {
        Map<String, Object> model = new HashMap<>();
        model.put("title", title);
        model.put("usuarioNombre", SessionUtil.usuarioNombre(ctx));
        model.put("usuarioRol", SessionUtil.usuarioRol(ctx));
        model.put("esTutor", SessionUtil.esTutor(ctx));
        return model;
    }
}
