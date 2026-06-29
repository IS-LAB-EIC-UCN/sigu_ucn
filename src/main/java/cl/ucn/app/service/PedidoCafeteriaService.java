package cl.ucn.app.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import cl.ucn.app.model.PedidoCafeteria;
import cl.ucn.app.model.ProductoCafeteria;
import cl.ucn.app.repository.PedidoCafeteriaRepository;
import cl.ucn.app.repository.ProductoCafeteriaRepository;
import cl.ucn.app.model.DetallePedidoCafeteria;
import cl.ucn.app.repository.DetallePedidoCafeteriaRepository;
import java.util.List;

public class PedidoCafeteriaService {

    private final PedidoCafeteriaRepository pedidoRepository;
    private final ProductoCafeteriaRepository productoRepository;
    private final DetallePedidoCafeteriaRepository detalleRepository;

    public PedidoCafeteriaService() {
        this.pedidoRepository = new PedidoCafeteriaRepository();
        this.productoRepository = new ProductoCafeteriaRepository();
        this.detalleRepository = new DetallePedidoCafeteriaRepository();
    }

    public BigDecimal calcularSubtotal(ProductoCafeteria producto, int cantidad) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }

        return producto.getPrecio().multiply(BigDecimal.valueOf(cantidad));
    }

    public void validarStock(ProductoCafeteria producto, int cantidad) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no existe.");
        }

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }

        if (producto.getStock() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente para el producto seleccionado.");
        }
    }

    public PedidoCafeteria crearPedidoBasico(Long productoId, int cantidad) {
        ProductoCafeteria producto = productoRepository.buscarPorId(productoId);

        validarStock(producto, cantidad);

        BigDecimal total = calcularSubtotal(producto, cantidad);

        PedidoCafeteria pedido = new PedidoCafeteria(
                LocalDateTime.now(),
                "PENDIENTE",
                total
        );

        pedidoRepository.guardar(pedido);

        producto.setStock(producto.getStock() - cantidad);
        productoRepository.actualizar(producto);

        return pedido;
    }

    public PedidoCafeteria cambiarEstado(Long pedidoId, String nuevoEstado) {
        PedidoCafeteria pedido = pedidoRepository.buscarPorId(pedidoId);

        if (pedido == null) {
            throw new IllegalArgumentException("El pedido no existe.");
        }

        if (!nuevoEstado.equals("PENDIENTE")
                && !nuevoEstado.equals("EN_PREPARACION")
                && !nuevoEstado.equals("LISTO")
                && !nuevoEstado.equals("ENTREGADO")
                && !nuevoEstado.equals("ANULADO")) {
            throw new IllegalArgumentException("Estado no válido.");
        }

        pedido.setEstado(nuevoEstado);
        pedidoRepository.actualizar(pedido);

        return pedido;
    }

    public PedidoCafeteria anularPedido(Long pedidoId) {
        PedidoCafeteria pedido = pedidoRepository.buscarPorId(pedidoId);

        if (pedido == null) {
            throw new IllegalArgumentException("El pedido no existe.");
        }

        if (pedido.getEstado().equals("ENTREGADO")) {
            throw new IllegalArgumentException("Un pedido entregado no puede anularse.");
        }

        pedido.setEstado("ANULADO");
        pedidoRepository.actualizar(pedido);

        return pedido;
    }

    public PedidoCafeteria crearPedidoConVariosProductos(List<Long> productoIds, List<Integer> cantidades) {
        if (productoIds == null || cantidades == null || productoIds.isEmpty()) {
            throw new IllegalArgumentException("El pedido debe tener al menos un producto.");
        }

        if (productoIds.size() != cantidades.size()) {
            throw new IllegalArgumentException("La cantidad de productos y cantidades no coincide.");
        }

        BigDecimal total = BigDecimal.ZERO;

        for (int i = 0; i < productoIds.size(); i++) {
            ProductoCafeteria producto = productoRepository.buscarPorId(productoIds.get(i));
            int cantidad = cantidades.get(i);

            validarStock(producto, cantidad);

            BigDecimal subtotal = calcularSubtotal(producto, cantidad);
            total = total.add(subtotal);
        }

        PedidoCafeteria pedido = new PedidoCafeteria(
                LocalDateTime.now(),
                "PENDIENTE",
                total
        );

        pedidoRepository.guardar(pedido);

        for (int i = 0; i < productoIds.size(); i++) {
            ProductoCafeteria producto = productoRepository.buscarPorId(productoIds.get(i));
            int cantidad = cantidades.get(i);

            BigDecimal subtotal = calcularSubtotal(producto, cantidad);

            DetallePedidoCafeteria detalle = new DetallePedidoCafeteria(
                    pedido,
                    producto,
                    cantidad,
                    subtotal
            );

            detalleRepository.guardar(detalle);

            producto.setStock(producto.getStock() - cantidad);
            productoRepository.actualizar(producto);
        }

        return pedido;
    }
    }