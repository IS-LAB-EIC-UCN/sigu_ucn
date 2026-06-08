package cl.ucn.app.service.Interfaces;

import java.time.LocalDate;
import java.time.LocalTime;

import cl.ucn.app.model.Entrada;
import cl.ucn.app.model.Prestamo;
import cl.ucn.app.model.Proveedor;
import cl.ucn.app.model.Recurso;
import cl.ucn.app.model.Salida;
import cl.ucn.app.model.Usuario;

public interface IMovimientoFactory {
    public Entrada crearEntrada(Recurso recurso, int cantidad, LocalDate fecha, LocalTime hora, Proveedor proveedor);
    public Salida crearSalida(Recurso recurso, int cantidad, LocalDate fecha, LocalTime hora);
    public Prestamo crearPrestamo(Recurso recurso, int cantidad, LocalDate fecha, LocalTime hora, Usuario usuario, String estado);
}
