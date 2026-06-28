package cl.ucn.app.auth;

import cl.ucn.app.model.biblioteca.Lector;
import cl.ucn.app.repository.biblioteca.LectorRepository;
import io.javalin.http.Context;

public class LectorSessionHelper {

    private static final String ATRIBUTO_LECTOR_ID = "bibliotecaLectorId";

    private LectorSessionHelper() {
    }

    public static Lector obtenerExistente(Context ctx) {
        Long cacheado = ctx.sessionAttribute(ATRIBUTO_LECTOR_ID);
        if (cacheado != null) {
            Lector lector = new LectorRepository().findById(cacheado);
            if (lector != null) {
                return lector;
            }
        }
        String correo = ctx.sessionAttribute("usuarioCorreo");
        if (correo == null) {
            return null;
        }
        Lector existente = new LectorRepository().findByCorreo(correo);
        if (existente != null) {
            ctx.sessionAttribute(ATRIBUTO_LECTOR_ID, existente.getId());
            return existente;
        }
        return null;
    }

    public static void guardarEnSesion(Context ctx, Lector lector) {
        ctx.sessionAttribute(ATRIBUTO_LECTOR_ID, lector.getId());
    }

    public static void limpiarSesion(Context ctx) {
        ctx.sessionAttribute(ATRIBUTO_LECTOR_ID, null);
    }
}
