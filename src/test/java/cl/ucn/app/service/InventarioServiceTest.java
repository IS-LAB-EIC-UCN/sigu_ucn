package cl.ucn.app.service;

import cl.ucn.app.dto.NuevoProductoDTO;
import cl.ucn.app.exception.RecursoNoEncontradoException;
import cl.ucn.app.exception.ReglaNegocioException;
import cl.ucn.app.model.Categoria;
import cl.ucn.app.model.Equipo;
import cl.ucn.app.model.EstadoEquipo;
import cl.ucn.app.model.Insumo;
import cl.ucn.app.model.Producto;
import cl.ucn.app.model.Proveedor;
import cl.ucn.app.repository.CategoriaRepository;
import cl.ucn.app.repository.ProductoRepository;
import cl.ucn.app.repository.ProveedorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del CU01 - Registrar producto (Grupo 5).
 * Los repositorios se simulan con Mockito: no se necesita base de datos.
 */
@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private ProveedorRepository proveedorRepository;

    private InventarioService service;
    private Categoria microscopia;

    @BeforeEach
    void setUp() {
        service = new InventarioService(productoRepository, categoriaRepository, proveedorRepository);
        microscopia = new Categoria("Microscopía");
    }

    private void categoriaExiste() {
        when(categoriaRepository.buscarPorId(1L)).thenReturn(Optional.of(microscopia));
    }

    @Test
    @DisplayName("Registra un equipo DISPONIBLE y lo guarda")
    void registraEquipo() {
        categoriaExiste();
        when(productoRepository.existeCodigo("EQ-005")).thenReturn(false);

        Producto p = service.registrarProducto(
                new NuevoProductoDTO("EQUIPO", " eq-005 ", "Balanza analítica", null, 1L, null, null));

        Equipo equipo = assertInstanceOf(Equipo.class, p);
        assertEquals("EQ-005", equipo.getCodigo(), "el código se normaliza a mayúsculas y sin espacios");
        assertEquals(EstadoEquipo.DISPONIBLE, equipo.getEstado());
        assertSame(microscopia, equipo.getCategoria());
        verify(productoRepository).guardar(equipo);
    }

    @Test
    @DisplayName("Registra un insumo con stock 0 y el stock mínimo indicado")
    void registraInsumo() {
        categoriaExiste();
        Proveedor labSupply = new Proveedor();
        when(proveedorRepository.buscarPorId(1L)).thenReturn(Optional.of(labSupply));
        when(productoRepository.existeCodigo("INS-010")).thenReturn(false);

        service.registrarProducto(
                new NuevoProductoDTO("INSUMO", "INS-010", "Pipeta Pasteur", "Plástico", 1L, 1L, 10));

        ArgumentCaptor<Producto> guardado = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository).guardar(guardado.capture());
        Insumo insumo = assertInstanceOf(Insumo.class, guardado.getValue());
        assertEquals(0, insumo.getStockActual());
        assertEquals(10, insumo.getStockMinimo());
        assertFalse(insumo.getStockInicializado());
        assertSame(labSupply, insumo.getProveedor());
    }

    @Test
    @DisplayName("A2: rechaza un código que ya existe")
    void rechazaCodigoDuplicado() {
        categoriaExiste();
        when(productoRepository.existeCodigo("EQ-001")).thenReturn(true);

        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class, () -> service.registrarProducto(
                new NuevoProductoDTO("EQUIPO", "EQ-001", "Microscopio", null, 1L, null, null)));

        assertTrue(ex.getMessage().contains("EQ-001"));
        verify(productoRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("Rechaza campos obligatorios vacíos")
    void rechazaCamposVacios() {
        assertThrows(ReglaNegocioException.class, () -> service.registrarProducto(
                new NuevoProductoDTO("EQUIPO", "  ", "Microscopio", null, 1L, null, null)));
        assertThrows(ReglaNegocioException.class, () -> service.registrarProducto(
                new NuevoProductoDTO("EQUIPO", "EQ-009", "", null, 1L, null, null)));
        assertThrows(ReglaNegocioException.class, () -> service.registrarProducto(
                new NuevoProductoDTO("EQUIPO", "EQ-009", "Microscopio", null, null, null, null)));
        verify(productoRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("Rechaza un código con formato inválido")
    void rechazaCodigoInvalido() {
        assertThrows(ReglaNegocioException.class, () -> service.registrarProducto(
                new NuevoProductoDTO("EQUIPO", "E", "Microscopio", null, 1L, null, null)));
        assertThrows(ReglaNegocioException.class, () -> service.registrarProducto(
                new NuevoProductoDTO("EQUIPO", "EQ 0#1", "Microscopio", null, 1L, null, null)));
        verify(productoRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("A1: rechaza un insumo sin stock mínimo")
    void rechazaInsumoSinStockMinimo() {
        categoriaExiste();
        when(productoRepository.existeCodigo("INS-011")).thenReturn(false);

        assertThrows(ReglaNegocioException.class, () -> service.registrarProducto(
                new NuevoProductoDTO("INSUMO", "INS-011", "Guantes", null, 1L, null, null)));
        verify(productoRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("A1: rechaza un stock mínimo negativo")
    void rechazaStockMinimoNegativo() {
        categoriaExiste();
        when(productoRepository.existeCodigo("INS-011")).thenReturn(false);

        assertThrows(ReglaNegocioException.class, () -> service.registrarProducto(
                new NuevoProductoDTO("INSUMO", "INS-011", "Guantes", null, 1L, null, -3)));
        verify(productoRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("Rechaza una categoría que no existe")
    void rechazaCategoriaInexistente() {
        when(categoriaRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.registrarProducto(
                new NuevoProductoDTO("EQUIPO", "EQ-009", "Microscopio", null, 99L, null, null)));
        verify(productoRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("Rechaza un tipo de recurso distinto de EQUIPO o INSUMO")
    void rechazaTipoInvalido() {
        categoriaExiste();
        when(productoRepository.existeCodigo("EQ-009")).thenReturn(false);

        assertThrows(ReglaNegocioException.class, () -> service.registrarProducto(
                new NuevoProductoDTO("MUEBLE", "EQ-009", "Silla", null, 1L, null, null)));
        verify(productoRepository, never()).guardar(any());
    }
}