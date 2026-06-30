package cl.ucn.app.service;

import cl.ucn.app.model.*;
import cl.ucn.app.repository.*;

import java.time.LocalDate;
import java.time.LocalTime;

public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final RecursoRepository recursoRepository;
    private final ProveedorRepository proveedorRepository;
    private final UsuarioRepository usuarioRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;

    private static MovimientoFactoryService movimientoFactoryService;
    private final ConsoleService console;

    public PrestamoService() {
        this.prestamoRepository = new PrestamoRepository();
        this.recursoRepository = new RecursoRepository();
        this.proveedorRepository = new ProveedorRepository();
        this.usuarioRepository = new UsuarioRepository();
        this.movimientoInventarioRepository = new MovimientoInventarioRepository();

        movimientoFactoryService = new MovimientoFactoryService();
        this.console = new ConsoleService();
    }

    public PrestamoService(PrestamoRepository prestamoRepository, RecursoRepository recursoRepository,
                           ProveedorRepository proveedorRepository, UsuarioRepository usuarioRepository,
                           MovimientoInventarioRepository movimientoInventarioRepository) {
        this.prestamoRepository = prestamoRepository;
        this.recursoRepository = recursoRepository;
        this.proveedorRepository = proveedorRepository;
        this.usuarioRepository = usuarioRepository;
        this.movimientoInventarioRepository = movimientoInventarioRepository;

        movimientoFactoryService = new MovimientoFactoryService();
        this.console = new ConsoleService();
    }

    public boolean pedirPrestamo(Long equipo_id, int cantidad, Long usuario_id) {

        if(recursoRepository == null || usuarioRepository == null) {
            console.log("Error interno, no se inicializó correctamente el servicio PrestamoService para manejar la operación pedirPrestamo(). Utilizar el segundo constructor.");
            return false;
        }

        Recurso recurso = recursoRepository.findById(equipo_id);

        if(recurso == null || usuarioRepository.findById(usuario_id) == null) {
            console.log("Error, los datos ingresados son incorrectos, reintentar.");
            return false;
        }

        return crearPrestamo("PRESTAMO", equipo_id, cantidad, LocalDate.now(),
                LocalTime.now(), usuario_id, "PRESTAMO PENDIENTE");
    }

    public boolean devolverEquipo(Long equipo_id, Long movimiento_id, Long usuario_id) {

        if(equipo_id < 1 || movimiento_id < 1) {
            console.log("Error, datos inválidos, reintentar");
            return false;
        }

        Prestamo prestamo = prestamoRepository.findById(movimiento_id);

        if(prestamo == null) {
            console.log("Error, el equipo no se encuentra prestado a su usuario actual, reintentar.");
            return false;
        }

        if(!prestamo.getUsuario().getId().equals(usuario_id)
                || !prestamo.getRecurso().getId().equals(equipo_id)) {
            console.log("Error, el equipo no se encuentra prestado a su usuario actual, reintentar.");
            return false;
        }

        prestamoRepository.alter(movimiento_id, "DEVOLUCION PENDIENTE");
        return true;
    }

    public boolean crearPrestamo(String tipo, Long id_recurso, int cantidad, LocalDate fecha,
                                 LocalTime hora, Long id_usuario, String estado) {
        if (id_recurso < 1 || cantidad < 1 || fecha == null || hora == null) {
            console.log("Error, los datos ingresados son incorrectos, reintentar");
            return false;
        }

        if(movimientoFactoryService == null || recursoRepository == null
        || proveedorRepository == null || usuarioRepository == null
        || movimientoInventarioRepository == null) {
            console.log("Error interno, no se inicializó correctamente el servicio PrestamoService para manejar la operación crearPretamo(). Utilizar el segundo constructor.");
            return false;
        }

        Recurso recurso = recursoRepository.findById(id_recurso);

        if (recurso == null) {
            console.log("Error, no se ha encontrado el recurso, reintentar");
            return false;
        }

        if (recurso.getStock() - cantidad < 0 && tipo.equalsIgnoreCase("PRESTAMO")) {
            console.log("No queda suficiente stock, reintentar");
            return false;
        }
        if (estado.equalsIgnoreCase("PRESTAMO ACEPTADO")) {
            recurso.setStock(recurso.getStock() - cantidad);
        } else if (estado.equalsIgnoreCase("DEVOLUCION CONFIRMADA")){
            recurso.setStock(recurso.getStock() + cantidad);
        }

        Usuario usuario = usuarioRepository.findById(id_usuario);

        if (usuario == null) {
            console.log("Error, no se ha encontrado el usuario, reintentar");
            return false;
        }

        if(!(tipo.equalsIgnoreCase("PRESTAMO") || tipo.equalsIgnoreCase("DEVOLUCION"))) {
            console.log("Error, tipo inválido, debe ser ENTRADA, SALIDA, PRESTAMO, o DEVOLUCION");
            return false;
        }

        Prestamo prestamo = movimientoFactoryService.crearPrestamo(recurso, cantidad, fecha, hora, usuario, estado);
        movimientoInventarioRepository.save(prestamo);
        prestamoRepository.save(prestamo);
        return true;
    }

    public boolean editarPrestamo(Long movimiento_id, String estado) {

        if(movimiento_id < 1
                || !(estado.equalsIgnoreCase("PRESTAMO PENDIENTE")
                || estado.equalsIgnoreCase("PRESTAMO ACEPTADO")
                || estado.equalsIgnoreCase("PRESTAMO RECHAZADO")
                || estado.equalsIgnoreCase("DEVOLUCION PENDIENTE")
                || estado.equalsIgnoreCase("DEVOLUCION CONFIRMADA"))
        ) {
            console.log("Error, los datos ingresados son incorrectos, reintentar.");
            return false;
        }

        if(prestamoRepository == null) {
            console.log("Error interno, no se inicializó correctamente el servicio PrestamoService para manejar la operación editarPrestamo(). Utilizar el segundo constructor.");
            return false;
        }
        
        Prestamo prestamo = prestamoRepository.findById(movimiento_id);

        if(prestamo == null){
            console.log("Error, no se ha encontrado el prestamo, reintentar");
            return false;
        }

        if (estado.equalsIgnoreCase("PRESTAMO ACEPTADO")) {
            prestamo.getRecurso().setStock(prestamo.getRecurso().getStock() - prestamo.getCantidad());
        } else if (estado.equalsIgnoreCase("DEVOLUCION CONFIRMADA")){
            prestamo.getRecurso().setStock(prestamo.getRecurso().getStock() + prestamo.getCantidad());
        }

        prestamoRepository.alter(movimiento_id, estado);
        return true;
    }
}