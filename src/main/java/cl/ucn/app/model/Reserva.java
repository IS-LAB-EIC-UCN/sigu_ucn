package cl.ucn.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
public class Reserva {
    public enum Estado {
        PENDIENTE,
        APROBADA,
        RECHAZADA,
        CANCELADA
    }

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "espacio_id", nullable = false)
    private Espacio espacio;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado;

    public Reserva() {}

    public Reserva(String id, Usuario usuario, Espacio espacio, LocalDateTime fechaInicio, LocalDateTime fechaFin, Estado estado) {
        this.id = id;
        this.usuario = usuario;
        this.espacio = espacio;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Espacio getEspacio() { return espacio; }
    public void setEspacio(Espacio espacio) { this.espacio = espacio; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
}
