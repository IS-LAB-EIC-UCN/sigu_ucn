package cl.ucn.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tutorias")
public class Tutoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Usuario estudiante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Usuario tutor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asignatura_id", nullable = false)
    private Asignatura asignatura;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "disponibilidad_id", nullable = false, unique = true)
    private DisponibilidadTutor disponibilidad;

    @Column(nullable = false)
    private String estado = "PENDIENTE";

    @Column(nullable = false)
    private Boolean asistencia = false;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    // ── Constructores ─────────────────────────────────────────

    public Tutoria() {}

    public Tutoria(Usuario estudiante, Usuario tutor,
                   Asignatura asignatura, DisponibilidadTutor disponibilidad) {
        this.estudiante    = estudiante;
        this.tutor         = tutor;
        this.asignatura    = asignatura;
        this.disponibilidad = disponibilidad;
    }


    public boolean esCancelable() {
        return this.estado.equals("PENDIENTE") || this.estado.equals("CONFIRMADA");
    }

    public boolean estaFinalizada() {
        return this.estado.equals("COMPLETADA") || this.estado.equals("CANCELADA");
    }

    public void cancelar() {
        if (estaFinalizada()) throw new IllegalStateException("La tutoría ya está finalizada.");
        this.estado = "CANCELADA";
    }

    public void cambiarEstado(String nuevoEstado) {
        if (estaFinalizada()) throw new IllegalStateException("La tutoría ya está finalizada.");
        this.estado = nuevoEstado;
    }


    public Long getId()                            { return id; }
    public Usuario getEstudiante()                 { return estudiante; }
    public Usuario getTutor()                      { return tutor; }
    public Asignatura getAsignatura()              { return asignatura; }
    public DisponibilidadTutor getDisponibilidad() { return disponibilidad; }
    public String getEstado()                      { return estado; }
    public Boolean getAsistencia()                 { return asistencia; }
    public LocalDateTime getCreadoEn()             { return creadoEn; }


    public void setId(Long id)                             { this.id = id; }
    public void setEstudiante(Usuario estudiante)          { this.estudiante = estudiante; }
    public void setTutor(Usuario tutor)                    { this.tutor = tutor; }
    public void setAsignatura(Asignatura asignatura)       { this.asignatura = asignatura; }
    public void setDisponibilidad(DisponibilidadTutor d)   { this.disponibilidad = d; }
    public void setEstado(String estado)                   { this.estado = estado; }
    public void setAsistencia(Boolean asistencia)          { this.asistencia = asistencia; }
}