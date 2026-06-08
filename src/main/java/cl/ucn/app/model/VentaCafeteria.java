package cl.ucn.app.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ventas_cafeteria")
public class VentaCafeteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    private PedidoCafeteria pedido;

    @Column(name = "fecha_venta", nullable = false)
    private LocalDateTime fechaVenta;

    @Column(nullable = false)
    private BigDecimal total;

    public VentaCafeteria() {}

    public VentaCafeteria(PedidoCafeteria pedido, LocalDateTime fechaVenta, BigDecimal total) {
        this.pedido = pedido;
        this.fechaVenta = fechaVenta;
        this.total = total;}

    public Long getId() {
        return id;}

    public PedidoCafeteria getPedido() {
        return pedido;}

    public LocalDateTime getFechaVenta() {
        return fechaVenta;}

    public BigDecimal getTotal() {
        return total;}

    public void setId(Long id) {
        this.id = id;}

    public void setPedido(PedidoCafeteria pedido) {
        this.pedido = pedido;}

    public void setFechaVenta(LocalDateTime fechaVenta) {
        this.fechaVenta = fechaVenta;}

    public void setTotal(BigDecimal total) {
        this.total = total;}
}