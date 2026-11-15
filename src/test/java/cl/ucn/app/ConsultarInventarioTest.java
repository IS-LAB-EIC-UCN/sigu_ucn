package cl.ucn.app.service;

import cl.ucn.app.model.Categoria;
import cl.ucn.app.model.Equipo;
import cl.ucn.app.model.Insumo;
import cl.ucn.app.model.Producto;
import cl.ucn.app.repository.CategoriaRepository;
import cl.ucn.app.repository.ProductoRepository;
import cl.ucn.app.repository.ProveedorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del CU05 - Consultar inventario (Grupo 5).
 */
@ExtendWith(MockitoExtension.class)
class ConsultarInventarioTest {

    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private ProveedorRepository proveedorRepository;

    private InventarioService service;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        service = new InventarioService(productoRepository, categoriaRepository, proveedorRepository);
        categoria = new Categoria("Reactivos químicos");
    }

    /** Crea un insumo con su stock inicial ya registrado. */
    private Insumo insumo(String codigo, int stockActual, int stockMinimo) {
        Insumo insumo = new Insumo(codigo, "Insumo " + codigo, null, categoria, null, stockMinimo);
        insumo.registrarStockInicial(stockActual);
        return insumo;
    }

    @Test
    @DisplayName("Consultar entrega los filtros de categoría y texto al repositorio")
    void consultarUsaLosFiltros() {
        Producto microscopio = new Equipo("EQ-001", "Microscopio", null, categoria, null);
        when(productoRepository.buscar(1L, "micro")).thenReturn(List.of(microscopio));

        List<Producto> resultado = service.consultar(1L, "micro");

        assertEquals(List.of(microscopio), resultado);
        verify(productoRepository).buscar(1L, "micro");
    }

    @Test
    @DisplayName("Sin resultados devuelve una lista vacía")
    void consultarSinResultados() {
        when(productoRepository.buscar(null, "zzz")).thenReturn(List.of());

        assertTrue(service.consultar(null, "zzz").isEmpty());
    }

    @Test
    @DisplayName("Alerta de stock bajo: solo insumos con stock actual <= mínimo")
    void alertaStockBajo() {
        Insumo normal = insumo("INS-001", 20, 5);
        Insumo bajo = insumo("INS-002", 3, 5);
        Insumo justoEnElMinimo = insumo("INS-003", 5, 5);
        when(productoRepository.listarInsumos()).thenReturn(List.of(normal, bajo, justoEnElMinimo));

        List<Insumo> alertas = service.insumosConStockBajo();

        assertEquals(List.of(bajo, justoEnElMinimo), alertas);
    }
}