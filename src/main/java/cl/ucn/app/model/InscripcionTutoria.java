package cl.ucn.app.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tut_inscripciones")
public class InscripcionTutoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tutoria_id", nullable = false)
    private Tutoria tutoria;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoInscripcion estado = EstadoInscripcion.RESERVADA;

    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDateTime fechaInscripcion;

    @Column(name = "motivo_cancelacion", length = 255)
    private String motivoCancelacion;

    @Column
    private Boolean asistio;

    public InscripcionTutoria() {
    }

    public InscripcionTutoria(Tutoria tutoria, Estudiante estudiante) {
        this.tutoria = tutoria;
        this.estudiante = estudiante;
        this.estado = EstadoInscripcion.RESERVADA;
        this.fechaInscripcion = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Tutoria getTutoria() {
        return tutoria;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public EstadoInscripcion getEstado() {
        return estado;
    }

    public LocalDateTime getFechaInscripcion() {
        return fechaInscripcion;
    }

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }

    public Boolean getAsistio() {
        return asistio;
    }

    public void setEstado(EstadoInscripcion estado) {
        this.estado = estado;
    }

    public void setMotivoCancelacion(String motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
    }

    public void setAsistio(Boolean asistio) {
        this.asistio = asistio;
    }

}