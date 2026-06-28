@import cl.ucn.app.controller.biblioteca.LibroDisponible
@import java.util.List
@param String usuarioNombre
@param List<LibroDisponible> librosDisponibles
@param String error
@param String libroId
@param String fechaVencimiento

@template.layout.main(
title = "Solicitar Préstamo - SIGU-UCN",
usuarioNombre = usuarioNombre,
content = @`
    <link rel="stylesheet" href="/css/biblioteca.css">
    <script src="/js/biblioteca.js"></script>

    <div class="bib-container">
        <p><a href="/biblioteca/libros" class="bib-action-link">&larr; Volver al catálogo</a></p>

        <h1>Solicitar Préstamo</h1>

        @if(error != null)
            <div class="bib-alert bib-alert-error">${error}</div>
        @endif

        <div class="bib-card">
            <form method="post" action="/biblioteca/prestamo/nuevo">
                <div class="bib-form-group">
                    <label>Libro a solicitar</label>
                    <select name="libroId" class="bib-form-select" required>
                        @if(librosDisponibles != null && !librosDisponibles.isEmpty())
                            <option value="">Seleccione un libro</option>
                            @for(LibroDisponible ld : librosDisponibles)
                            @if(libroId != null && !libroId.isEmpty() && ld.getLibro().getId().equals(Long.parseLong(libroId)))
                                <option value="${ld.getLibro().getId()}" selected>${ld.getLabel()}</option>
                            @else
                                <option value="${ld.getLibro().getId()}">${ld.getLabel()}</option>
                            @endif
                            @endfor
                        @else
                            <option value="">No hay libros disponibles</option>
                        @endif
                    </select>
                </div>
                <div class="bib-form-group">
                    <label>Fecha de vencimiento</label>
                    <input type="date" name="fechaVencimiento" class="bib-form-input" value="${fechaVencimiento != null ? fechaVencimiento : ""}" required/>
                </div>
                <button type="submit" class="bib-btn bib-btn-primary">Confirmar préstamo</button>
            </form>
        </div>
    </div>
`
)
