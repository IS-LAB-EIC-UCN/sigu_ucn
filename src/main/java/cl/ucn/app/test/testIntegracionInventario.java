package cl.ucn.app.test;

import cl.ucn.app.model.Prestamo;
import cl.ucn.app.model.Recurso;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.*;
import cl.ucn.app.service.InventarioService;
import cl.ucn.app.service.PrestamoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class testIntegracionInventario {
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
    private InventarioRepository inventarioRepository;

    private PrestamoService prestamoService;

    private InventarioService inventarioService;

    @Mock
    private Recurso recurso_mock;
    @Mock
    private Prestamo prestamo_mock;
    @Mock
    private Usuario usuario_mock;

    @BeforeEach
    public void setUp() {
        prestamoService = new PrestamoService(
                prestamoRepository, recursoRepository, proveedorRepository,
                usuarioRepository, movimientoInventarioRepository
        );
        inventarioService = new InventarioService(inventarioRepository);
    }

    @AfterEach
    public void refresh() {
        recurso_mock = null;
        usuario_mock = null;
        prestamo_mock = null;
    }

    @Test
    public void testPedir_y_Aceptar_Prestamo() {
        Recurso recurso_mock = new Recurso();
        Usuario usuario_mock = new Usuario();
        recurso_mock.setStock(5);

        Mockito.when(recursoRepository.findById(Mockito.anyLong())).thenReturn(recurso_mock);
        Mockito.when(usuarioRepository.findById(Mockito.anyLong())).thenReturn(usuario_mock);
        Mockito.when(prestamoRepository.findById(Mockito.anyLong())).thenReturn(prestamo_mock);

        prestamoService.pedirPrestamo(1L, 2, 1L);

        Mockito.verify(prestamoRepository).save(Mockito.any());
        Mockito.verify(movimientoInventarioRepository).save(Mockito.any());

        prestamoService.editarPrestamo(1L, "PRESTAMO ACEPTADO");

        Mockito.verify(prestamoRepository).alter(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    public void testPedir_y_Confirmar_Devolucion() {
        Mockito.when(recurso_mock.getId()).thenReturn(1L);
        Mockito.when(usuario_mock.getId()).thenReturn(1L);
        Mockito.when(recurso_mock.getStock()).thenReturn(5);
        Mockito.when(prestamo_mock.getUsuario()).thenReturn(usuario_mock);
        Mockito.when(prestamo_mock.getRecurso()).thenReturn(recurso_mock);

        Mockito.when(recursoRepository.findById(Mockito.anyLong())).thenReturn(recurso_mock);
        Mockito.when(usuarioRepository.findById(Mockito.anyLong())).thenReturn(usuario_mock);
        Mockito.when(prestamoRepository.findById(Mockito.anyLong())).thenReturn(prestamo_mock);

        prestamoService.pedirPrestamo(1L, 2, 1L);
        Mockito.verify(prestamoRepository).save(Mockito.any());
        Mockito.verify(movimientoInventarioRepository).save(Mockito.any());

        prestamoService.editarPrestamo(1L, "PRESTAMO ACEPTADO");
        prestamoService.devolverEquipo(1L, 1L, 1L);
        prestamoService.editarPrestamo(1L, "DEVOLUCION CONFIRMADA");

        Mockito.verify(prestamoRepository, Mockito.atLeastOnce()).alter(Mockito.anyLong(), Mockito.anyString());

    }

    @Test
    public void testConsultaInventario() {
        Mockito.when(inventarioRepository.findAll()
        ).thenReturn(Collections.singletonList(recurso_mock));

        assertEquals(recurso_mock,
                inventarioService.obtenerInventarioCompleto().get(0));
    }

    @Test
    public void testConsultaCategoria() {
        Mockito.when(inventarioRepository.findByCategoria(Mockito.anyString())
        ).thenReturn(Collections.singletonList(recurso_mock));

        assertEquals(recurso_mock,
                inventarioService.filtrarRecursosPorCategoria("Categoria").get(0));
    }

}
