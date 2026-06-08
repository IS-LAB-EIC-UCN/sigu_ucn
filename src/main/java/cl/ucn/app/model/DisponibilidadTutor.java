package cl.ucn.app.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "disponibilidad_tutor")
public class DisponibilidadTutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usuario con rol TUTOR — validado en la capa de servicio
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Usuario tutor;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    // true cuando una Tutoria ya ocupa este bloque
    @Column(nullable = false)
    private Boolean ocupado = false;

    public DisponibilidadTutor() {}

    public DisponibilidadTutor(Usuario tutor, LocalDate fecha,
                               LocalTime horaInicio, LocalTime horaFin,
                               String modalidad) {
        this.tutor      = tutor;
        this.fecha      = fecha;
        this.horaInicio = horaInicio;
        this.horaFin    = horaFin;
    }

    public Long getId()              { return id; }
    public Usuario getTutor()        { return tutor; }
    public LocalDate getFecha()      { return fecha; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin()    { return horaFin; }
    public Boolean getOcupado()      { return ocupado; }

    public void setTutor(Usuario tutor)           { this.tutor = tutor; }
    public void setFecha(LocalDate fecha)         { this.fecha = fecha; }
    public void setHoraInicio(LocalTime h)        { this.horaInicio = h; }
    public void setHoraFin(LocalTime h)           { this.horaFin = h; }
    public void setOcupado(Boolean ocupado)       { this.ocupado = ocupado; }
}