package cl.ucn.app.model;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "entradas")
public class Entrada extends MovimientoInventario {
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    public Entrada() {}

    public Entrada(Recurso recurso, int cantidad, LocalDate fecha, LocalTime hora, Proveedor proveedor) {
        super(recurso, cantidad, fecha, hora);
        this.proveedor = proveedor;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    @Override
    public String toString() {
        return "Entrada [proveedor=" + proveedor + ", id=" + id + ", recurso=" + recurso + ", cantidad=" + cantidad
                + ", fecha=" + fecha + ", hora=" + hora + "]";
    }

}
