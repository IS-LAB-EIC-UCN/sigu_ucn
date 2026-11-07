package cl.ucn.app.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "prestamos")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_prestamo", nullable = false)
    private LocalDate fechaPrestamo;

    @Column(name = "fecha_limite", nullable = false)
    private LocalDate fechaLimite;

    @Column(name = "fecha_devolucion")
    private LocalDate fechaDevolucion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPrestamo estado = EstadoPrestamo.ACTIVO;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lector_id", nullable = false)
    private Lector lector;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ejemplar_id", nullable = false)
    private Ejemplar ejemplar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recibido_por_id")
    private Bibliotecario recibidoPor;

    @OneToOne(mappedBy = "prestamo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Multa multa;

    public Prestamo() {
    }

    public Prestamo(Lector lector, Ejemplar ejemplar, LocalDate fechaPrestamo, LocalDate fechaLimite) {
        this.lector = lector;
        this.ejemplar = ejemplar;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaLimite = fechaLimite;
        this.estado = EstadoPrestamo.ACTIVO;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public EstadoPrestamo getEstado() {
        return estado;
    }

    public void setEstado(EstadoPrestamo estado) {
        this.estado = estado;
    }

    public Lector getLector() {
        return lector;
    }

    public void setLector(Lector lector) {
        this.lector = lector;
    }

    public Ejemplar getEjemplar() {
        return ejemplar;
    }

    public void setEjemplar(Ejemplar ejemplar) {
        this.ejemplar = ejemplar;
    }

    public Bibliotecario getRecibidoPor() {
        return recibidoPor;
    }

    public void setRecibidoPor(Bibliotecario recibidoPor) {
        this.recibidoPor = recibidoPor;
    }

    public Multa getMulta() {
        return multa;
    }

    public void setMulta(Multa multa) {
        this.multa = multa;
    }

    public boolean estaVencido(LocalDate hoy) {
        return this.estado == EstadoPrestamo.ACTIVO && hoy.isAfter(this.fechaLimite);
    }

    public int calcularDiasAtraso() {
        if (fechaDevolucion == null || !fechaDevolucion.isAfter(fechaLimite)) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(fechaLimite, fechaDevolucion);
    }

    public void registrarDevolucion(LocalDate fecha) {
        this.fechaDevolucion = fecha;
        this.estado = EstadoPrestamo.DEVUELTO;
    }
}
