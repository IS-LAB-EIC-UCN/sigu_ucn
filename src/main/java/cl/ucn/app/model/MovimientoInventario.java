package cl.ucn.app.model;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "movimientosInventario")
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recurso_id", nullable = false)
    protected Recurso recurso;

    @Column(name = "cantidad", nullable = false)
    protected int cantidad;

    @Column(name = "fecha", nullable = false)
    protected LocalDate fecha;

    @Column(name = "hora", nullable = false)
    protected LocalTime hora;

    public MovimientoInventario() {}
    
    public MovimientoInventario(Recurso recurso, int cantidad, LocalDate fecha, LocalTime hora) {
        this.recurso = recurso;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.hora = hora;
    }

    public Long getId() {
        return id;
    }

    public Recurso getRecurso() {
        return recurso;
    }

    public int getCantidad() {
        return cantidad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setRecurso(Recurso recurso) {
        this.recurso = recurso;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    @Override
    public String toString() {
        return "MovimientoInventario [id=" + id + ", recurso=" + recurso + ", cantidad=" + cantidad + ", fecha=" + fecha
                + ", hora=" + hora + "]";
    }

}
