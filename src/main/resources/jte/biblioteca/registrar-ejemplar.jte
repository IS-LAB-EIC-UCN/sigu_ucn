@import java.util.List
@import cl.ucn.app.model.biblioteca.Libro
@param String usuarioNombre
@param List<Libro> libros
@param String error
@param String mensaje

@template.layout.main(
title = "Registrar Ejemplar - SIGU-UCN",
usuarioNombre = usuarioNombre,
content = @`
    <link rel="stylesheet" href="/css/biblioteca.css">

    <p><a href="/biblioteca/libros">Volver al catálogo</a></p>

    <h1>Registrar nuevo ejemplar</h1>

    @if(error != null)
        <div class="alert-error">${error}</div>
    @endif
    @if(mensaje != null)
        <div class="alert-success">${mensaje}</div>
    @endif

    <div class="info-card">
        <form method="post" action="/biblioteca/ejemplares/nuevo">
            <div class="form-group">
                <label>Libro</label>
                <select name="libroId">
                    @for(Libro libro : libros)
                    <option value="${libro.getId()}">${libro.getTitulo()} — ${libro.getAutor()} (${libro.getIsbn()})</option>
                    @endfor
                </select>
            </div>
            <button type="submit" class="primary-btn">Registrar ejemplar</button>
        </form>
    </div>
`
)
