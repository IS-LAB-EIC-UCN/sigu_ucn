package cl.ucn.app.service.biblioteca;

import cl.ucn.app.model.biblioteca.Lector;
import cl.ucn.app.model.biblioteca.Multa;
import cl.ucn.app.model.biblioteca.PrestamoLibro;
import cl.ucn.app.repository.biblioteca.LectorRepository;
import cl.ucn.app.repository.biblioteca.MultaRepository;
import cl.ucn.app.service.biblioteca.api.ILectorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MultaServiceTest {

    @Mock
    private MultaRepository multaRepository;
    @Mock
    private LectorRepository lectorRepository;
    @Mock
    private ILectorService lectorService;
    @Mock
    private EntityManager em;
    @Mock
    private EntityTransaction tx;

    private MultaService multaService;

    @BeforeEach
    public void setUp() {
        multaService = new MultaService(multaRepository, lectorRepository, lectorService);
        Mockito.lenient().when(em.getTransaction()).thenReturn(tx);
    }

    @Test
    public void testGenerarMulta_cincoDiasAtraso_monto5000() {
        PrestamoLibro prestamo = new PrestamoLibro();

        Multa multa = multaService.generarMulta(prestamo, 5, em);

        assertNotNull(multa);
        assertEquals(5, multa.getDiasAtraso());
        assertEquals(0, new BigDecimal("5000.00").compareTo(multa.getMonto()));
    }

    @Test
    public void testGenerarMulta_bloqueaLector() {
        Lector lector = new Lector();
        lector.setBloqueado(false);
        PrestamoLibro prestamo = new PrestamoLibro();
        prestamo.setLector(lector);

        multaService.generarMulta(prestamo, 2, em);

        assertTrue(lector.isBloqueado());
    }

    @Test
    public void testRegistrarPago_desbloqueaLector_siSinDeudas() {
        Multa multa = new Multa();
        multa.setPagada(false);
        PrestamoLibro prestamo = new PrestamoLibro();
        Lector lector = new Lector();
        lector.setBloqueado(true);
        prestamo.setLector(lector);
        multa.setPrestamo(prestamo);

        Mockito.when(lectorService.tieneDeudaPendiente(lector.getId())).thenReturn(false);

        multaService.registrarPago(multa, em);

        assertTrue(multa.getPagada());
        assertFalse(lector.isBloqueado());
    }
}
