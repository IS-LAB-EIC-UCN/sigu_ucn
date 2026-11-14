package cl.ucn.app.dto.talleres;

/**
 * Resultado de anular una inscripción. Si se liberó un cupo y había lista de espera,
 * promovidoNombre contiene el nombre del participante que pasó a inscrito; si no, es null.
 */
public record ResultadoAnulacion(String promovidoNombre) {

    public boolean huboPromocion() {
        return promovidoNombre != null;
    }
}
