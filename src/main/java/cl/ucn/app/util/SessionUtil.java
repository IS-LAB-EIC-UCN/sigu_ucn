package cl.ucn.app.util;

import io.javalin.http.Context;

public final class SessionUtil {

    public static final String USUARIO_ID = "usuarioId";
    public static final String USUARIO_NOMBRE = "usuarioNombre";
    public static final String USUARIO_CORREO = "usuarioCorreo";
    public static final String USUARIO_ROL = "usuarioRol";
    public static final String ES_TUTOR = "esTutor";
    public static final String TUTOR_ID = "tutorId";

    private SessionUtil() {
    }

    public static Long usuarioId(Context ctx) {
        return ctx.sessionAttribute(USUARIO_ID);
    }

    public static String usuarioNombre(Context ctx) {
        return ctx.sessionAttribute(USUARIO_NOMBRE);
    }

    public static String usuarioRol(Context ctx) {
        return ctx.sessionAttribute(USUARIO_ROL);
    }

    public static boolean esAdmin(Context ctx) {
        return "ADMIN".equals(usuarioRol(ctx));
    }

    public static boolean esTutor(Context ctx) {
        return Boolean.TRUE.equals(ctx.sessionAttribute(ES_TUTOR));
    }

    public static Long tutorId(Context ctx) {
        return ctx.sessionAttribute(TUTOR_ID);
    }
}
