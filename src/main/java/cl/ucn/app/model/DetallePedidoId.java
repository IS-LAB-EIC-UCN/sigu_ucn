package cl.ucn.app.model;

import java.io.Serializable;
import java.util.Objects;

public class DetallePedidoId implements Serializable {
    private Long pedido;
    private Long producto;

    public DetallePedidoId() {}

    public DetallePedidoId(Long pedido, Long producto) {
        this.pedido = pedido;
        this.producto = producto;
    }

    // Es obligatorio sobreescribir equals y hashCode para las llaves compuestas en JPA
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetallePedidoId that = (DetallePedidoId) o;
        return Objects.equals(pedido, that.pedido) && Objects.equals(producto, that.producto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pedido, producto);
    }
}