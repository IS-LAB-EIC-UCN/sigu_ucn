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
                    @if(libroId != null && libroId.equals(String.valueOf(libro.getId())))
                        <option value="${libro.getId()}" selected>${libro.getTitulo()} - ${libro.getAutor()} (${libro.getIsbn()})</option>
                    @else
                        <option value="${libro.getId()}">${libro.getTitulo()} - ${libro.getAutor()} (${libro.getIsbn()})</option>
                    @endif
                    @endfor
                </select>
            </div>
            <div class="form-group">
                <label>Cantidad de ejemplares a registrar</label>
                <input type="number" name="cantidad" min="1" max="100" value="1" required/>
            </div>
            <button type="submit" class="primary-btn">Registrar ejemplares</button>
        </form>
    </div>
`
)
