package cl.ucn.app.model.talleres;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "talleres")
public class Taller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false, length = 50)
    private String categoria;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(length = 100)
    private String lugar;

    @Column(name = "cupo_maximo", nullable = false)
    private Integer cupoMaximo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoTaller estado = EstadoTaller.ACTIVO;

    public Taller() {
    }

    public Taller(String nombre, String descripcion, String categoria, LocalDate fecha,
                  LocalTime horaInicio, LocalTime horaFin, String lugar, Integer cupoMaximo) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.lugar = lugar;
        this.cupoMaximo = cupoMaximo;
        this.estado = EstadoTaller.ACTIVO;
    }

    /**
     * Indica si el horario de este taller se cruza con el de otro.
     * Dos talleres se solapan si son el mismo día y sus intervalos [inicio, fin) se intersectan.
     * Que uno termine exactamente cuando empieza el otro NO es solapamiento.
     */
    public boolean seSolapaCon(Taller otro) {
        if (otro == null || this.fecha == null || otro.fecha == null) {
            return false;
        }
        if (!this.fecha.equals(otro.fecha)) {
            return false;
        }
        if (this.horaInicio == null || this.horaFin == null || otro.horaInicio == null || otro.horaFin == null) {
            return false;
        }
        return this.horaInicio.isBefore(otro.horaFin) && otro.horaInicio.isBefore(this.horaFin);
    }

    public boolean estaActivo() {
        return estado == EstadoTaller.ACTIVO;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getCategoria() { return categoria; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public String getLugar() { return lugar; }
    public Integer getCupoMaximo() { return cupoMaximo; }
    public EstadoTaller getEstado() { return estado; }

    public void setId(Long id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
    public void setLugar(String lugar) { this.lugar = lugar; }
    public void setCupoMaximo(Integer cupoMaximo) { this.cupoMaximo = cupoMaximo; }
    public void setEstado(EstadoTaller estado) { this.estado = estado; }
}
