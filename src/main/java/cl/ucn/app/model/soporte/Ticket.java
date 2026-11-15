package cl.ucn.app.model.soporte;

import cl.ucn.app.model.Usuario;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tecnico_id")
    private Usuario tecnicoAsignado;

    public Ticket() {}

    public Ticket(String titulo, String descripcion, Usuario usuario) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.usuario = usuario;
        this.estado = EstadoTicket.CREADO;
        this.fechaCreacion = LocalDateTime.now();
    }

    public void asignarTecnico(Usuario tecnico) {
        Objects.requireNonNull(tecnico, "El técnico es obligatorio.");

        if (estado == EstadoTicket.CERRADO) {
            throw new IllegalArgumentException("No se puede modificar un ticket cerrado.");
        }

        if (estado != EstadoTicket.CREADO && estado != EstadoTicket.ASIGNADO) {
            throw new IllegalArgumentException("El ticket no está disponible para asignación");
        }

        this.tecnicoAsignado = tecnico;
        this.estado = EstadoTicket.ASIGNADO;
    }

    public Long getId() {return id;}
    public String getTitulo() {return titulo;}
    public String getDescripcion() {return descripcion;}
    public EstadoTicket getEstado() {return estado;}
    public LocalDateTime getFechaCreacion() {return fechaCreacion;}
    public Usuario getUsuario() {return usuario;}
    public Usuario getTecnicoAsignado() {return tecnicoAsignado;}
}
