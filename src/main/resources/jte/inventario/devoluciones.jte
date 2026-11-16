@import java.util.List
@import cl.ucn.app.model.Prestamo
@import cl.ucn.app.util.FechasInventario

@param String title
@param String usuarioNombre
@param String rol
@param String flashOk = null
@param String flashError = null
@param List<Prestamo> prestamos

@template.inventario.layout(
title = title, usuarioNombre = usuarioNombre, rol = rol,
flashOk = flashOk, flashError = flashError,
content = @`
    <h2>Préstamos activos</h2>
    @if(prestamos.isEmpty())
        <p class="inv-vacio">No hay préstamos activos pendientes de devolución.</p>
    @else
        <table class="inv-tabla">
            <thead>
            <tr>
                <th>Equipo</th>
                <th>Solicitante</th>
                <th>Fecha de préstamo</th>
                <th>Acción</th>
            </tr>
            </thead>
            <tbody>
            @for(Prestamo p : prestamos)
                <tr>
                    <td>${p.getEquipo().getCodigo()} - ${p.getEquipo().getNombre()}</td>
                    <td>${p.getUsuario().getNombre()}</td>
                    <td>${FechasInventario.formatear(p.getFechaPrestamo())}</td>
                    <td>
                        <form method="post" action="/inventario/devoluciones"
                              onsubmit="return confirm('¿Confirmar la recepción del equipo?');">
                            <input type="hidden" name="prestamoId" value="${p.getId()}">
                            <button type="submit" class="inv-btn">Registrar devolución</button>
                        </form>
                    </td>
                </tr>
            @endfor
            </tbody>
        </table>
        <p class="inv-total">${prestamos.size()} préstamo(s) activo(s)</p>
    @endif
`
)
