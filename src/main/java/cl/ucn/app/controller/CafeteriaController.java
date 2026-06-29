package cl.ucn.app.controller;

import java.util.ArrayList;
import java.util.List;

import cl.ucn.app.model.PedidoCafeteria;
import cl.ucn.app.model.ProductoCafeteria;
import cl.ucn.app.repository.PedidoCafeteriaRepository;
import cl.ucn.app.repository.ProductoCafeteriaRepository;
import cl.ucn.app.service.PedidoCafeteriaService;
import io.javalin.http.Context;

public class CafeteriaController {

    private final ProductoCafeteriaRepository productoRepository;
    private final PedidoCafeteriaService pedidoService;
    private final PedidoCafeteriaRepository pedidoRepository;

    public CafeteriaController() {
        this.productoRepository = new ProductoCafeteriaRepository();
        this.pedidoService = new PedidoCafeteriaService();
        this.pedidoRepository = new PedidoCafeteriaRepository();
    }

    public void mostrarCafeteria(Context ctx) {
        List<ProductoCafeteria> productos = productoRepository.listar();
        List<PedidoCafeteria> pedidos = pedidoRepository.listar();

        String html = construirPagina(productos, pedidos, "", "");

        ctx.contentType("text/html; charset=UTF-8");
        ctx.html(html);
    }

    public void crearPedido(Context ctx) {
        String mensaje = "";
        String error = "";

        try {
            List<String> productoIdTextos = ctx.formParams("productoId");
            List<String> cantidadTextos = ctx.formParams("cantidad");

            ArrayList<Long> productoIds = new ArrayList<>();
            ArrayList<Integer> cantidades = new ArrayList<>();

            for (int i = 0; i < productoIdTextos.size(); i++) {
                Long productoId = Long.parseLong(productoIdTextos.get(i));
                int cantidad = Integer.parseInt(cantidadTextos.get(i));

                productoIds.add(productoId);
                cantidades.add(cantidad);
            }

            PedidoCafeteria pedido = pedidoService.crearPedidoConVariosProductos(productoIds, cantidades);

            mensaje = "Pedido creado correctamente. ID: " + pedido.getId()
                    + " | Total: $" + pedido.getTotal();

        } catch (Exception e) {
            error = e.getMessage();
        }

        List<ProductoCafeteria> productos = productoRepository.listar();
        List<PedidoCafeteria> pedidos = pedidoRepository.listar();

        String html = construirPagina(productos, pedidos, mensaje, error);

        ctx.contentType("text/html; charset=UTF-8");
        ctx.html(html);
    }

    public void cambiarEstadoPedido(Context ctx) {
        String mensaje = "";
        String error = "";

        try {
            Long pedidoId = Long.parseLong(ctx.formParam("pedidoId"));
            String estado = ctx.formParam("estado");

            pedidoService.cambiarEstado(pedidoId, estado);

            mensaje = "Estado del pedido actualizado correctamente.";

        } catch (Exception e) {
            error = e.getMessage();
        }

        List<ProductoCafeteria> productos = productoRepository.listar();
        List<PedidoCafeteria> pedidos = pedidoRepository.listar();

        String html = construirPagina(productos, pedidos, mensaje, error);

        ctx.contentType("text/html; charset=UTF-8");
        ctx.html(html);
    }

    public void anularPedido(Context ctx) {
        String mensaje = "";
        String error = "";

        try {
            Long pedidoId = Long.parseLong(ctx.formParam("pedidoId"));

            pedidoService.anularPedido(pedidoId);

            mensaje = "Pedido anulado correctamente.";

        } catch (Exception e) {
            error = e.getMessage();
        }

        List<ProductoCafeteria> productos = productoRepository.listar();
        List<PedidoCafeteria> pedidos = pedidoRepository.listar();

        String html = construirPagina(productos, pedidos, mensaje, error);

        ctx.contentType("text/html; charset=UTF-8");
        ctx.html(html);
    }

