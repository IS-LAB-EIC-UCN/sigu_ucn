package cl.ucn.app.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class TutoriaValidacionException extends RuntimeException {

    private final String campo;
    private final Map<String, String> errores;

    public TutoriaValidacionException(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
        this.errores = campo == null ? Map.of() : Map.of(campo, mensaje);
    }

    public TutoriaValidacionException(Map<String, String> errores) {
        super(errores.values().iterator().next());
        this.campo = errores.keySet().iterator().next();
        this.errores = Collections.unmodifiableMap(new LinkedHashMap<>(errores));
    }

    public String getCampo() {
        return campo;
    }

    public Map<String, String> getErrores() {
        return errores;
    }
}
