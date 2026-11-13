package cl.ucn.app.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_pedido")
@IdClass(DetallePedidoId.class)
public class DetallePedido {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_registrado", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioRegistrado;

    public DetallePedido() {
    }

    public DetallePedido(Pedido pedido, Producto producto, Integer cantidad, BigDecimal precioRegistrado) {
        this.pedido = pedido;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioRegistrado = precioRegistrado;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioRegistrado() {
        return precioRegistrado;
    }

    public void setPrecioRegistrado(BigDecimal precioRegistrado) {
        this.precioRegistrado = precioRegistrado;
    }

    public BigDecimal getSubtotal() {
        if (precioRegistrado == null || cantidad == null) {
            return BigDecimal.ZERO;
        }
        return precioRegistrado.multiply(BigDecimal.valueOf(cantidad));
    }
}
