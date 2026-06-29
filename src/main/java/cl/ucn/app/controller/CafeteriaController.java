package cl.ucn.app.controller;

import java.util.List;

import cl.ucn.app.model.PedidoCafeteria;
import cl.ucn.app.model.ProductoCafeteria;
import cl.ucn.app.repository.ProductoCafeteriaRepository;
import cl.ucn.app.service.PedidoCafeteriaService;
import io.javalin.http.Context;

public class CafeteriaController {

    private final ProductoCafeteriaRepository productoRepository;
    private final PedidoCafeteriaService pedidoService;

    public CafeteriaController() {
        this.productoRepository = new ProductoCafeteriaRepository();
        this.pedidoService = new PedidoCafeteriaService();
    }

    public void mostrarCafeteria(Context ctx) {
        List<ProductoCafeteria> productos = productoRepository.listar();

        String html = construirPagina(productos, "", "");

        ctx.contentType("text/html; charset=UTF-8");
        ctx.html(html);
    }

    public void crearPedido(Context ctx) {
        String mensaje = "";
        String error = "";

        try {
            Long productoId = Long.parseLong(ctx.formParam("productoId"));
            int cantidad = Integer.parseInt(ctx.formParam("cantidad"));

            PedidoCafeteria pedido = pedidoService.crearPedidoBasico(productoId, cantidad);

            mensaje = "Pedido creado correctamente. ID: " + pedido.getId()
                    + " | Total: $" + pedido.getTotal();

        } catch (Exception e) {
            error = e.getMessage();
        }

        List<ProductoCafeteria> productos = productoRepository.listar();

        String html = construirPagina(productos, mensaje, error);

        ctx.contentType("text/html; charset=UTF-8");
        ctx.html(html);
    }

    private String construirPagina(List<ProductoCafeteria> productos, String mensaje, String error) {
        StringBuilder html = new StringBuilder();

        html.append("""
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <title>Cafetería SIGU-UCN</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f4f4f4;
                            margin: 0;
                        }

                        header {
                            background-color: #003366;
                            color: white;
                            padding: 20px;
                            text-align: center;
                        }

                        main {
                            width: 85%;
                            margin: 30px auto;
                        }

                        section {
                            background-color: white;
                            padding: 20px;
                            margin-bottom: 25px;
                            border-radius: 8px;
                        }

                        table {
                            width: 100%;
                            border-collapse: collapse;
                        }

                        th, td {
                            border: 1px solid #ccc;
                            padding: 10px;
                            text-align: center;
                        }

                        th {
                            background-color: #003366;
                            color: white;
                        }

                        form {
                            display: flex;
                            flex-direction: column;
                            gap: 12px;
                        }

                        select, input, button {
                            padding: 10px;
                            font-size: 15px;
                        }

                        button {
                            background-color: #003366;
                            color: white;
                            border: none;
                            cursor: pointer;
                        }

                        .mensaje {
                            background-color: #d4edda;
                            color: #155724;
                            padding: 12px;
                            border-radius: 6px;
                            margin-bottom: 15px;
                        }

                        .error {
                            background-color: #f8d7da;
                            color: #721c24;
                            padding: 12px;
                            border-radius: 6px;
                            margin-bottom: 15px;
                        }
                    </style>
                </head>
                <body>
                    <header>
                        <h1>Módulo de Cafetería</h1>
                        <p>Sistema de gestión de productos y pedidos</p>
                    </header>

                    <main>
                """);

        if (mensaje != null && !mensaje.isEmpty()) {
            html.append("<div class='mensaje'>").append(mensaje).append("</div>");
        }

        if (error != null && !error.isEmpty()) {
            html.append("<div class='error'>").append(error).append("</div>");
        }

        html.append("""
                        <section>
                            <h2>Productos disponibles</h2>

                            <table>
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Producto</th>
                                        <th>Precio</th>
                                        <th>Stock</th>
                                    </tr>
                                </thead>
                                <tbody>
                """);

        for (ProductoCafeteria producto : productos) {
            html.append("<tr>");
            html.append("<td>").append(producto.getId()).append("</td>");
            html.append("<td>").append(producto.getNombre()).append("</td>");
            html.append("<td>$").append(producto.getPrecio()).append("</td>");
            html.append("<td>").append(producto.getStock()).append("</td>");
            html.append("</tr>");
        }

        html.append("""
                                </tbody>
                            </table>
                        </section>

                        <section>
                            <h2>Crear pedido</h2>

                            <form method="post" action="/cafeteria/pedidos">
                                <label for="productoId">Producto:</label>
                                <select name="productoId" id="productoId" required>
                """);

        for (ProductoCafeteria producto : productos) {
            html.append("<option value='")
                    .append(producto.getId())
                    .append("'>")
                    .append(producto.getNombre())
                    .append(" - $")
                    .append(producto.getPrecio())
                    .append(" - Stock: ")
                    .append(producto.getStock())
                    .append("</option>");
        }

        html.append("""
                                </select>

                                <label for="cantidad">Cantidad:</label>
                                <input type="number" name="cantidad" id="cantidad" min="1" required>

                                <button type="submit">Crear pedido</button>
                            </form>
                        </section>
                    </main>
                </body>
                </html>
                """);

        return html.toString();
    }
}