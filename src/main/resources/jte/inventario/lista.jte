@import java.util.List
@import java.util.Set
@import cl.ucn.app.model.Producto
@import cl.ucn.app.model.Categoria
@import cl.ucn.app.model.Insumo

@param String title
@param String usuarioNombre
@param String rol
@param String flashOk = null
@param String flashError = null
@param List<Producto> productos
@param List<Categoria> categorias
@param Long categoriaSeleccionada = null
@param String texto = ""
@param List<Insumo> alertas
@param Set<Long> idsStockBajo
@param boolean hayFiltros = false

@template.inventario.layout(
title = title, usuarioNombre = usuarioNombre, rol = rol,
flashOk = flashOk, flashError = flashError,
content = @`
    @if(!alertas.isEmpty())
        <div class="inv-alerta inv-alerta-aviso">
            <strong>Alerta de stock bajo (${alertas.size()}):</strong>
            <ul>
                @for(Insumo i : alertas)
                    <li>${i.getCodigo()} - ${i.getNombre()}: stock ${i.getStockActual()}, mínimo ${i.getStockMinimo()}</li>
                @endfor
            </ul>
        </div>
    @endif

    <form method="get" action="/inventario" class="inv-filtros">
        <div class="form-group">
            <label for="categoria">Categoría</label>
            <select id="categoria" name="categoria">
                <option value="">Todas</option>
                @for(Categoria c : categorias)
                    <option value="${c.getId()}" selected="${c.getId().equals(categoriaSeleccionada)}">${c.getNombre()}</option>
                @endfor
            </select>
        </div>
        <div class="form-group">
            <label for="q">Buscar por nombre o código</label>
            <input type="text" id="q" name="q" value="${texto}" maxlength="100" placeholder="Ej: microscopio, INS-001">
        </div>
        <button type="submit" class="inv-btn">Filtrar</button>
        @if(hayFiltros)
            <a href="/inventario" class="inv-btn inv-btn-secundario">Quitar filtros</a>
        @endif
    </form>

    @if(productos.isEmpty())
        <p class="inv-vacio">
            @if(hayFiltros)
                No existen resultados para el filtro seleccionado.
            @else
                El inventario está vacío.
            @endif
        </p>
    @else
        <table class="inv-tabla">
            <thead>
            <tr>
                <th>Código</th>
                <th>Nombre</th>
                <th>Tipo</th>
                <th>Categoría</th>
                <th>Disponibilidad / stock</th>
                <th>Proveedor</th>
            </tr>
            </thead>
            <tbody>
            @for(Producto p : productos)
                <tr class="${idsStockBajo.contains(p.getId()) ? "inv-fila-alerta" : null}">
                    <td>${p.getCodigo()}</td>
                    <td>
                        ${p.getNombre()}
                        @if(p.getDescripcion() != null && !p.getDescripcion().isBlank())
                            <br><small>${p.getDescripcion()}</small>
                        @endif
                    </td>
                    <td><span class="inv-badge">${p.getTipoRecurso()}</span></td>
                    <td>${p.getCategoria().getNombre()}</td>
                    <td>${p.getDisponibilidad()}</td>
                    <td>${p.getProveedor() != null ? p.getProveedor().getNombre() : "—"}</td>
                </tr>
            @endfor
            </tbody>
        </table>
        <p class="inv-total">${productos.size()} producto(s)</p>
    @endif
`
)