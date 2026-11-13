package cl.ucn.app.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "DetallePedido")
@IdClass(DetallePedidoId.class)
public class DetallePedido {
    @Id
    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @Id
    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_registrado", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioRegistrado;

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
}