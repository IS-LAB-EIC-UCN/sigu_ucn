@import java.util.List
@import cl.ucn.app.model.biblioteca.PrestamoLibro
@param String usuarioNombre
@param String usuarioRol
@param List<PrestamoLibro> prestamos
@param String error
@param String mensaje

@template.layout.main(
title = "Préstamos Activos - SIGU-UCN",
usuarioNombre = usuarioNombre,
content = @`
    <link rel="stylesheet" href="/css/biblioteca.css">
    <script src="/js/biblioteca.js"></script>

    <div class="bib-container">
        <p><a href="/biblioteca/libros" class="bib-action-link">&larr; Volver al catálogo</a></p>

        <h1>Préstamos Activos</h1>

        @if(error != null)
            <div class="bib-alert bib-alert-error">${error}</div>
        @endif
        @if(mensaje != null)
            <div class="bib-alert bib-alert-success">${mensaje}</div>
        @endif

        @if(prestamos != null && !prestamos.isEmpty())
            <div class="bib-card">
                <form method="post" action="/biblioteca/devolucion">
                    <div class="bib-form-group">
                        <label>Préstamo activo</label>
                        <select name="prestamoId" class="bib-form-select">
                            @for(PrestamoLibro p : prestamos)
                            <option value="${p.getId()}">
                                ${p.getLector().getNombre()} (${p.getLector().getRut()}) - ${p.getEjemplar().getLibro().getTitulo()} - Vence: ${p.getFechaVencimiento().toString()}
                            </option>
                            @endfor
                        </select>
                    </div>
                    <button type="submit" class="bib-btn bib-btn-primary">Registrar devolución</button>
                </form>
            </div>
        @else
            <p>No hay préstamos activos.</p>
        @endif
    </div>
`
)
