@import java.util.List
@import cl.ucn.app.model.biblioteca.PrestamoLibro
@param String usuarioNombre
@param List<PrestamoLibro> prestamos
@param String error
@param String mensaje

@template.layout.main(
title = "Registrar Devolución - SIGU-UCN",
usuarioNombre = usuarioNombre,
content = @`
    <h1>Registrar Devolución</h1>

    @if(error != null)
        <p style="color:red">${error}</p>
    @endif
    @if(mensaje != null)
        <p style="color:green">${mensaje}</p>
    @endif

    <form method="post" action="/biblioteca/devolucion">
        <label>Préstamo activo:</label>
        <select name="prestamoId">
            @for(PrestamoLibro p : prestamos)
            <option value="${p.getId()}">
                ${p.getLector().getNombre()} —
                ${p.getEjemplar().getLibro().getTitulo()} —
                Vence: ${p.getFechaVencimiento().toString()}
            </option>
            @endfor
        </select>
        <br/>
        <button type="submit">Registrar devolución</button>
    </form>
`
)