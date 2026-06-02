package cl.ucn.app.model;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "prestamos")
public class Prestamo extends MovimientoInventario {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "estado", nullable = false, length = 100)
    private String estado;

    public Prestamo() {}

    public Prestamo(Recurso recurso, int cantidad, LocalDate fecha, LocalTime hora, Usuario usuario, String estado) {
        super(recurso, cantidad, fecha, hora);
        this.usuario = usuario;
        this.estado = estado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getEstado() {
        return estado;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Prestamo [id=" + id + ", usuario=" + usuario + ", recurso=" + recurso + ", cantidad=" + cantidad
                + ", fecha=" + fecha + ", hora=" + hora + "]";
    }

}
