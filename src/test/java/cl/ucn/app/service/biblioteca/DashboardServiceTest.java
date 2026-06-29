package cl.ucn.app.service.biblioteca;

import cl.ucn.app.model.biblioteca.Ejemplar;
import cl.ucn.app.model.biblioteca.EstadoEjemplar;
import cl.ucn.app.model.biblioteca.EstadoPrestamo;
import cl.ucn.app.model.biblioteca.Lector;
import cl.ucn.app.model.biblioteca.Libro;
import cl.ucn.app.model.biblioteca.Multa;
import cl.ucn.app.model.biblioteca.PrestamoLibro;
import cl.ucn.app.repository.biblioteca.EjemplarRepository;
import cl.ucn.app.repository.biblioteca.LibroRepository;
import cl.ucn.app.repository.biblioteca.MultaRepository;
import cl.ucn.app.repository.biblioteca.PrestamoLibroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DashboardServiceTest {

    @Mock
    private LibroRepository libroRepository;
    @Mock
    private EjemplarRepository ejemplarRepository;
    @Mock
    private PrestamoLibroRepository prestamoRepository;
    @Mock
    private MultaRepository multaRepository;

    private DashboardService dashboardService;

    @BeforeEach
    public void setUp() {
        dashboardService = new DashboardService(libroRepository, ejemplarRepository, prestamoRepository, multaRepository);
    }

    @Test
    public void testCalcularKpis_sumaLibrosEjemplaresYPrestamos() {
        Libro libro1 = new Libro();
        libro1.setId(1L);
        Libro libro2 = new Libro();
        libro2.setId(2L);

        Ejemplar ej1 = new Ejemplar();
        Ejemplar ej2 = new Ejemplar();
        Ejemplar ej3 = new Ejemplar();
        Ejemplar ej4 = new Ejemplar();

        PrestamoLibro prestamoActivo = new PrestamoLibro();
        prestamoActivo.setEstado(EstadoPrestamo.ACTIVO);
        prestamoActivo.setFechaVencimiento(LocalDate.now().plusDays(3));

        PrestamoLibro prestamoAtrasado = new PrestamoLibro();
        prestamoAtrasado.setEstado(EstadoPrestamo.ACTIVO);
        prestamoAtrasado.setFechaVencimiento(LocalDate.now().minusDays(2));

        Multa multaPendiente = new Multa();
        multaPendiente.setMonto(new BigDecimal("3000"));
        multaPendiente.setPagada(false);
        Multa multaPagada = new Multa();
        multaPagada.setMonto(new BigDecimal("5000"));
        multaPagada.setPagada(true);

        Mockito.when(libroRepository.findAll()).thenReturn(List.of(libro1, libro2));
        Mockito.when(ejemplarRepository.findByLibro(libro1)).thenReturn(List.of(ej1, ej2));
        Mockito.when(ejemplarRepository.findByLibro(libro2)).thenReturn(List.of(ej3, ej4));
        Mockito.when(prestamoRepository.findAll()).thenReturn(List.of(prestamoActivo, prestamoAtrasado));
        Mockito.when(multaRepository.findAll()).thenReturn(List.of(multaPendiente, multaPagada));

        DashboardService.Kpis kpis = dashboardService.calcularKpis();

        assertEquals(2, kpis.totalLibros());
        assertEquals(4, kpis.totalEjemplares());
        assertEquals(2, kpis.prestamosActivos());
        assertEquals(1, kpis.prestamosAtrasados());
        assertEquals(0, new BigDecimal("3000").compareTo(kpis.totalMultas()));
    }

    @Test
    public void testMultasPendientesDelLector_lectorNull_retornaCero() {
        assertEquals(0, BigDecimal.ZERO.compareTo(dashboardService.multasPendientesDelLector(null)));
    }

    @Test
    public void testMultasPendientesDelLector_sumaSoloPendientes() {
        Lector lector = new Lector();
        Multa m1 = new Multa();
        m1.setMonto(new BigDecimal("2000"));
        m1.setPagada(false);
        Multa m2 = new Multa();
        m2.setMonto(new BigDecimal("5000"));
        m2.setPagada(false);
        Mockito.when(multaRepository.findPendientesByLector(lector)).thenReturn(List.of(m1, m2));

        BigDecimal total = dashboardService.multasPendientesDelLector(lector);

        assertEquals(0, new BigDecimal("7000").compareTo(total));
    }
}
