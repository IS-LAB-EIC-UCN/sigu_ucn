package cl.ucn.app.test;

import cl.ucn.app.model.MovimientoInventario;
import cl.ucn.app.model.Prestamo;
import cl.ucn.app.model.Recurso;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.*;
import cl.ucn.app.service.PrestamoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class testPrestamoNormal {

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
    public void setUp(){
        prestamoService = new PrestamoService(
                prestamoRepository, recursoRepository, proveedorRepository,
                usuarioRepository, movimientoInventarioRepository);
    }

    @Test
    public void testPedirPrestamo() {
        Recurso recurso_mock = new Recurso();
        Usuario usuario_mock = new Usuario();

        recurso_mock.setStock(5);

        Mockito.when(recursoRepository.findById(Mockito.anyLong())).thenReturn(recurso_mock);
        Mockito.when(usuarioRepository.findById(Mockito.anyLong())).thenReturn(usuario_mock);

        assertTrue(prestamoService.pedirPrestamo(1L, 2, 1L));
    }

    @Test
    public void testDevolverEquipo() {
        Mockito.when(prestamo_mock.getUsuario()).thenAnswer(invocation -> usuario_mock);
        Mockito.when(prestamo_mock.getRecurso()).thenAnswer(invocation -> recurso_mock);

        Mockito.when(recurso_mock.getId()).thenReturn(1L);
        Mockito.when(usuario_mock.getId()).thenReturn(1L);

        Mockito.when(prestamoRepository.findById(Mockito.anyLong())).thenReturn(prestamo_mock);

        assertAll();
        assertTrue(prestamoService.devolverEquipo(1L, 1L, 1L));
    }
}
