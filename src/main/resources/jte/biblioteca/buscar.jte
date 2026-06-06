@import java.util.List
@import cl.ucn.app.model.biblioteca.Libro
@param String usuarioNombre
@param List<Libro> resultados
@param String termino

@template.layout.main(
title = "Buscar Libros - SIGU-UCN",
usuarioNombre = usuarioNombre,
content = @`
    <h1>Buscar Libros</h1>
    <form method="get" action="/biblioteca/buscar">
        <input type="text" name="termino" value="${termino}" placeholder="Título, autor o categoría"/>
        <button type="submit">Buscar</button>
    </form>

    @if(resultados != null && !resultados.isEmpty())
        <h2>Resultados</h2>
        <ul>
            @for(Libro libro : resultados)
            <li>${libro.getTitulo()} — ${libro.getAutor()} (${libro.getCategoria()})</li>
            @endfor
        </ul>
    @elseif(termino != null && !termino.isEmpty())
        <p>No se encontraron libros para "${termino}"</p>
    @endif
`
)