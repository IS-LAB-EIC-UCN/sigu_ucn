package cl.ucn.app.service.biblioteca.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MultaLinealPorDias implements ICalculadorMulta {
    private static final BigDecimal MONTO_POR_DIA = new BigDecimal("1000");

    @Override
    public BigDecimal calcular(int diasAtraso) {
        return MONTO_POR_DIA.multiply(BigDecimal.valueOf(diasAtraso))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
