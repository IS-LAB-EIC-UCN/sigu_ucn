
package cl.ucn.app.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "eventos")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(nullable = false, length = 150)
    private String lugar;

    @Column(name = "cupo_maximo", nullable = false)
    private Integer cupoMaximo;

    @Column(nullable = false, length = 30)
    private String estado = "BORRADOR";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizador_id", nullable = false)
    private Usuario organizador;

    @Column(length = 100)
    private String tematica;
    @ManyToOne @JoinColumn(name = "expositor_id")
    private Expositor expositor;
    @ManyToOne @JoinColumn(name = "espacio_id")
    private Espacio espacio;
    public String getTematica() { return tematica == null ? "General" : tematica; }
    public void setTematica(String tematica) { this.tematica = tematica; }
    @ManyToMany
    @JoinTable(name = "evento_expositores", joinColumns = @JoinColumn(name = "evento_id"), inverseJoinColumns = @JoinColumn(name = "expositor_id"))
    @OrderBy("nombre ASC")
    private java.util.List<Expositor> expositores = new java.util.ArrayList<>();
    public java.util.List<Expositor> getExpositores() {
        // Compatibilidad con eventos y scripts iniciales anteriores a la migración 08.
        return expositores.isEmpty() && expositor != null ? java.util.List.of(expositor) : expositores;
    }
    public void setExpositores(java.util.List<Expositor> lista) {
        expositores = new java.util.ArrayList<>(lista);
        expositor = lista.isEmpty() ? null : lista.get(0);
    }
    public String getNombresExpositores() {
        return getExpositores().stream().map(Expositor::getNombre).collect(java.util.stream.Collectors.joining(", "));
    }
    public Expositor getExpositor() { return expositor; }
    public void setExpositor(Expositor expositor) { this.expositor = expositor; }
    public Espacio getEspacio() { return espacio; }
    public void setEspacio(Espacio espacio) { this.espacio = espacio; }
    public boolean isDisponible() {
        return "PUBLICADO".equals(estado) && java.time.LocalDateTime.of(fecha, horaInicio).isAfter(java.time.LocalDateTime.now());
    }
    public Evento() {
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public Integer getCupoMaximo() {
        return cupoMaximo;
    }

    public void setCupoMaximo(Integer cupoMaximo) {
        this.cupoMaximo = cupoMaximo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Usuario getOrganizador() {
        return organizador;
    }

    public void setOrganizador(Usuario organizador) {
        this.organizador = organizador;
    }
}
