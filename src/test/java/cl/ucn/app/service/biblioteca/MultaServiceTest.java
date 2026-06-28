package cl.ucn.app.service.biblioteca;

import cl.ucn.app.model.biblioteca.Multa;
import cl.ucn.app.model.biblioteca.PrestamoLibro;
import cl.ucn.app.repository.biblioteca.LectorRepository;
import cl.ucn.app.repository.biblioteca.MultaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MultaServiceTest {

    @Mock
    private MultaRepository multaRepository;
    @Mock
    private LectorRepository lectorRepository;

    private MultaService multaService;

    @BeforeEach
    public void setUp() {
        multaService = new MultaService(multaRepository, lectorRepository);
    }

    @Test
    public void testGenerarMulta_cincoDiasAtraso_monto5000() {
        PrestamoLibro prestamo = new PrestamoLibro();
        prestamo.setId(1L);
        prestamo.setFechaVencimiento(LocalDate.now().minusDays(5));

        Multa multa = multaService.generarMulta(prestamo, 5);

        assertNotNull(multa);
        assertEquals(5, multa.getDiasAtraso());
        assertEquals(0, new BigDecimal("5000.00").compareTo(multa.getMonto()));
    }

    @Test
    public void testGenerarMulta_diasCero_retornaNull() {
        PrestamoLibro prestamo = new PrestamoLibro();
        assertNull(multaService.generarMulta(prestamo, 0));
    }

    @Test
    public void testGenerarMulta_diasNegativos_retornaNull() {
        PrestamoLibro prestamo = new PrestamoLibro();
        assertNull(multaService.generarMulta(prestamo, -3));
    }
}
