package cl.ucn.app.model.soporte;

import cl.ucn.app.model.Usuario;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_tickets")
public class HistorialTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario autor;

    @Column(name = "fecha_cambio", nullable = false)
    private LocalDateTime fechaCambio;

    @Column(name = "accion", nullable = false, length = 150)
    private String accionRealizada;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior", length = 30)
    private EstadoTicket estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", length = 30)
    private EstadoTicket estadoNuevo;

    protected HistorialTicket() {}

    public HistorialTicket(Ticket ticket, Usuario autor, String accionRealizada,
                           EstadoTicket estadoAnterior, EstadoTicket estadoNuevo) {
        this.ticket = ticket;
        this.autor = autor;
        this.accionRealizada = accionRealizada;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.fechaCambio = LocalDateTime.now();
    }

    public Long getId() {return id;}
    public Ticket getTicket() {return ticket;}
    public Usuario getAutor() {return autor;}
    public LocalDateTime getFechaCambio() {return fechaCambio;}
    public String getAccionRealizada() {return accionRealizada;}
    public EstadoTicket getEstadoAnterior() {return estadoAnterior;}
    public EstadoTicket getEstadoNuevo() {return estadoNuevo;}
}
