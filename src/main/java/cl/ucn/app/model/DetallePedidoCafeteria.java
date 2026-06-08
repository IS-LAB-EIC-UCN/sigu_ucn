package cl.ucn.app.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "detalle_pedidos_cafeteria")
public class DetallePedidoCafeteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoCafeteria pedido;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoCafeteria producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private BigDecimal subtotal;

    public DetallePedidoCafeteria() {}

    public DetallePedidoCafeteria(PedidoCafeteria pedido, ProductoCafeteria producto, Integer cantidad, BigDecimal subtotal) {
        this.pedido = pedido;
        this.producto = producto;
        this.cantidad = cantidad;
        this.subtotal = subtotal;}

    public Long getId() {
        return id;}

    public PedidoCafeteria getPedido() {
        return pedido;}

    public ProductoCafeteria getProducto() {
        return producto;}

    public Integer getCantidad() {
        return cantidad;}

    public BigDecimal getSubtotal() {
        return subtotal;}

    public void setId(Long id) {
        this.id = id;}

    public void setPedido(PedidoCafeteria pedido) {
        this.pedido = pedido;}

    public void setProducto(ProductoCafeteria producto) {
        this.producto = producto;}

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;}

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;}
}