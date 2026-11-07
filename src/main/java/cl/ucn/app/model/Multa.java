package cl.ucn.app.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "multas")
public class Multa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dias_atraso", nullable = false)
    private int diasAtraso;

    @Column(nullable = false)
    private long monto;

    @Column(name = "fecha_generacion", nullable = false)
    private LocalDate fechaGeneracion;

    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoMulta estado = EstadoMulta.PENDIENTE;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prestamo_id", nullable = false, unique = true)
    private Prestamo prestamo;

    public Multa() {
    }

    public Multa(Prestamo prestamo, int diasAtraso, long monto, LocalDate fechaGeneracion) {
        this.prestamo = prestamo;
        this.diasAtraso = diasAtraso;
        this.monto = monto;
        this.fechaGeneracion = fechaGeneracion;
        this.estado = EstadoMulta.PENDIENTE;
    }

    public Long getId() {
        return id;
    }

    public int getDiasAtraso() {
        return diasAtraso;
    }

    public void setDiasAtraso(int diasAtraso) {
        this.diasAtraso = diasAtraso;
    }

    public long getMonto() {
        return monto;
    }

    public void setMonto(long monto) {
        this.monto = monto;
    }

    public LocalDate getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDate fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public EstadoMulta getEstado() {
        return estado;
    }

    public void setEstado(EstadoMulta estado) {
        this.estado = estado;
    }

    public Prestamo getPrestamo() {
        return prestamo;
    }

    public void setPrestamo(Prestamo prestamo) {
        this.prestamo = prestamo;
    }

    public boolean estaPendiente() {
        return this.estado == EstadoMulta.PENDIENTE;
    }

    public void pagar(LocalDate fecha) {
        this.estado = EstadoMulta.PAGADA;
        this.fechaPago = fecha;
    }
}
