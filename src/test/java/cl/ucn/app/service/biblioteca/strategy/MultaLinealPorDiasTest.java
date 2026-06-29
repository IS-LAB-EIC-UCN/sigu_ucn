package cl.ucn.app.service.biblioteca.strategy;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class MultaLinealPorDiasTest {

    @Test
    public void testCalcular_ceroDias_retornaCero() {
        assertEquals(0, BigDecimal.ZERO.compareTo(new MultaLinealPorDias().calcular(0)));
    }

    @Test
    public void testCalcular_cincoDias_retorna5000() {
        assertEquals(0, new BigDecimal("5000.00").compareTo(new MultaLinealPorDias().calcular(5)));
    }
}
