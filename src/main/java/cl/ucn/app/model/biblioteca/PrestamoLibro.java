package cl.ucn.app.model.biblioteca;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "prestamo")
public class PrestamoLibro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "fecha_devolucion")
    private LocalDate fechaDevolucion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoPrestamo estado = EstadoPrestamo.ACTIVO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lector_id", nullable = false)
    private Lector lector;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ejemplar_id", nullable = false)
    private Ejemplar ejemplar;

    public PrestamoLibro() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public LocalDate getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(LocalDate fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }

    public EstadoPrestamo getEstado() { return estado; }
    public void setEstado(EstadoPrestamo estado) { this.estado = estado; }

    public Lector getLector() { return lector; }
    public void setLector(Lector lector) { this.lector = lector; }

    public Ejemplar getEjemplar() { return ejemplar; }
    public void setEjemplar(Ejemplar ejemplar) { this.ejemplar = ejemplar; }
}