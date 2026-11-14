@import java.util.List
@import java.util.Map
@import cl.ucn.app.model.Categoria
@import cl.ucn.app.model.Proveedor

@param String title
@param String usuarioNombre
@param String rol
@param String flashOk = null
@param String flashError = null
@param List<Categoria> categorias
@param List<Proveedor> proveedores
@param Map<String, String> valores
@param String errorFormulario = null

@template.inventario.layout(
title = title, usuarioNombre = usuarioNombre, rol = rol,
flashOk = flashOk, flashError = flashError,
content = @`
    <h2>Registrar producto</h2>

    @if(errorFormulario != null)
        <div class="inv-alerta inv-alerta-error">${errorFormulario}</div>
    @endif

    <form method="post" action="/inventario/productos/nuevo" class="inv-form">
        <div class="form-group">
            <label for="tipo">Tipo de recurso *</label>
            <select id="tipo" name="tipo" required>
                <option value="EQUIPO" selected="${"EQUIPO".equalsIgnoreCase(valores.getOrDefault("tipo", ""))}">Equipo (se presta y se devuelve)</option>
                <option value="INSUMO" selected="${"INSUMO".equalsIgnoreCase(valores.getOrDefault("tipo", ""))}">Insumo (se controla por stock)</option>
            </select>
        </div>

        <div class="form-group">
            <label for="codigo">Código *</label>
            <input type="text" id="codigo" name="codigo" value="${valores.getOrDefault("codigo", "")}"
                   required maxlength="30" pattern="[A-Za-z0-9\-]{2,30}" placeholder="Ej: EQ-005 o INS-010">
        </div>

        <div class="form-group">
            <label for="nombre">Nombre *</label>
            <input type="text" id="nombre" name="nombre" value="${valores.getOrDefault("nombre", "")}"
                   required maxlength="100">
        </div>

        <div class="form-group">
            <label for="descripcion">Descripción</label>
            <textarea id="descripcion" name="descripcion" maxlength="255" rows="2">${valores.getOrDefault("descripcion", "")}</textarea>
        </div>

        <div class="form-group">
            <label for="categoriaId">Categoría *</label>
            <select id="categoriaId" name="categoriaId" required>
                <option value="">Seleccione...</option>
                @for(Categoria c : categorias)
                    <option value="${c.getId()}" selected="${String.valueOf(c.getId()).equals(valores.get("categoriaId"))}">${c.getNombre()}</option>
                @endfor
            </select>
        </div>

        <div class="form-group">
            <label for="proveedorId">Proveedor</label>
            <select id="proveedorId" name="proveedorId">
                <option value="">Sin proveedor</option>
                @for(Proveedor p : proveedores)
                    <option value="${p.getId()}" selected="${String.valueOf(p.getId()).equals(valores.get("proveedorId"))}">${p.getNombre()} (${p.getIdentificador()})</option>
                @endfor
            </select>
        </div>

        <div class="form-group" id="grupoStockMinimo">
            <label for="stockMinimo">Stock mínimo * (solo insumos)</label>
            <input type="number" id="stockMinimo" name="stockMinimo" min="0" max="100000"
                   value="${valores.getOrDefault("stockMinimo", "")}">
        </div>

        <div class="inv-acciones">
            <button type="submit" class="inv-btn">Registrar</button>
            <a href="/inventario" class="inv-btn inv-btn-secundario">Cancelar</a>
        </div>
    </form>

    <script>
        $(function () {
            function actualizarStockMinimo() {
                var esInsumo = $("#tipo").val() === "INSUMO";
                $("#grupoStockMinimo").toggle(esInsumo);
                $("#stockMinimo").prop("required", esInsumo);
            }
            $("#tipo").on("change", actualizarStockMinimo);
            actualizarStockMinimo();
        });
    </script>
`
)
