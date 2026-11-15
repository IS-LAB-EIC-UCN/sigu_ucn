@import cl.ucn.app.model.Categoria
@import cl.ucn.app.model.Pedido
@import cl.ucn.app.model.Producto
@import java.util.List

@param String title
@param String usuarioNombre
@param String rol
@param String mensaje = ""
@param String error = ""
@param boolean puedeCatalogo = false
@param boolean puedeStock = false
@param boolean puedePedidos = false
@param boolean puedeEstado = false
@param boolean puedeVentas = false
@param List<Categoria> categorias
@param List<Producto> productos
@param List<Pedido> pedidos
@param List<Pedido> ventas
@param boolean hayFiltro = false
@param String desde = ""
@param String hasta = ""

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <section class="dashboard">
        <h1>Cafetería</h1>
        <p>Rol actual: <strong>${rol}</strong></p>

        @if(!mensaje.isBlank())
            <div class="info-card"><strong>${mensaje}</strong></div>
        @endif
        @if(!error.isBlank())
            <div class="alert-error">${error}</div>
        @endif

        <style>
            .caf-table { border-collapse: collapse; width: 100%; background: #fff; margin-bottom: 16px; }
            .caf-table th, .caf-table td { border: 1px solid #c9d2df; padding: 6px 10px; text-align: left; }
            .caf-inline { display: inline-flex; gap: 6px; align-items: center; margin: 2px 0; }
            .caf-inline input[type=number] { width: 80px; }
        </style>

        <h2>Categorías</h2>
        <table class="caf-table">
            <tr><th>Nombre</th><th>Descripción</th></tr>
            @for(var c : categorias)
                <tr><td>${c.getNombre()}</td><td>${c.getDescripcion()}</td></tr>
            @endfor
        </table>
        @if(puedeCatalogo)
            <form method="post" action="/cafeteria/categorias" class="caf-inline">
                <input type="text" name="nombre" placeholder="Nombre" required>
                <input type="text" name="descripcion" placeholder="Descripción">
                <button type="submit" class="primary-btn">Agregar categoría</button>
            </form>
        @endif

        <h2>Productos</h2>
        <table class="caf-table">
            <tr><th>Nombre</th><th>Categoría</th><th>Precio (CLP)</th><th>Stock</th>@if(puedeStock)<th>Actualizar stock</th>@endif</tr>
            @for(var p : productos)
                <tr>
                    <td>${p.getNombre()}</td>
                    <td>${p.getCategoria().getNombre()}</td>
                    <td>${p.getPrecio().toPlainString()}</td>
                    <td>${p.getStock()}</td>
                    @if(puedeStock)
                        <td>
                            <form method="post" action="/cafeteria/productos/${p.getId()}/stock" class="caf-inline">
                                <input type="number" name="stock" min="0" value="${p.getStock()}" required>
                                <button type="submit" class="primary-btn">Guardar</button>
                            </form>
                        </td>
                    @endif
                </tr>
            @endfor
        </table>
        @if(puedeCatalogo)
            <form method="post" action="/cafeteria/productos" class="caf-inline">
                <input type="text" name="nombre" placeholder="Nombre" required>
                <input type="number" name="precio" min="0" step="0.01" placeholder="Precio" required>
                <input type="number" name="stock" min="0" placeholder="Stock" required>
                <select name="categoriaId" required>
                    <option value="">Categoría</option>
                    @for(var c : categorias)
                        <option value="${c.getId()}">${c.getNombre()}</option>
                    @endfor
                </select>
                <button type="submit" class="primary-btn">Agregar producto</button>
            </form>
        @endif

        @if(puedePedidos)
            <h2>Nuevo pedido</h2>
            <form method="post" action="/cafeteria/pedidos">
                <table class="caf-table">
                    <tr><th>Producto</th><th>Cantidad</th></tr>
                    @for(var p : productos)
                        <tr>
                            <td>${p.getNombre()} (stock ${p.getStock()})</td>
                            <td>
                                <input type="hidden" name="productoId" value="${p.getId()}">
                                <input type="number" name="cantidad" min="0" value="0">
                            </td>
                        </tr>
                    @endfor
                </table>
                <button type="submit" class="primary-btn">Crear pedido</button>
            </form>
        @endif

        <h2>Historial de pedidos</h2>
        <table class="caf-table">
            <tr><th>N°</th><th>Fecha</th><th>Usuario</th><th>Estado</th><th>Total (CLP)</th><th>Acciones</th></tr>
            @for(var pe : pedidos)
                <tr>
                    <td>${pe.getId()}</td>
                    <td>${pe.getFecha().toString().replace('T', ' ')}</td>
                    <td>@if(pe.getUsuario() != null)${pe.getUsuario().getNombre()}@endif</td>
                    <td>${pe.getEstado()}</td>
                    <td>${pe.getTotal().toPlainString()}</td>
                    <td>
                        @if(puedeEstado)
                            @if(!pe.getEstado().equals("ENTREGADO") && !pe.getEstado().equals("ANULADO"))
                                <form method="post" action="/cafeteria/pedidos/${pe.getId()}/avanzar" class="caf-inline">
                                    <button type="submit" class="primary-btn">Avanzar estado</button>
                                </form>
                            @endif
                        @endif
                        @if(puedePedidos)
                            <form method="post" action="/cafeteria/pedidos/${pe.getId()}/productos" class="caf-inline">
                                <select name="productoId">
                                    @for(var p : productos)
                                        <option value="${p.getId()}">${p.getNombre()}</option>
                                    @endfor
                                </select>
                                <input type="number" name="cantidad" min="1" value="1">
                                <button type="submit" class="primary-btn">Agregar</button>
                            </form>
                            <form method="post" action="/cafeteria/pedidos/${pe.getId()}/anular" class="caf-inline">
                                <button type="submit" class="primary-btn">Anular</button>
                            </form>
                        @endif
                    </td>
                </tr>
            @endfor
        </table>

        @if(puedeVentas)
            <h2>Ventas por rango de fechas</h2>
            <form method="get" action="/cafeteria/ventas" class="caf-inline">
                <label>Desde <input type="date" name="desde" value="${desde}" required></label>
                <label>Hasta <input type="date" name="hasta" value="${hasta}" required></label>
                <button type="submit" class="primary-btn">Filtrar</button>
            </form>
            @if(hayFiltro)
                <table class="caf-table">
                    <tr><th>N°</th><th>Fecha</th><th>Estado</th><th>Total (CLP)</th></tr>
                    @for(var v : ventas)
                        <tr>
                            <td>${v.getId()}</td>
                            <td>${v.getFecha().toString().replace('T', ' ')}</td>
                            <td>${v.getEstado()}</td>
                            <td>${v.getTotal().toPlainString()}</td>
                        </tr>
                    @endfor
                </table>
                @if(ventas.isEmpty())
                    <p>No hay ventas en ese rango de fechas.</p>
                @endif
            @endif
        @endif
    </section>
`
)
