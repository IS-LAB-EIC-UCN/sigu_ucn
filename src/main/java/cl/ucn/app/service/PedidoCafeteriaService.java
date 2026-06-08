package cl.ucn.app.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import cl.ucn.app.model.PedidoCafeteria;
import cl.ucn.app.model.ProductoCafeteria;
import cl.ucn.app.repository.PedidoCafeteriaRepository;
import cl.ucn.app.repository.ProductoCafeteriaRepository;

public class PedidoCafeteriaService {

    private final PedidoCafeteriaRepository pedidoRepository;
    private final ProductoCafeteriaRepository productoRepository;

    public PedidoCafeteriaService() {
        this.pedidoRepository = new PedidoCafeteriaRepository();
        this.productoRepository = new ProductoCafeteriaRepository();}

    public BigDecimal calcularSubtotal(ProductoCafeteria producto, int cantidad) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");}

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");}

        return producto.getPrecio().multiply(BigDecimal.valueOf(cantidad));}

    public void validarStock(ProductoCafeteria producto, int cantidad) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no existe.");}

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");}

        if (producto.getStock() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente para el producto seleccionado.");}}

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

        return pedido;}
}