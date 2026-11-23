@import cl.ucn.app.model.Categoria
@import cl.ucn.app.model.Pedido
@import cl.ucn.app.model.Producto
@import java.math.BigDecimal
@import java.time.format.DateTimeFormatter
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
        <div class="caf-header">
            <div>
                <h1>Gestión de Cafetería</h1>
                <p class="caf-subtitle">Panel de catálogo, control de stock, pedidos y ventas</p>
            </div>
            <span class="caf-role-badge">Rol actual: <strong>${rol}</strong></span>
        </div>

        @if(!mensaje.isBlank())
            <div class="alert-success"><strong>${mensaje}</strong></div>
        @endif
        @if(!error.isBlank())
            <div class="alert-error">${error}</div>
        @endif

        <%-- Navegación rapida --%>
        <nav class="caf-nav">
            @if(puedePedidos)
                <a href="#sec-nuevo-pedido" class="caf-pill">+ Nuevo Pedido</a>
            @endif
            <a href="#sec-pedidos" class="caf-pill">Historial de Pedidos (${pedidos.size()})</a>
            <a href="#sec-catalogo" class="caf-pill">Productos y Stock (${productos.size()})</a>
            @if(puedeVentas)
                <a href="#sec-ventas" class="caf-pill">Ventas por Fecha</a>
            @endif
        </nav>

        <%-- Resumen KPIs --%>
        <div class="card-grid">
            <div class="info-card">
                <span class="caf-kpi-label">Categorías</span>
                <div class="caf-kpi-value">${categorias.size()}</div>
            </div>
            <div class="info-card">
                <span class="caf-kpi-label">Productos en Catálogo</span>
                <div class="caf-kpi-value">${productos.size()}</div>
            </div>
            <div class="info-card">
                <span class="caf-kpi-label">Pedidos Activos</span>
                <div class="caf-kpi-value">
                    ${pedidos.stream().filter(p -> !"ENTREGADO".equals(p.getEstado()) && !"ANULADO".equals(p.getEstado())).count()}
                </div>
            </div>
            <div class="info-card">
                <span class="caf-kpi-label">Total Pedidos Registrados</span>
                <div class="caf-kpi-value">${pedidos.size()}</div>
            </div>
        </div>

        <%-- Sección: Nuevo pedido --%>
        @if(puedePedidos)
            <div id="sec-nuevo-pedido" class="caf-card">
                <h2>Nuevo pedido</h2>
                <form method="post" action="/cafeteria/pedidos" id="form-crear-pedido">
                    <div class="caf-table-wrap">
                        <table class="caf-table">
                            <thead>
                                <tr>
                                    <th>Producto</th>
                                    <th>Categoría</th>
                                    <th>Precio (CLP)</th>
                                    <th>Stock Disponible</th>
                                    <th>Cantidad</th>
                                </tr>
                            </thead>
                            <tbody>
                                @for(var p : productos)
                                    <tr>
                                        <td><strong>${p.getNombre()}</strong></td>
                                        <td>${p.getCategoria().getNombre()}</td>
                                        <td>$${p.getPrecio().toPlainString()}</td>
                                        <td>
                                            @if(p.getStock() == 0)
                                                <span class="badge-stock stock-agotado">Sin stock</span>
                                            @elseif(p.getStock() <= 5)
                                                <span class="badge-stock stock-bajo">${p.getStock()} unid.</span>
                                            @else
                                                <span class="badge-stock stock-ok">${p.getStock()} unid.</span>
                                            @endif
                                        </td>
                                        <td>
                                            <input type="hidden" name="productoId" value="${p.getId()}">
                                            <input type="number"
                                                   name="cantidad"
                                                   class="caf-input caf-input-qty inp-cant-pedido"
                                                   data-precio="${p.getPrecio().toPlainString()}"
                                                   min="0"
                                                   max="${p.getStock()}"
                                                   value="0"
                                                   readonly="${p.getStock() == 0}">
                                        </td>
                                    </tr>
                                @endfor
                            </tbody>
                        </table>
                    </div>
                    <div class="caf-order-footer">
                        <div class="caf-total-box">
                            Total estimado del pedido: <strong id="total-estimado-pedido">$0.00</strong>
                        </div>
                        <button type="submit" class="caf-btn">Crear pedido</button>
                    </div>
                </form>
            </div>
        @endif

        <%-- Sección: Historial de pedidos y detalle de acciones --%>
        <div id="sec-pedidos" class="caf-card">
            <h2>Historial de pedidos y estados</h2>
            @if(pedidos.isEmpty())
                <p class="caf-muted">No hay pedidos registrados todavía.</p>
            @else
                <div class="caf-table-wrap">
                    <table class="caf-table">
                        <thead>
                            <tr>
                                <th>N°</th>
                                <th>Fecha</th>
                                <th>Usuario</th>
                                <th>Estado</th>
                                <th>Total (CLP)</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            @for(var pe : pedidos)
                                <tr>
                                    <td><strong>#${pe.getId()}</strong></td>
                                    <td>${pe.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}</td>
                                    <td>@if(pe.getUsuario() != null)${pe.getUsuario().getNombre()}@else - @endif</td>
                                    <td>
                                        <span class="badge-estado estado-${pe.getEstado().toLowerCase()}">
                                            ${pe.getEstado().replace("_", " ")}
                                        </span>
                                    </td>
                                    <td><strong>$${pe.getTotal().toPlainString()}</strong></td>
                                    <td>
                                        <div class="caf-actions-group">
                                            @if(puedeEstado && !pe.getEstado().equals("ENTREGADO") && !pe.getEstado().equals("ANULADO"))
                                                <form method="post" action="/cafeteria/pedidos/${pe.getId()}/avanzar" class="caf-inline">
                                                    <button type="submit" class="caf-btn caf-btn-sm caf-btn-info">
                                                        @if(pe.getEstado().equals("PENDIENTE"))
                                                            Preparar
                                                        @elseif(pe.getEstado().equals("EN_PREPARACION"))
                                                            Marcar Listo
                                                        @else
                                                            Entregar
                                                        @endif
                                                    </button>
                                                </form>
                                            @endif

                                            @if(puedePedidos && pe.getEstado().equals("PENDIENTE"))
                                                <form method="post" action="/cafeteria/pedidos/${pe.getId()}/productos" class="caf-inline">
                                                    <select name="productoId" class="caf-select">
                                                        @for(var p : productos)
                                                            @if(p.getStock() > 0)
                                                                <option value="${p.getId()}">${p.getNombre()} (stock ${p.getStock()})</option>
                                                            @endif
                                                        @endfor
                                                    </select>
                                                    <input type="number" name="cantidad" min="1" value="1" class="caf-input caf-input-qty">
                                                    <button type="submit" class="caf-btn caf-btn-sm caf-btn-secondary">+ Agregar</button>
                                                </form>
                                            @endif

                                            @if(puedePedidos && !pe.getEstado().equals("ENTREGADO") && !pe.getEstado().equals("ANULADO"))
                                                <form method="post" action="/cafeteria/pedidos/${pe.getId()}/anular" class="caf-inline"
                                                      onsubmit="return confirm('¿Seguro que desea anular el pedido #${pe.getId()}?');">
                                                    <button type="submit" class="caf-btn caf-btn-sm caf-btn-danger">Anular</button>
                                                </form>
                                            @endif

                                            @if(pe.getEstado().equals("ENTREGADO") || pe.getEstado().equals("ANULADO"))
                                                <span class="caf-muted">Finalizado</span>
                                            @endif
                                        </div>
                                    </td>
                                </tr>
                            @endfor
                        </tbody>
                    </table>
                </div>
            @endif
        </div>

        <%-- Sección: Catálogo (Categorias y Productos) --%>
        <div id="sec-catalogo" class="caf-card">
            <h2>Categorías</h2>
            <div class="caf-table-wrap">
                <table class="caf-table">
                    <thead>
                        <tr>
                            <th>Nombre</th>
                            <th>Descripción</th>
                        </tr>
                    </thead>
                    <tbody>
                        @for(var c : categorias)
                            <tr>
                                <td><strong>${c.getNombre()}</strong></td>
                                <td>${c.getDescripcion()}</td>
                            </tr>
                        @endfor
                    </tbody>
                </table>
            </div>
            @if(puedeCatalogo)
                <div class="caf-form-box">
                    <h3>Registrar nueva categoría</h3>
                    <form method="post" action="/cafeteria/categorias" class="caf-inline">
                        <input type="text" name="nombre" placeholder="Nombre de categoría" class="caf-input" required>
                        <input type="text" name="descripcion" placeholder="Descripción" class="caf-input">
                        <button type="submit" class="caf-btn">Agregar categoría</button>
                    </form>
                </div>
            @endif

            <h2 style="margin-top: 28px;">Productos y Control de Stock</h2>
            <div class="caf-table-wrap">
                <table class="caf-table">
                    <thead>
                        <tr>
                            <th>Nombre</th>
                            <th>Categoría</th>
                            <th>Precio (CLP)</th>
                            <th>Stock</th>
                            @if(puedeStock)
                                <th>Actualizar stock</th>
                            @endif
                        </tr>
                    </thead>
                    <tbody>
                        @for(var p : productos)
                            <tr>
                                <td><strong>${p.getNombre()}</strong></td>
                                <td>${p.getCategoria().getNombre()}</td>
                                <td>$${p.getPrecio().toPlainString()}</td>
                                <td>
                                    @if(p.getStock() == 0)
                                        <span class="badge-stock stock-agotado">0 (Agotado)</span>
                                    @elseif(p.getStock() <= 5)
                                        <span class="badge-stock stock-bajo">${p.getStock()} unid.</span>
                                    @else
                                        <span class="badge-stock stock-ok">${p.getStock()} unid.</span>
                                    @endif
                                </td>
                                @if(puedeStock)
                                    <td>
                                        <form method="post" action="/cafeteria/productos/${p.getId()}/stock" class="caf-inline">
                                            <input type="number" name="stock" min="0" value="${p.getStock()}" class="caf-input caf-input-qty" required>
                                            <button type="submit" class="caf-btn caf-btn-sm caf-btn-secondary">Guardar</button>
                                        </form>
                                    </td>
                                @endif
                            </tr>
                        @endfor
                    </tbody>
                </table>
            </div>
            @if(puedeCatalogo)
                <div class="caf-form-box">
                    <h3>Registrar nuevo producto</h3>
                    <form method="post" action="/cafeteria/productos" class="caf-inline">
                        <input type="text" name="nombre" placeholder="Nombre del producto" class="caf-input" required>
                        <input type="number" name="precio" min="0" step="0.01" placeholder="Precio" class="caf-input" required>
                        <input type="number" name="stock" min="0" placeholder="Stock inicial" class="caf-input caf-input-qty" required>
                        <select name="categoriaId" class="caf-select" required>
                            <option value="">-- Categoría --</option>
                            @for(var c : categorias)
                                <option value="${c.getId()}">${c.getNombre()}</option>
                            @endfor
                        </select>
                        <button type="submit" class="caf-btn">Agregar producto</button>
                    </form>
                </div>
            @endif
        </div>

        <%-- Sección: Ventas por rango de fechas --%>
        @if(puedeVentas)
            <div id="sec-ventas" class="caf-card">
                <h2>Ventas por rango de fechas</h2>
                <form method="get" action="/cafeteria/ventas" class="caf-inline">
                    <label>Desde <input type="date" name="desde" value="${desde}" class="caf-input" required></label>
                    <label>Hasta <input type="date" name="hasta" value="${hasta}" class="caf-input" required></label>
                    <button type="submit" class="caf-btn">Filtrar</button>
                    @if(hayFiltro)
                        <a href="/cafeteria" class="caf-btn caf-btn-secondary">Limpiar</a>
                    @endif
                </form>

                @if(hayFiltro)
                    <div class="caf-sales-summary">
                        Ventas del <strong>${desde}</strong> al <strong>${hasta}</strong> —
                        Total recaudado: <strong>$${ventas.stream().map(Pedido::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add).toPlainString()}</strong>
                    </div>
                    <div class="caf-table-wrap">
                        <table class="caf-table">
                            <thead>
                                <tr>
                                    <th>N°</th>
                                    <th>Fecha</th>
                                    <th>Estado</th>
                                    <th>Total (CLP)</th>
                                </tr>
                            </thead>
                            <tbody>
                                @for(var v : ventas)
                                    <tr>
                                        <td><strong>#${v.getId()}</strong></td>
                                        <td>${v.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}</td>
                                        <td>
                                            <span class="badge-estado estado-${v.getEstado().toLowerCase()}">
                                                ${v.getEstado().replace("_", " ")}
                                            </span>
                                        </td>
                                        <td><strong>$${v.getTotal().toPlainString()}</strong></td>
                                    </tr>
                                @endfor
                            </tbody>
                        </table>
                    </div>
                    @if(ventas.isEmpty())
                        <p class="caf-muted">No hay ventas en ese rango de fechas.</p>
                    @endif
                @endif
            </div>
        @endif
    </section>

    <script>
        $(function () {
            $('.inp-cant-pedido').on('input change', function () {
                let total = 0;
                $('.inp-cant-pedido').each(function () {
                    const precio = parseFloat($(this).data('precio')) || 0;
                    const cant = parseInt($(this).val(), 10) || 0;
                    if (cant > 0) {
                        total += precio * cant;
                    }
                });
                $('#total-estimado-pedido').text('$' + total.toFixed(2));
            });
        });
    </script>
`
)