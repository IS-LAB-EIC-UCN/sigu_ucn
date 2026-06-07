@import java.util.List
@import cl.ucn.app.model.biblioteca.Libro
@param String usuarioNombre
@param List<Libro> resultados
@param String termino

@template.layout.main(
title = "Buscar Libros - SIGU-UCN",
usuarioNombre = usuarioNombre,
content = @`
    <link rel="stylesheet" href="/css/biblioteca.css">

    <p><a href="/biblioteca/libros">Volver al catálogo</a></p>

    <h1>Buscar Libros</h1>

    <div class="info-card">
        <form method="get" action="/biblioteca/buscar">
            <div class="form-group">
                <label>Buscar por título, autor o categoría</label>
                <div style="display:flex;gap:8px;">
                    <input type="text" name="termino" value="${termino}" placeholder="Escribe tu búsqueda..." style="flex:1;"/>
                    <button type="submit" class="primary-btn" style="width:auto;">Buscar</button>
                </div>
            </div>
        </form>
    </div>

    @if(resultados != null && !resultados.isEmpty())
        <table class="data-table">
            <thead>
                <tr>
                    <th>Título</th>
                    <th>Autor</th>
                    <th>Categoría</th>
                    <th>ISBN</th>
                </tr>
            </thead>
            <tbody>
                @for(Libro libro : resultados)
                <tr>
                    <td>${libro.getTitulo()}</td>
                    <td>${libro.getAutor()}</td>
                    <td>${libro.getCategoria()}</td>
                    <td>${libro.getIsbn()}</td>
                </tr>
                @endfor
            </tbody>
        </table>
    @elseif(termino != null && !termino.isEmpty())
        <p>No se encontraron libros para "${termino}".</p>
    @endif
`
)
