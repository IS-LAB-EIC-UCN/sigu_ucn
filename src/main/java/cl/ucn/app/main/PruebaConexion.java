package cl.ucn.app.main;

import cl.ucn.app.model.ProductoCafeteria;
import cl.ucn.app.repository.ProductoCafeteriaRepository;

public class PruebaConexion {

    public static void main(String[] args) {
        ProductoCafeteriaRepository repo = new ProductoCafeteriaRepository();

        System.out.println("Productos de cafetería:");

        for (ProductoCafeteria producto : repo.listar()) {
            System.out.println(
                    producto.getId() + " - " +
                    producto.getNombre() + " - $" +
                    producto.getPrecio() + " - Stock: " +
                    producto.getStock()
            );}}}