package cl.ucn.app.model.biblioteca;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "multa")
public class Multa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dias_atraso", nullable = false)
    private Integer diasAtraso;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "pagada", nullable = false)
    private Boolean pagada = false;

    @Column(name = "fecha_generacion", nullable = false)
    private LocalDate fechaGeneracion;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "prestamo_id", nullable = false, unique = true)
    private PrestamoLibro prestamo;

    public Multa() {}

    // Getters y Setters
    public Long getId() { return id; }

    public Integer getDiasAtraso() { return diasAtraso; }
    public void setDiasAtraso(Integer diasAtraso) { this.diasAtraso = diasAtraso; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public Boolean getPagada() { return pagada; }
    public void setPagada(Boolean pagada) { this.pagada = pagada; }

    public LocalDate getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(LocalDate fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }

    public PrestamoLibro getPrestamo() { return prestamo; }
    public void setPrestamo(PrestamoLibro prestamo) { this.prestamo = prestamo; }
}