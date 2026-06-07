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
    <link rel="stylesheet" href="/css/biblioteca.css">

    <p><a href="/biblioteca/libros">Volver al catálogo</a></p>

    <h1>Registrar Devolución</h1>

    @if(error != null)
        <div class="alert-error">${error}</div>
    @endif
    @if(mensaje != null)
        <div class="alert-success">${mensaje}</div>
    @endif

    <div class="info-card">
        <form method="post" action="/biblioteca/devolucion">
            <div class="form-group">
                <label>Préstamo activo</label>
                <select name="prestamoId">
                    @for(PrestamoLibro p : prestamos)
                    <option value="${p.getId()}">
                        ${p.getLector().getNombre()} — ${p.getEjemplar().getLibro().getTitulo()} — Vence: ${p.getFechaVencimiento().toString()}
                    </option>
                    @endfor
                </select>
            </div>
            <button type="submit" class="primary-btn">Registrar devolución</button>
        </form>
    </div>
`
)
