package cl.ucn.app.test;

import cl.ucn.app.model.*;
import cl.ucn.app.repository.*;
import cl.ucn.app.service.MovimientoFactoryService;
import cl.ucn.app.service.StockService;
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
public class testStock {

    @Mock
    private ProveedorRepository proveedorRepository;

    @Mock
    private MovimientoInventarioRepository movimientoInventarioRepository;

    @Mock
    private RecursoRepository recursoRepository;

    @Mock
    private EntradaRepository entradaRepository;

    @Mock
    private SalidaRepository salidaRepository;

    @Mock
    private MovimientoFactoryService movimientoFactoryService;

    @Mock
    private Salida salida_mock;

    private StockService stockService;

    @BeforeEach
    public void setUp() {
        stockService = new StockService(
                proveedorRepository, movimientoInventarioRepository, entradaRepository,
                salidaRepository, recursoRepository, movimientoFactoryService);
    }

    @Test
    public void testCrearEntrada() {
        Proveedor proveedor_mock = new Proveedor();
        Recurso recurso_mock = new Recurso();

        Mockito.when(proveedorRepository.findById(Mockito.anyLong())).thenReturn(proveedor_mock);
        Mockito.when(recursoRepository.findById(Mockito.anyLong())).thenReturn(recurso_mock);

        assertTrue(stockService.crearEntrada(1L,1,LocalDate.now(),LocalTime.now(),1L));
    }

    @Test
    public void testCrearSalida() {
        Recurso recurso_mock = new Recurso();

        recurso_mock.setStock(15);

        Mockito.when(recursoRepository.findById(Mockito.anyLong())).thenReturn(recurso_mock);
        Mockito.when(movimientoFactoryService.crearSalida(
                Mockito.any(), Mockito.anyInt(), Mockito.any(), Mockito.any())
        ).thenReturn(salida_mock);

        assertTrue(stockService.crearSalida(1L,10, LocalDate.now(), LocalTime.now()));
    }

    @Test
    public void testNotificarStockBajo() {
        Recurso recurso_mock = new Recurso();
        recurso_mock.setStock(15);

        Mockito.when(recursoRepository.findById(Mockito.anyLong())).thenReturn(recurso_mock);
        Mockito.when(movimientoFactoryService.crearSalida(
                Mockito.any(), Mockito.anyInt(), Mockito.any(), Mockito.any())
        ).thenReturn(salida_mock);

        assertTrue(stockService.crearSalida(1L,15, LocalDate.now(), LocalTime.now()));
        Mockito.verify(salida_mock, Mockito.atLeastOnce()).notifyObservers();
    }

}
