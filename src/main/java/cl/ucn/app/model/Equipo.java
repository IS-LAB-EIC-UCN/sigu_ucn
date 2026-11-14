package cl.ucn.app.model;

import cl.ucn.app.exception.EquipoNoDisponibleException;
import jakarta.persistence.*;

/**
 * Equipo de laboratorio que se presta y se devuelve (Grupo 5 - Inventario).
 * <p>
 * Regla de negocio: un equipo prestado no puede prestarse nuevamente.
 */
@Entity
@DiscriminatorValue("EQUIPO")
public class Equipo extends Producto {

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EstadoEquipo estado = EstadoEquipo.DISPONIBLE;

    public Equipo() {
    }

    public Equipo(String codigo, String nombre, String descripcion, Categoria categoria, Proveedor proveedor) {
        super(codigo, nombre, descripcion, categoria, proveedor);
        this.estado = EstadoEquipo.DISPONIBLE;
    }

    public boolean estaDisponible() {
        return estado == EstadoEquipo.DISPONIBLE && Boolean.TRUE.equals(getActivo());
    }

    public void marcarPrestado() {
        if (!estaDisponible()) {
            throw new EquipoNoDisponibleException(getCodigo());
        }
        this.estado = EstadoEquipo.PRESTADO;
    }

    public void marcarDisponible() {
        this.estado = EstadoEquipo.DISPONIBLE;
    }

    @Override
    public String getTipoRecurso() {
        return "EQUIPO";
    }

    @Override
    public String getDisponibilidad() {
        return estado == EstadoEquipo.DISPONIBLE ? "Disponible" : "Prestado";
    }

    public EstadoEquipo getEstado() {
        return estado;
    }
}
