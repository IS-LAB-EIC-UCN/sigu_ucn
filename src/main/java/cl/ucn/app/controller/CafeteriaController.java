package cl.ucn.app.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import cl.ucn.app.model.CategoriaCafeteria;
import cl.ucn.app.model.DetallePedidoCafeteria;
import cl.ucn.app.model.PedidoCafeteria;
import cl.ucn.app.model.ProductoCafeteria;
import cl.ucn.app.repository.CategoriaCafeteriaRepository;
import cl.ucn.app.repository.DetallePedidoCafeteriaRepository;
import cl.ucn.app.repository.PedidoCafeteriaRepository;
import cl.ucn.app.repository.ProductoCafeteriaRepository;
import cl.ucn.app.service.PedidoCafeteriaService;
import io.javalin.http.Context;

public class CafeteriaController {

    private final ProductoCafeteriaRepository productoRepository;
    private final PedidoCafeteriaService pedidoService;
    private final PedidoCafeteriaRepository pedidoRepository;
    private final CategoriaCafeteriaRepository categoriaRepository;
    private final DetallePedidoCafeteriaRepository detalleRepository;

    public CafeteriaController() {
        this.productoRepository = new ProductoCafeteriaRepository();
        this.pedidoService = new PedidoCafeteriaService();
        this.pedidoRepository = new PedidoCafeteriaRepository();
        this.categoriaRepository = new CategoriaCafeteriaRepository();
        this.detalleRepository = new DetallePedidoCafeteriaRepository();
    }

    public void mostrarCafeteria(Context ctx) {
        String fechaInicioTexto = ctx.queryParam("fechaInicio");
        String fechaFinTexto = ctx.queryParam("fechaFin");
        String pedidoIdTexto = ctx.queryParam("pedidoId");

        List<ProductoCafeteria> productos = productoRepository.listar();
        List<CategoriaCafeteria> categorias = categoriaRepository.listar();
        List<PedidoCafeteria> pedidos;

        if (fechaInicioTexto != null && !fechaInicioTexto.isEmpty()
                && fechaFinTexto != null && !fechaFinTexto.isEmpty()) {

            LocalDate fechaInicio = LocalDate.parse(fechaInicioTexto);
            LocalDate fechaFin = LocalDate.parse(fechaFinTexto);

            pedidos = pedidoRepository.listarPorFechas(
                    fechaInicio.atStartOfDay(),
                    fechaFin.atTime(23, 59, 59)
            );

        } else {
            pedidos = pedidoRepository.listar();
        }

        List<DetallePedidoCafeteria> detallesPedido = new ArrayList<>();
        Long pedidoSeleccionadoId = null;

        if (pedidoIdTexto != null && !pedidoIdTexto.isEmpty()) {
            pedidoSeleccionadoId = Long.parseLong(pedidoIdTexto);
            detallesPedido = detalleRepository.listarPorPedido(pedidoSeleccionadoId);
        }

        String html = construirPagina(
                productos,
                pedidos,
                categorias,
                detallesPedido,
                pedidoSeleccionadoId,
                fechaInicioTexto,
                fechaFinTexto,
                "",
                ""
        );

        ctx.contentType("text/html; charset=UTF-8");
        ctx.html(html);
    }

    public void registrarCategoria(Context ctx) {
        String mensaje = "";
        String error = "";

        try {
            String nombre = ctx.formParam("nombreCategoria");

            if (nombre == null || nombre.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío.");
            }

            CategoriaCafeteria categoria = new CategoriaCafeteria(nombre.trim());
            categoriaRepository.guardar(categoria);

            mensaje = "Categoría registrada correctamente.";

        } catch (Exception e) {
            error = e.getMessage();
        }

        cargarPagina(ctx, mensaje, error);
    }

    public void registrarProducto(Context ctx) {
        String mensaje = "";
        String error = "";

        try {
            String nombre = ctx.formParam("nombreProducto");
            BigDecimal precio = new BigDecimal(ctx.formParam("precioProducto"));
            int stock = Integer.parseInt(ctx.formParam("stockProducto"));
            Long categoriaId = Long.parseLong(ctx.formParam("categoriaId"));

            if (nombre == null || nombre.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre del producto no puede estar vacío.");
            }

            if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El precio debe ser mayor a cero.");
            }

            if (stock < 0) {
                throw new IllegalArgumentException("El stock no puede ser negativo.");
            }

            CategoriaCafeteria categoria = categoriaRepository.buscarPorId(categoriaId);

            if (categoria == null) {
                throw new IllegalArgumentException("La categoría seleccionada no existe.");
            }

            ProductoCafeteria producto = new ProductoCafeteria(
                    nombre.trim(),
                    precio,
                    stock,
                    categoria
            );

            productoRepository.guardar(producto);

            mensaje = "Producto registrado correctamente.";

        } catch (Exception e) {
            error = e.getMessage();
        }

