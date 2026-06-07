@import java.util.List
@import java.util.Map
@import cl.ucn.app.model.biblioteca.Libro
@param String usuarioNombre
@param List<Libro> libros
@param Map<Long, Integer> totalEjemplares
@param Map<Long, Integer> disponibles

@template.layout.main(
title = "Catálogo de Libros - SIGU-UCN",
usuarioNombre = usuarioNombre,
content = @`
    <link rel="stylesheet" href="/css/biblioteca.css">

    <h1>Catálogo de Libros</h1>

    <div class="spacer">
        <a href="/biblioteca/buscar" class="primary-btn" style="width:auto;display:inline-block;">Buscar libro</a>
        <a href="/biblioteca/libros/nuevo" class="primary-btn" style="width:auto;display:inline-block;">Registrar libro</a>
        <a href="/biblioteca/ejemplares/nuevo" class="primary-btn" style="width:auto;display:inline-block;">Registrar ejemplar</a>
        <a href="/biblioteca/prestamo/nuevo" class="primary-btn" style="width:auto;display:inline-block;">Solicitar préstamo</a>
        <a href="/biblioteca/devolucion" class="primary-btn" style="width:auto;display:inline-block;">Registrar devolución</a>
        <a href="/biblioteca/historial" class="primary-btn" style="width:auto;display:inline-block;">Historial</a>
        <a href="/biblioteca/lectores" class="primary-btn" style="width:auto;display:inline-block;">Lectores</a>
    </div>

    @if(libros != null && !libros.isEmpty())
        <table class="data-table">
            <thead>
                <tr>
                    <th>Título</th>
                    <th>Autor</th>
                    <th>Categoría</th>
                    <th>ISBN</th>
                    <th>Ejemplares</th>
                    <th>Disponibles</th>
                </tr>
            </thead>
            <tbody>
                @for(Libro libro : libros)
                <tr>
                    <td>${libro.getTitulo()}</td>
                    <td>${libro.getAutor()}</td>
                    <td>${libro.getCategoria()}</td>
                    <td>${libro.getIsbn()}</td>
                    <td>${totalEjemplares.getOrDefault(libro.getId(), 0)}</td>
                    <td>${disponibles.getOrDefault(libro.getId(), 0)}</td>
                </tr>
                @endfor
            </tbody>
        </table>
    @else
        <p>No hay libros registrados en el catálogo.</p>
    @endif
`
)
