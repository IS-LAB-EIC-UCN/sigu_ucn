@import java.util.List
@import cl.ucn.app.model.biblioteca.Libro
@param String usuarioNombre
@param List<Libro> libros
@param String error
@param String mensaje
@param String libroId

@template.layout.main(
title = "Registrar Ejemplar - SIGU-UCN",
usuarioNombre = usuarioNombre,
content = @`
    <link rel="stylesheet" href="/css/biblioteca.css">
    <script src="/js/biblioteca.js"></script>

    <div class="bib-container">
        <p><a href="/biblioteca/libros" class="bib-action-link">&larr; Volver al catálogo</a></p>

        <h1>Registrar nuevo ejemplar</h1>

        @if(error != null)
            <div class="bib-alert bib-alert-error">${error}</div>
        @endif
        @if(mensaje != null)
            <div class="bib-alert bib-alert-success">${mensaje}</div>
        @endif

        <div class="bib-card">
            @if(libros == null || libros.isEmpty())
                <div class="bib-alert bib-alert-error" style="margin-bottom: 20px;">
                    <strong>No hay libros en el catálogo:</strong> Debes registrar al menos un libro antes de poder agregar ejemplares físicos.
                </div>
                <p><a href="/biblioteca/libros/nuevo" class="bib-btn bib-btn-primary">Registrar libro</a></p>
            @else
                <form method="post" action="/biblioteca/ejemplares/nuevo">
                    <div class="bib-form-group">
                        <label>Libro</label>
                        <select name="libroId" class="bib-form-select">
                            @for(Libro libro : libros)
                                @if(libroId != null && libroId.equals(String.valueOf(libro.getId())))
                                    <option value="${libro.getId()}" selected>${libro.getTitulo()} - ${libro.getAutor()} (${libro.getIsbn()})</option>
                                @else
                                    <option value="${libro.getId()}">${libro.getTitulo()} - ${libro.getAutor()} (${libro.getIsbn()})</option>
                                @endif
                            @endfor
                        </select>
                    </div>
                    <div class="bib-form-group">
                        <label>Cantidad de ejemplares a registrar</label>
                        <input type="number" name="cantidad" class="bib-form-input" min="1" max="100" value="1" required/>
                    </div>
                    <button type="submit" class="bib-btn bib-btn-primary">Registrar ejemplares</button>
                </form>
            @endif
        </div>
    </div>
`
)
