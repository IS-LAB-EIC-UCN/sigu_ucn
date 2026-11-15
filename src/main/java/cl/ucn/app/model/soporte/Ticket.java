package cl.ucn.app.model.soporte;

import cl.ucn.app.model.Usuario;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoTicket estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public Ticket() {}

    public Ticket(String titulo, String descripcion, Usuario usuario) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.usuario = usuario;
        this.estado = EstadoTicket.CREADO;
        this.fechaCreacion = LocalDateTime.now();
    }

    public Long getId() {return id;}
    public String getTitulo() {return titulo;}
    public String getDescripcion() {return descripcion;}
    public EstadoTicket getEstado() {return estado;}
    public LocalDateTime getFechaCreacion() {return fechaCreacion;}
    public Usuario getUsuario() {return usuario;}
}
