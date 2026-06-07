package cl.ucn.app.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "eventos")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(length = 100)
    private String tematica;

    @Column(nullable = false)
    private Integer capacidad;

    @Column(nullable = false, length = 30)
    private String estado = "PLANIFICADO";

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "espacio_id", nullable = false)
    private Espacio espacio;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "evento_expositores",
        joinColumns = @JoinColumn(name = "evento_id"),
        inverseJoinColumns = @JoinColumn(name = "expositor_id")
    )
    private List<Expositor> expositores = new ArrayList<>();

    public Evento() {}

    public Evento(String titulo, String descripcion, LocalDate fecha, LocalTime horaInicio,
                  LocalTime horaFin, String tematica, Integer capacidad, Espacio espacio) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.tematica = tematica;
        this.capacidad = capacidad;
        this.espacio = espacio;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public String getTematica() { return tematica; }
    public void setTematica(String tematica) { this.tematica = tematica; }

    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Espacio getEspacio() { return espacio; }
    public void setEspacio(Espacio espacio) { this.espacio = espacio; }

    public List<Expositor> getExpositores() { return expositores; }
    public void setExpositores(List<Expositor> expositores) { this.expositores = expositores; }
}