    private String construirPagina(List<ProductoCafeteria> productos, List<PedidoCafeteria> pedidos, String mensaje, String error) {
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
                            margin-top: 15px;
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
                            border-radius: 4px;
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

                        .producto-pedido {
                            display: grid;
                            grid-template-columns: 2fr 1fr auto;
                            gap: 10px;
                            margin-bottom: 12px;
                            align-items: center;
                            background-color: #eef2f7;
                            padding: 12px;
                            border-radius: 6px;
                        }

                        .boton-agregar {
                            background-color: #006400;
                        }

                        .boton-quitar {
                            background-color: #8b0000;
                        }

                        .acciones {
                            display: flex;
                            gap: 8px;
                            justify-content: center;
                        }

                        .acciones form {
                            display: inline;
                        }

                        .acciones select,
                        .acciones button {
                            padding: 6px;
                            font-size: 13px;
                        }

                        .boton-anular {
                            background-color: #8b0000;
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
                                <div id="productosPedido">

                                    <div class="producto-pedido">
                                        <select name="productoId" required>
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

                                        <input type="number" name="cantidad" min="1" placeholder="Cantidad" required>

                                        <button type="button" class="boton-quitar" onclick="quitarProducto(this)">Quitar</button>
                                    </div>

                                </div>

                                <button type="button" class="boton-agregar" onclick="agregarProducto()">Agregar otro producto</button>
                                <button type="submit">Crear pedido</button>
                            </form>
                        </section>

                        <section>
                            <h2>Historial de pedidos</h2>

                            <table>
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Fecha</th>
                                        <th>Estado</th>
                                        <th>Total</th>
                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                """);

        for (PedidoCafeteria pedido : pedidos) {
            html.append("<tr>");
            html.append("<td>").append(pedido.getId()).append("</td>");
            html.append("<td>").append(pedido.getFechaPedido()).append("</td>");
            html.append("<td>").append(pedido.getEstado()).append("</td>");
            html.append("<td>$").append(pedido.getTotal()).append("</td>");

            html.append("<td class='acciones'>");

            html.append("<form method='post' action='/cafeteria/pedidos/estado'>");
            html.append("<input type='hidden' name='pedidoId' value='").append(pedido.getId()).append("'>");
            html.append("<select name='estado'>");
            html.append("<option value='PENDIENTE'>PENDIENTE</option>");
            html.append("<option value='EN_PREPARACION'>EN_PREPARACION</option>");
            html.append("<option value='LISTO'>LISTO</option>");
            html.append("<option value='ENTREGADO'>ENTREGADO</option>");
            html.append("</select>");
            html.append("<button type='submit'>Cambiar</button>");
            html.append("</form>");

            html.append("<form method='post' action='/cafeteria/pedidos/anular'>");
            html.append("<input type='hidden' name='pedidoId' value='").append(pedido.getId()).append("'>");
            html.append("<button class='boton-anular' type='submit'>Anular</button>");
            html.append("</form>");

            html.append("</td>");
            html.append("</tr>");
        }

        html.append("""
                                </tbody>
                            </table>
                        </section>

                        <script>
                            function agregarProducto() {
                                const contenedor = document.getElementById("productosPedido");
                                const primeraFila = document.querySelector(".producto-pedido");
                                const nuevaFila = primeraFila.cloneNode(true);

                                nuevaFila.querySelector("input").value = "";

                                contenedor.appendChild(nuevaFila);
                            }

                            function quitarProducto(boton) {
                                const contenedor = document.getElementById("productosPedido");
                                const filas = contenedor.querySelectorAll(".producto-pedido");

                                if (filas.length > 1) {
                                    boton.parentElement.remove();
                                } else {
                                    alert("El pedido debe tener al menos un producto.");
                                }
                            }
                        </script>
                    </main>
                </body>
                </html>
                """);

        return html.toString();
    }
}