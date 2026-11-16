@import java.util.List
@import cl.ucn.app.model.Equipo
@import cl.ucn.app.model.Prestamo
@import cl.ucn.app.util.FechasInventario

@param String title
@param String usuarioNombre
@param String rol
@param String flashOk = null
@param String flashError = null
@param List<Equipo> equipos
@param List<Prestamo> misPrestamos

@template.inventario.layout(
title = title, usuarioNombre = usuarioNombre, rol = rol,
flashOk = flashOk, flashError = flashError,
content = @`
    <h2>Equipos disponibles</h2>
    @if(equipos.isEmpty())
        <p class="inv-vacio">No hay equipos disponibles para préstamo en este momento.</p>
    @else
        <table class="inv-tabla">
            <thead>
            <tr>
                <th>Código</th>
                <th>Equipo</th>
                <th>Categoría</th>
                <th>Acción</th>
            </tr>
            </thead>
            <tbody>
            @for(Equipo e : equipos)
                <tr>
                    <td>${e.getCodigo()}</td>
                    <td>
                        ${e.getNombre()}
                        @if(e.getDescripcion() != null && !e.getDescripcion().isBlank())
                            <br><small>${e.getDescripcion()}</small>
                        @endif
                    </td>
                    <td>${e.getCategoria().getNombre()}</td>
                    <td>
                        <form method="post" action="/inventario/prestamos"
                              onsubmit="return confirm('¿Confirmar el préstamo de este equipo?');">
                            <input type="hidden" name="equipoId" value="${e.getId()}">
                            <button type="submit" class="inv-btn">Solicitar</button>
                        </form>
                    </td>
                </tr>
            @endfor
            </tbody>
        </table>
    @endif

    <h2>Mis solicitudes</h2>
    @if(misPrestamos.isEmpty())
        <p class="inv-vacio">Aún no ha solicitado préstamos.</p>
    @else
        <table class="inv-tabla">
            <thead>
            <tr>
                <th>Equipo</th>
                <th>Fecha de préstamo</th>
                <th>Estado</th>
                <th>Fecha de devolución</th>
            </tr>
            </thead>
            <tbody>
            @for(Prestamo p : misPrestamos)
                <tr>
                    <td>${p.getEquipo().getCodigo()} - ${p.getEquipo().getNombre()}</td>
                    <td>${FechasInventario.formatear(p.getFechaPrestamo())}</td>
                    <td><span class="inv-badge">${p.getEstado().name()}</span></td>
                    <td>${FechasInventario.formatear(p.getFechaDevolucion())}</td>
                </tr>
            @endfor
            </tbody>
        </table>
    @endif
`
)
