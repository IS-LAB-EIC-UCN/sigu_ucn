package cl.ucn.app.test;

import cl.ucn.app.model.Prestamo;
import cl.ucn.app.model.Recurso;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.*;
import cl.ucn.app.service.PrestamoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class testPrestamoAdmin {

    @Mock
    private PrestamoRepository prestamoRepository;

    @Mock
    private RecursoRepository recursoRepository;

    @Mock
    private ProveedorRepository proveedorRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MovimientoInventarioRepository movimientoInventarioRepository;

    @Mock
    private Recurso recurso_mock;
    @Mock
    private Prestamo prestamo_mock;
    @Mock
    private Usuario usuario_mock;

    private PrestamoService prestamoService;

    @BeforeEach
    public void setUp() {
        prestamoService = new PrestamoService(
                prestamoRepository, recursoRepository, proveedorRepository,
                usuarioRepository, movimientoInventarioRepository);
    }

    @Test
    public void testCrearPrestamo() {
        Recurso recurso_mock = new Recurso();
        Usuario usuario_mock = new Usuario();

        recurso_mock.setStock(5);

        Mockito.when(recursoRepository.findById(Mockito.anyLong())).thenReturn(recurso_mock);
        Mockito.when(usuarioRepository.findById(Mockito.anyLong())).thenReturn(usuario_mock);

        assertTrue(prestamoService.crearPrestamo("PRESTAMO", 1L, 2, LocalDate.now(),
                LocalTime.now(), 1L, "PRESTAMO PENDIENTE"));
    }

    @Test
    public void testAceptarPrestamo() {
        Mockito.when(prestamo_mock.getRecurso()).thenReturn(recurso_mock);
        Mockito.when(recurso_mock.getStock()).thenReturn(5);
        Mockito.when(prestamoRepository.findById(Mockito.anyLong())).thenReturn(prestamo_mock);

        assertTrue(prestamoService.editarPrestamo(1L, "PRESTAMO ACEPTADO"));
    }

    @Test
    public void testCrearDevolucion() {
        Recurso recurso_mock = new Recurso();
        Usuario usuario_mock = new Usuario();

        recurso_mock.setStock(5);

        Mockito.when(recursoRepository.findById(Mockito.anyLong())).thenReturn(recurso_mock);
        Mockito.when(usuarioRepository.findById(Mockito.anyLong())).thenReturn(usuario_mock);

        assertTrue(prestamoService.crearPrestamo("DEVOLUCION", 1L, 2, LocalDate.now(),
                LocalTime.now(), 1L, "DEVOLUCION PENDIENTE"));
    }

    @Test
    public void testConfirmarDevolucion() {
        Mockito.when(prestamo_mock.getRecurso()).thenReturn(recurso_mock);
        Mockito.when(recurso_mock.getStock()).thenReturn(5);
        Mockito.when(prestamoRepository.findById(Mockito.anyLong())).thenReturn(prestamo_mock);

        assertTrue(prestamoService.editarPrestamo(1L, "DEVOLUCION CONFIRMADA"));
    }

}
