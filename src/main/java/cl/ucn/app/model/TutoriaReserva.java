package cl.ucn.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tutorias_reservas")
public class TutoriaReserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cada disponibilidad solo puede reservarse una vez
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "disponibilidad_id", unique = true, nullable = false)
    private Tutoria tutoria;

    // Usuario que actúa como estudiante
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Usuario estudiante;

    // null = pendiente, true = asistió, false = faltó
    @Column(name = "asistio")
    private Boolean asistio = null;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    public TutoriaReserva() {
    }

    public TutoriaReserva(Tutoria tutoria, Usuario estudiante) {
        this.tutoria = tutoria;
        this.estudiante = estudiante;
        this.asistio = null;
        this.creadoEn = LocalDateTime.now();
    }

    public TutoriaReserva(Long id, Tutoria tutoria, Usuario estudiante, Boolean asistio, LocalDateTime creadoEn) {
        this.id = id;
        this.tutoria = tutoria;
        this.estudiante = estudiante;
        this.asistio = asistio;
        this.creadoEn = creadoEn;
    }

    public Long getId() {
        return id;
    }

    public Tutoria getTutoria() {
        return tutoria;
    }

    public Usuario getEstudiante() {
        return estudiante;
    }

    public Boolean getAsistio() {
        return asistio;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTutoria(Tutoria tutoria) {
        this.tutoria = tutoria;
    }

    public void setEstudiante(Usuario estudiante) {
        this.estudiante = estudiante;
    }

    public void setAsistio(Boolean asistio) {
        this.asistio = asistio;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}