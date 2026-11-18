package cl.ucn.app.model;

import java.io.Serializable;
import java.util.Objects;

public class DetallePedidoId implements Serializable {
    private Long pedido;
    private Long producto;

    public DetallePedidoId() {
    }

    public DetallePedidoId(Long pedido, Long producto) {
        this.pedido = pedido;
        this.producto = producto;
    }

    public Long getPedido() {
        return pedido;
    }

    public void setPedido(Long pedido) {
        this.pedido = pedido;
    }

    public Long getProducto() {
        return producto;
    }

    public void setProducto(Long producto) {
        this.producto = producto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DetallePedidoId that = (DetallePedidoId) o;
        return Objects.equals(pedido, that.pedido) && Objects.equals(producto, that.producto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pedido, producto);
    }
}
