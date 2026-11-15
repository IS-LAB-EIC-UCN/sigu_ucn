package cl.ucn.app.security;

/**
 * Reglas de autorización para operaciones administrativas en el backend (RN-02 / RNF-02).
 * La verificación debe aplicarse en los controladores y servicios del servidor,
 * no solo ocultando botones o enlaces en la interfaz.
 */
public final class AutorizacionAdmin {

    private AutorizacionAdmin() {
    }

    /**
     * Indica si el nombre de rol corresponde exactamente al rol administrador ({@code "ADMIN"}).
     */
    public static boolean esAdmin(String rolNombre) {
        return "ADMIN".equals(rolNombre);
    }
}
