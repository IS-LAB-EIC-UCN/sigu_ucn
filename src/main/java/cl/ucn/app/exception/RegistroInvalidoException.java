package cl.ucn.app.exception;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Errores de validación del registro, agrupados por campo del formulario
 * para poder mostrarlos junto a cada campo (RNF-06).
 */
public class RegistroInvalidoException extends RuntimeException {

    private final Map<String, String> errores;

    public RegistroInvalidoException(Map<String, String> errores) {
        super("Datos de registro inválidos: " + errores);
        this.errores = Collections.unmodifiableMap(new LinkedHashMap<>(errores));
    }

    public Map<String, String> getErrores() {
        return errores;
    }
}
