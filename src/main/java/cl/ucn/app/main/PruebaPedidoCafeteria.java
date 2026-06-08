package cl.ucn.app.main;

import cl.ucn.app.model.PedidoCafeteria;
import cl.ucn.app.service.PedidoCafeteriaService;

public class PruebaPedidoCafeteria {

    public static void main(String[] args) {
        PedidoCafeteriaService service = new PedidoCafeteriaService();

        PedidoCafeteria pedido = service.crearPedidoBasico(1L, 2);

        System.out.println("Pedido creado correctamente:");
        System.out.println("ID: " + pedido.getId());
        System.out.println("Estado: " + pedido.getEstado());
        System.out.println("Total: $" + pedido.getTotal());
    }}