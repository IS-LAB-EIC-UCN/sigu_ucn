package cl.ucn.app.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pedidos_cafeteria")
public class PedidoCafeteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_pedido", nullable = false)
    private LocalDateTime fechaPedido;

    @Column(nullable = false, length = 30)
    private String estado;

    @Column(nullable = false)
    private BigDecimal total;

    public PedidoCafeteria() {}

    public PedidoCafeteria(LocalDateTime fechaPedido, String estado, BigDecimal total) {
        this.fechaPedido = fechaPedido;
        this.estado = estado;
        this.total = total;}

    public Long getId() {
        return id;}

    public LocalDateTime getFechaPedido() {
        return fechaPedido;}

    public String getEstado() {
        return estado;}

    public BigDecimal getTotal() {
        return total;}

    public void setId(Long id) {
        this.id = id;}

    public void setFechaPedido(LocalDateTime fechaPedido) {
        this.fechaPedido = fechaPedido;}

    public void setEstado(String estado) {
        this.estado = estado;}

    public void setTotal(BigDecimal total) {
        this.total = total;}
}