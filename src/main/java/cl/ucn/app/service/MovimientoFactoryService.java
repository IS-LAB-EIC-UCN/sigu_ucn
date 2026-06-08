package cl.ucn.app.service;

import java.time.LocalDate;
import java.time.LocalTime;

import cl.ucn.app.model.Entrada;
import cl.ucn.app.model.Prestamo;
import cl.ucn.app.model.Proveedor;
import cl.ucn.app.model.Recurso;
import cl.ucn.app.model.Salida;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.service.Interfaces.IMovimientoFactory;

public class MovimientoFactoryService implements IMovimientoFactory {

    @Override
    public Entrada crearEntrada(Recurso recurso, int cantidad, LocalDate fecha, LocalTime hora, Proveedor proveedor) {
        if (recurso == null || proveedor == null) return null;
        return new Entrada(recurso, cantidad, fecha, hora, proveedor);
    }

    @Override
    public Salida crearSalida(Recurso recurso, int cantidad, LocalDate fecha, LocalTime hora) {
        if (recurso == null) return null;
        return new Salida(recurso, cantidad, fecha, hora);
    }

    @Override
    public Prestamo crearPrestamo(Recurso recurso, int cantidad, LocalDate fecha, LocalTime hora, Usuario usuario,
            String estado) {
        if (recurso == null || usuario == null) return null;
        return new Prestamo(recurso, cantidad, fecha, hora, usuario, estado);
    }
    
}
