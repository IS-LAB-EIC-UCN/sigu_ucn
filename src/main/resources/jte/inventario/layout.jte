@import gg.jte.Content
@import cl.ucn.app.util.RolesInventario

@param String title
@param String usuarioNombre
@param String rol
@param String flashOk = null
@param String flashError = null
@param Content content

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <section class="inv-modulo">
        <header class="inv-encabezado">
            <h1>Inventario de laboratorio</h1>
            <span class="inv-rol">Rol: ${rol}</span>
        </header>

        <nav class="inv-nav">
            <a href="/inventario">Consultar inventario</a>
            @if(RolesInventario.esAdmin(rol))
                <a href="/inventario/productos/nuevo">Registrar producto</a>
            @endif
            @if(RolesInventario.esGestion(rol))
                <a href="/inventario/stock">Gestionar stock</a>
                <a href="/inventario/devoluciones">Registrar devolución</a>
                <a href="/inventario/movimientos">Movimientos</a>
            @endif
            @if(RolesInventario.esSolicitante(rol))
                <a href="/inventario/prestamos">Solicitar préstamo</a>
            @endif
        </nav>

        @if(flashOk != null && !flashOk.isBlank())
            <div class="inv-alerta inv-alerta-ok">${flashOk}</div>
        @endif
        @if(flashError != null && !flashError.isBlank())
            <div class="inv-alerta inv-alerta-error">${flashError}</div>
        @endif

        ${content}
    </section>
`
)
