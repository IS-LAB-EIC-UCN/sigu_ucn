package cl.ucn.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tutorias_reservas")
public class TutoriaReserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disponibilidad_id", unique = true, nullable = false)
    private Tutoria tutoria;

    @Column(name = "estudiante_id", nullable = false)
    private Long estudianteId;

    @Column(name = "asistio")
    private Boolean asistio = null; // NULL = Pendiente, TRUE = Asistió, FALSE = Faltó

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    public TutoriaReserva() {}
    public  TutoriaReserva(Tutoria tutoria,long estudianteId,boolean asistio,LocalDateTime creadoEn) {
        this.tutoria = tutoria;
        this.estudianteId = estudianteId;
        this.asistio = asistio;
        this.creadoEn = creadoEn;

    }
    // Getters y Setters
    public Long getId() { return id; }
    public Tutoria getTutoria() { return tutoria; }
    public Boolean getAsistio() { return asistio; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
    public Long getEstudianteId() { return estudianteId; }


    public void setEstudianteId(Long estudianteId) { this.estudianteId = estudianteId; }
    public void setId(Long id) { this.id = id; }
    public void setDisponibilidad(Tutoria tutoria) { this.tutoria = tutoria; }
    public void setAsistio(Boolean asistio) { this.asistio = asistio; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}
