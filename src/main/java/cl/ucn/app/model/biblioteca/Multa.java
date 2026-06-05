package cl.ucn.app.model.biblioteca;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "multa")
public class Multa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dias_atraso", nullable = false)
    private Integer diasAtraso;

    @Column(name = "monto", nullable = false)
    private Double monto;

    @Column(name = "pagada", nullable = false)
    private Boolean pagada = false;

    @Column(name = "fecha_generacion", nullable = false)
    private LocalDate fechaGeneracion;

    @ManyToOne
    @JoinColumn(name = "prestamo_id", nullable = false)
    private PrestamoLibro prestamo;

    public Multa() {}

    // Getters y Setters
    public Long getId() { return id; }

    public Integer getDiasAtraso() { return diasAtraso; }
    public void setDiasAtraso(Integer diasAtraso) { this.diasAtraso = diasAtraso; }

    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }

    public Boolean getPagada() { return pagada; }
    public void setPagada(Boolean pagada) { this.pagada = pagada; }

    public LocalDate getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(LocalDate fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }

    public PrestamoLibro getPrestamo() { return prestamo; }
    public void setPrestamo(PrestamoLibro prestamo) { this.prestamo = prestamo; }
}