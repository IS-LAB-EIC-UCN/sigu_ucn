package cl.ucn.app.model;

public enum TipoLector {
    ESTUDIANTE(7, 3),
    DOCENTE(14, 5),
    FUNCIONARIO(10, 3);

    private final int diasPlazo;
    private final int maxPrestamos;

    TipoLector(int diasPlazo, int maxPrestamos) {
        this.diasPlazo = diasPlazo;
        this.maxPrestamos = maxPrestamos;
    }

    public int getDiasPlazo() {
        return diasPlazo;
    }

    public int getMaxPrestamos() {
        return maxPrestamos;
    }
}
