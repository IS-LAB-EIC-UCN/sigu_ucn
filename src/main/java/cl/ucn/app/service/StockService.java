package cl.ucn.app.service;

import cl.ucn.app.model.*;
import cl.ucn.app.repository.*;
import cl.ucn.app.service.Interfaces.IObserver;

import java.time.LocalDate;
import java.time.LocalTime;

public class StockService implements IObserver {

    private final ProveedorRepository proveedorRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final EntradaRepository entradaRepository;
    private final SalidaRepository salidaRepository;
    private final RecursoRepository recursoRepository;

    private static MovimientoFactoryService movimientoFactoryService;
    private final ConsoleService console;

    private static int stock_minimo = 1;

    public StockService() {
        this.proveedorRepository = new ProveedorRepository();
        this.movimientoInventarioRepository = new MovimientoInventarioRepository();
        this.entradaRepository = new EntradaRepository();
        this.salidaRepository = new SalidaRepository();
        this.recursoRepository = new RecursoRepository();

        movimientoFactoryService = new MovimientoFactoryService();
        this.console = new ConsoleService();
    }

    public StockService(ProveedorRepository proveedorRepository, MovimientoInventarioRepository movimientoInventarioRepository,
                        EntradaRepository entradaRepository, SalidaRepository salidaRepository,
                        RecursoRepository recursoRepository) {

        this.proveedorRepository = proveedorRepository;
        this.movimientoInventarioRepository = movimientoInventarioRepository;
        this.entradaRepository = entradaRepository;
        this.salidaRepository = salidaRepository;
        this.recursoRepository = recursoRepository;

        movimientoFactoryService = new MovimientoFactoryService();
        this.console = new ConsoleService();
    }

    public StockService(ProveedorRepository proveedorRepository, MovimientoInventarioRepository movimientoInventarioRepository,
                        EntradaRepository entradaRepository, SalidaRepository salidaRepository,
                        RecursoRepository recursoRepository, MovimientoFactoryService factoryService) {

        this.proveedorRepository = proveedorRepository;
        this.movimientoInventarioRepository = movimientoInventarioRepository;
        this.entradaRepository = entradaRepository;
        this.salidaRepository = salidaRepository;
        this.recursoRepository = recursoRepository;

        movimientoFactoryService = factoryService;
        this.console = new ConsoleService();
    }

    public static int getStock_minimo() {
        return stock_minimo;
    }

    public static void setStock_minimo(int stock_minimo) {
        StockService.stock_minimo = stock_minimo;
    }

    public boolean crearEntrada(Long recurso_id, int cantidad, LocalDate fecha,
                                LocalTime hora, Long proveedor_id) {

        Proveedor proveedor = proveedorRepository.findById(proveedor_id);

        if(proveedor == null) {
            console.log("Error, no se ha encontrado al proveedor, reintentar.");
            return false;
        }

        Recurso recurso = recursoRepository.findById(recurso_id);

        if(recurso == null) {
            console.log("Error, no se ha encontrado al recurso, reintentar.");
            return false;
        }
        recurso.setStock(recurso.getStock() + cantidad);

        MovimientoInventario entrada = null;

        entrada = movimientoFactoryService.crearEntrada(recurso, cantidad, fecha, hora, proveedor);

        movimientoInventarioRepository.save(entrada);
        entradaRepository.save((Entrada) entrada);
        return true;
    }

    public boolean crearSalida(Long recurso_id, int cantidad, LocalDate fecha,
                               LocalTime hora) {

        Recurso recurso = recursoRepository.findById(recurso_id);

        if(recurso == null) {
            console.log("Error, no se ha encontrado al recurso, reintentar.");
            return false;
        }

        if(recurso.getStock() - cantidad < 0) {
            console.log("Error, no queda stock suficiente, reintentar.");
            return false;
        }
        recurso.setStock(recurso.getStock() - cantidad);

        Salida salida = movimientoFactoryService.crearSalida(recurso, cantidad, fecha, hora);

        movimientoInventarioRepository.save(salida);
        salidaRepository.save(salida);

        //Añade a este servicio a la lista de observadores de la salida.
        salida.addObserver(this);

        //Llama a la alerta para todos los observadores.
        if(recurso.getStock() < stock_minimo) {
            salida.notifyObservers();
            console.log("notifyObservers ha sido activado (desde StockService)");
        }

        return true;
    }

    @Override
    public void actualizarStock(Salida salida) {
        console.log_alerta(salida);
    }
}
