package cl.ucn.app.model.talleres;

import cl.ucn.app.model.Usuario;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inscripciones")
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "taller_id", nullable = false)
    private Taller taller;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoInscripcion estado;

    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDateTime fechaInscripcion;

    @Column(name = "fecha_anulacion")
    private LocalDateTime fechaAnulacion;

    public Inscripcion() {
    }

    public Inscripcion(Taller taller, Usuario usuario, EstadoInscripcion estado) {
        this.taller = taller;
        this.usuario = usuario;
        this.estado = estado;
        this.fechaInscripcion = LocalDateTime.now();
    }

    /** Anula la inscripción. Una inscripción ya anulada no puede volver a anularse. */
    public void anular() {
        if (this.estado == EstadoInscripcion.ANULADA) {
            throw new IllegalStateException("La inscripción ya se encuentra anulada.");
        }
        this.estado = EstadoInscripcion.ANULADA;
        this.fechaAnulacion = LocalDateTime.now();
    }

    /** Pasa una inscripción de la lista de espera a inscrita (cuando se libera un cupo). */
    public void promoverDesdeEspera() {
        if (this.estado != EstadoInscripcion.EN_ESPERA) {
            throw new IllegalStateException("Solo se puede promover una inscripción que esté en lista de espera.");
        }
        this.estado = EstadoInscripcion.INSCRITO;
    }

    public Long getId() { return id; }
    public Taller getTaller() { return taller; }
    public Usuario getUsuario() { return usuario; }
    public EstadoInscripcion getEstado() { return estado; }
    public LocalDateTime getFechaInscripcion() { return fechaInscripcion; }
    public LocalDateTime getFechaAnulacion() { return fechaAnulacion; }

    public void setId(Long id) { this.id = id; }
    public void setTaller(Taller taller) { this.taller = taller; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public void setEstado(EstadoInscripcion estado) { this.estado = estado; }
    public void setFechaInscripcion(LocalDateTime fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }
}
