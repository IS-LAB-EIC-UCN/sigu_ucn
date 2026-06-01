package cl.ucn.app.model;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "prestamos")
public class Prestamo extends MovimientoInventario {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public Prestamo() {}

    public Prestamo(Recurso recurso, int cantidad, LocalDate fecha, LocalTime time, Usuario usuario) {
        super(recurso, cantidad, fecha, time);
        this.usuario = usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    @Override
    public String toString() {
        return "Prestamo [id=" + id + ", usuario=" + usuario + ", recurso=" + recurso + ", cantidad=" + cantidad
                + ", fecha=" + fecha + ", hora=" + hora + "]";
    }
    
}