        cargarPagina(ctx, mensaje, error);
    }

    public void agregarStock(Context ctx) {
        String mensaje = "";
        String error = "";

        try {
            Long productoId = Long.parseLong(ctx.formParam("productoIdStock"));
            int cantidad = Integer.parseInt(ctx.formParam("cantidadStock"));

            if (cantidad <= 0) {
                throw new IllegalArgumentException("La cantidad de stock debe ser mayor a cero.");
            }

            ProductoCafeteria producto = productoRepository.buscarPorId(productoId);

            if (producto == null) {
                throw new IllegalArgumentException("El producto seleccionado no existe.");
            }

            producto.setStock(producto.getStock() + cantidad);
            productoRepository.actualizar(producto);

            mensaje = "Stock agregado correctamente.";

        } catch (Exception e) {
            error = e.getMessage();
        }

        cargarPagina(ctx, mensaje, error);
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

        cargarPagina(ctx, mensaje, error);
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

        cargarPagina(ctx, mensaje, error);
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

        cargarPagina(ctx, mensaje, error);
    }

    public void eliminarPedido(Context ctx) {
        String mensaje = "";
        String error = "";

        try {
            Long pedidoId = Long.parseLong(ctx.formParam("pedidoId"));

            detalleRepository.eliminarPorPedido(pedidoId);
            pedidoRepository.eliminar(pedidoId);

            mensaje = "Pedido eliminado correctamente.";

        } catch (Exception e) {
            error = e.getMessage();
        }

        cargarPagina(ctx, mensaje, error);
    }

    private void cargarPagina(Context ctx, String mensaje, String error) {
        List<ProductoCafeteria> productos = productoRepository.listar();
        List<PedidoCafeteria> pedidos = pedidoRepository.listar();
        List<CategoriaCafeteria> categorias = categoriaRepository.listar();
        List<DetallePedidoCafeteria> detallesPedido = new ArrayList<>();

        String html = construirPagina(
                productos,
                pedidos,
                categorias,
                detallesPedido,
                null,
                "",
                "",
                mensaje,
                error
        );

        ctx.contentType("text/html; charset=UTF-8");
        ctx.html(html);
    }

    private String construirPagina(
            List<ProductoCafeteria> productos,
            List<PedidoCafeteria> pedidos,
            List<CategoriaCafeteria> categorias,
            List<DetallePedidoCafeteria> detallesPedido,
            Long pedidoSeleccionadoId,
            String fechaInicioTexto,
            String fechaFinTexto,
            String mensaje,
            String error) {

        StringBuilder html = new StringBuilder();

        html.append("""
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <title>Cafetería SIGU-UCN</title>
                    <style>
                        :root {
                            --morado-oscuro: #5A174E;
                            --vino: #9B0B43;
                            --fucsia: #D0003F;
                            --naranjo: #FF5134;
                            --amarillo: #FFC400;
                            --fondo: #FFF8E6;
                            --blanco: #FFFFFF;
                            --texto: #1F1F1F;
                        }

                        body {
                            font-family: Arial, sans-serif;
                            background-color: var(--fondo);
                            margin: 0;
                            color: var(--texto);
                        }

                        header {
                            background: linear-gradient(
                                90deg,
                                var(--morado-oscuro),
                                var(--vino),
                                var(--fucsia),
                                var(--naranjo),
                                var(--amarillo)
                            );
                            color: white;
                            padding: 18px 30px;
                            display: flex;
                            justify-content: space-between;
                            align-items: center;
                        }

                        header h1 {
                            margin: 0;
                            font-size: 34px;
                        }

                        header p {
                            margin: 5px 0 0 0;
                            font-size: 17px;
                        }

                        .boton-menu {
                            background-color: var(--blanco);
                            color: var(--morado-oscuro);
                            border: none;
                            font-size: 28px;
                            padding: 8px 14px;
                            border-radius: 6px;
                            cursor: pointer;
                        }

                        .boton-menu:hover {
                            background-color: var(--amarillo);
                            color: var(--morado-oscuro);
                        }

                        main {
                            width: 85%;
                            margin: 30px auto;
                        }

                        section {
                            background-color: var(--blanco);
                            padding: 20px;
                            margin-bottom: 25px;
                            border-radius: 10px;
                            border-left: 8px solid var(--fucsia);
                            box-shadow: 0 3px 8px rgba(0, 0, 0, 0.08);
                        }

                        section h2 {
                            color: var(--morado-oscuro);
                            margin-top: 0;
                        }

                        table {
                            width: 100%;
                            border-collapse: collapse;
                            margin-top: 15px;
                        }

                        th, td {
                            border: 1px solid #ddd;
                            padding: 10px;
                            text-align: center;
                        }

                        th {
                            background-color: var(--vino);
                            color: white;
                        }

                        tr:nth-child(even) {
                            background-color: #fff0d4;
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

                        select, input {
                            border: 1px solid #ccc;
                            border-radius: 4px;
                        }

                        button {
                            background-color: var(--vino);
                            color: white;
                            border: none;
                            cursor: pointer;
                            border-radius: 4px;
                            font-weight: bold;
                        }

                        button:hover {
                            background-color: var(--fucsia);
                        }

                        a {
                            color: var(--vino);
                            font-weight: bold;
                            text-decoration: none;
                        }

                        a:hover {
                            color: var(--fucsia);
                            text-decoration: underline;
                        }

                        .mensaje {
                            background-color: #fff3cd;
                            color: var(--morado-oscuro);
                            padding: 12px;
                            border-radius: 6px;
                            margin-bottom: 15px;
                            border-left: 6px solid var(--amarillo);
                        }

                        .error {
                            background-color: #ffe1dc;
                            color: var(--vino);
                            padding: 12px;
                            border-radius: 6px;
                            margin-bottom: 15px;
                            border-left: 6px solid var(--naranjo);
                        }

                        .producto-pedido {
                            display: grid;
                            grid-template-columns: 2fr 1fr auto;
                            gap: 10px;
                            margin-bottom: 12px;
                            align-items: center;
                            background-color: #fff0d4;
                            padding: 12px;
                            border-radius: 6px;
                            border-left: 5px solid var(--amarillo);
                        }

                        .boton-agregar {
                            background-color: var(--naranjo);
                        }

                        .boton-agregar:hover {
                            background-color: var(--fucsia);
                        }

                        .boton-quitar,
                        .boton-anular,
                        .boton-eliminar {
                            background-color: var(--morado-oscuro);
                        }

                        .boton-quitar:hover,
                        .boton-anular:hover,
                        .boton-eliminar:hover {
                            background-color: var(--vino);
                        }

                        .acciones {
                            display: flex;
                            gap: 8px;
                            justify-content: center;
                            flex-wrap: wrap;
                        }

                        .acciones form {
                            display: inline;
                        }

                        .acciones select,
                        .acciones button {
                            padding: 6px;
                            font-size: 13px;
                        }

                        .filtro-fechas {
                            display: grid;
                            grid-template-columns: 1fr 1fr auto auto;
                            gap: 10px;
                            align-items: end;
                            background-color: #fff0d4;
                            padding: 12px;
                            border-radius: 6px;
                            border-left: 5px solid var(--amarillo);
                        }

                        .filtro-fechas a {
                            padding: 10px;
                            text-align: center;
                        }

                        .panel-lateral {
                            height: 100%;
                            width: 0;
                            position: fixed;
                            top: 0;
                            right: 0;
                            background-color: var(--blanco);
                            overflow-x: hidden;
                            transition: 0.3s;
                            padding-top: 70px;
                            box-shadow: -4px 0 12px rgba(0, 0, 0, 0.25);
                            z-index: 10;
                        }

                        .panel-lateral.abierto {
                            width: 380px;
                            padding-left: 25px;
                            padding-right: 25px;
                        }

                        .cerrar-menu {
                            position: absolute;
                            top: 20px;
                            right: 25px;
                            font-size: 30px;
                            color: var(--morado-oscuro);
                            cursor: pointer;
                        }

                        .panel-lateral h2 {
                            color: var(--morado-oscuro);
                            border-bottom: 2px solid var(--amarillo);
                            padding-bottom: 8px;
                        }

                        .opcion-menu {
                            font-size: 20px;
                            color: var(--morado-oscuro);
                            padding: 18px 12px;
                            border-bottom: 1px solid #ddd;
                            cursor: pointer;
                            display: flex;
                            justify-content: space-between;
                            align-items: center;
                        }

                        .opcion-menu:hover {
                            background-color: #fff0d4;
                            color: var(--fucsia);
                        }

                        .seccion-pagina {
                            display: none;
                        }

                        .seccion-pagina.activo {
                            display: block;
                        }

                        .titulo-admin {
                            color: var(--vino);
                            margin-bottom: 12px;
                        }
                    </style>
                </head>
                <body>
                    <header>
                        <div>
                            <h1>Módulo de Cafetería</h1>
                            <p>Sistema de gestión de productos y pedidos</p>
                        </div>

                        <button class="boton-menu" onclick="abrirMenu()">☰</button>
                    </header>

                    <div id="panelLateral" class="panel-lateral">
                        <span class="cerrar-menu" onclick="cerrarMenu()">×</span>

                        <h2>Menú cafetería</h2>

                        <div class="opcion-menu" onclick="mostrarSeccion('seccionCrearPedido')">
                            Crear pedido <span>›</span>
                        </div>

                        <div class="opcion-menu" onclick="mostrarSeccion('seccionHistorial')">
                            Historial de pedidos <span>›</span>
                        </div>

                        <div class="opcion-menu" onclick="mostrarSeccion('seccionProductos')">
                            Productos disponibles <span>›</span>
                        </div>

                        <div class="opcion-menu" onclick="mostrarSeccion('formularioProducto')">
                            Registrar producto <span>›</span>
                        </div>

                        <div class="opcion-menu" onclick="mostrarSeccion('formularioCategoria')">
                            Registrar categoría <span>›</span>
                        </div>

                        <div class="opcion-menu" onclick="mostrarSeccion('formularioStock')">
                            Agregar stock <span>›</span>
                        </div>
                    </div>

                    <main>
                """);

        if (mensaje != null && !mensaje.isEmpty()) {
            html.append("<div class='mensaje'>").append(mensaje).append("</div>");
        }

        if (error != null && !error.isEmpty()) {
            html.append("<div class='error'>").append(error).append("</div>");
        }

        html.append("""
                        <section id="seccionCrearPedido" class="seccion-pagina activo">
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

                        <section id="seccionHistorial" class="seccion-pagina activo">
                            <h2>Historial de pedidos</h2>

                            <form method="get" action="/cafeteria" class="filtro-fechas">
                                <div>
                                    <label for="fechaInicio">Fecha inicio:</label>
                                    <input type="date" name="fechaInicio" id="fechaInicio" value='""");

        html.append(fechaInicioTexto == null ? "" : fechaInicioTexto);

        html.append("""
                                    '>
                                </div>

                                <div>
                                    <label for="fechaFin">Fecha fin:</label>
                                    <input type="date" name="fechaFin" id="fechaFin" value='""");

        html.append(fechaFinTexto == null ? "" : fechaFinTexto);

        html.append("""
                                    '>
                                </div>

                                <button type="submit">Filtrar</button>
                                <a href="/cafeteria">Quitar filtro</a>
                            </form>

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

            html.append("<td>");
            html.append("<a href='/cafeteria?pedidoId=").append(pedido.getId());

            if (fechaInicioTexto != null && !fechaInicioTexto.isEmpty()
                    && fechaFinTexto != null && !fechaFinTexto.isEmpty()) {
                html.append("&fechaInicio=").append(fechaInicioTexto);
                html.append("&fechaFin=").append(fechaFinTexto);
            }

            html.append("'>");
            html.append(pedido.getId());
            html.append("</a>");
            html.append("</td>");

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

            html.append("<form method='post' action='/cafeteria/pedidos/eliminar'>");
            html.append("<input type='hidden' name='pedidoId' value='").append(pedido.getId()).append("'>");
            html.append("<button class='boton-eliminar' type='submit' onclick='return confirm(\"¿Seguro que deseas eliminar este pedido?\")'>Eliminar</button>");
            html.append("</form>");

            html.append("</td>");
            html.append("</tr>");
        }

        html.append("""
                                </tbody>
                            </table>
                        </section>
                """);

        if (pedidoSeleccionadoId != null) {
            html.append("""
                        <section id="seccionDetallePedido" class="seccion-pagina activo">
                            <h2>Productos del pedido #""")
                    .append(pedidoSeleccionadoId)
                    .append("""
                            </h2>

                            <table>
                                <thead>
                                    <tr>
                                        <th>Producto</th>
                                        <th>Cantidad</th>
                                        <th>Subtotal</th>
                                    </tr>
                                </thead>
                                <tbody>
                    """);

            for (DetallePedidoCafeteria detalle : detallesPedido) {
                html.append("<tr>");
                html.append("<td>").append(detalle.getProducto().getNombre()).append("</td>");
                html.append("<td>").append(detalle.getCantidad()).append("</td>");
                html.append("<td>$").append(detalle.getSubtotal()).append("</td>");
                html.append("</tr>");
            }

            html.append("""
                                </tbody>
                            </table>
                        </section>
                    """);
        }

        html.append("""
                        <section id="seccionProductos" class="seccion-pagina">
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

                        <section id="formularioProducto" class="seccion-pagina">
                            <h2 class="titulo-admin">Registrar producto</h2>

                            <form method="post" action="/cafeteria/productos">
                                <label for="nombreProducto">Nombre del producto:</label>
                                <input type="text" name="nombreProducto" id="nombreProducto" required>

                                <label for="precioProducto">Precio:</label>
                                <input type="number" name="precioProducto" id="precioProducto" min="1" required>

                                <label for="stockProducto">Stock inicial:</label>
                                <input type="number" name="stockProducto" id="stockProducto" min="0" required>

                                <label for="categoriaId">Categoría:</label>
                                <select name="categoriaId" id="categoriaId" required>
                """);

        for (CategoriaCafeteria categoria : categorias) {
            html.append("<option value='")
                    .append(categoria.getId())
                    .append("'>")
                    .append(categoria.getNombre())
                    .append("</option>");
        }

        html.append("""
                                </select>

                                <button type="submit">Registrar producto</button>
                            </form>
                        </section>

                        <section id="formularioCategoria" class="seccion-pagina">
                            <h2 class="titulo-admin">Registrar categoría</h2>

                            <form method="post" action="/cafeteria/categorias">
                                <label for="nombreCategoria">Nombre de la categoría:</label>
                                <input type="text" name="nombreCategoria" id="nombreCategoria" required>

                                <button type="submit">Registrar categoría</button>
                            </form>
                        </section>

                        <section id="formularioStock" class="seccion-pagina">
                            <h2 class="titulo-admin">Agregar stock</h2>

                            <form method="post" action="/cafeteria/productos/stock">
                                <label for="productoIdStock">Producto:</label>
                                <select name="productoIdStock" id="productoIdStock" required>
                """);

        for (ProductoCafeteria producto : productos) {
            html.append("<option value='")
                    .append(producto.getId())
                    .append("'>")
                    .append(producto.getNombre())
                    .append(" - Stock actual: ")
                    .append(producto.getStock())
                    .append("</option>");
        }

        html.append("""
                                </select>

                                <label for="cantidadStock">Cantidad a agregar:</label>
                                <input type="number" name="cantidadStock" id="cantidadStock" min="1" required>

                                <button type="submit">Agregar stock</button>
                            </form>
                        </section>

                        <script>
                            function abrirMenu() {
                                document.getElementById("panelLateral").classList.add("abierto");
                            }

                            function cerrarMenu() {
                                document.getElementById("panelLateral").classList.remove("abierto");
                            }

                            function mostrarSeccion(id) {
                                cerrarMenu();

                                const secciones = document.querySelectorAll(".seccion-pagina");

                                secciones.forEach(function(seccion) {
                                    seccion.classList.remove("activo");
                                });

                                const seccionSeleccionada = document.getElementById(id);
                                seccionSeleccionada.classList.add("activo");

                                seccionSeleccionada.scrollIntoView({ behavior: "smooth" });
                            }

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

                            window.onload = function() {
                                const detalle = document.getElementById("seccionDetallePedido");

                                if (detalle != null) {
                                    const secciones = document.querySelectorAll(".seccion-pagina");

                                    secciones.forEach(function(seccion) {
                                        seccion.classList.remove("activo");
                                    });

                                    detalle.classList.add("activo");
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