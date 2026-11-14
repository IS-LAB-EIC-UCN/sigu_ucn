package cl.ucn.app.exception;

/**
 * Regla de negocio: el stock no puede quedar negativ.
 */

public class StockInsuficienteException extends ReglaNegocioException {

    public StockInsuficienteException(String codigo, int disponible, int solicitado) {
        super("Stock insuficiente para " + codigo + ": disponible"+ disponible
                + ", solicitado" + solicitado + ".");
    }
}