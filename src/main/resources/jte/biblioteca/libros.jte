@import java.util.List
@import java.util.Map
@import cl.ucn.app.model.biblioteca.Libro
@param String usuarioNombre
@param String usuarioRol
@param List<Libro> libros
@param Map<Long, Integer> totalEjemplares
@param Map<Long, Integer> disponibles

@template.layout.main(
title = "Catálogo de Libros - SIGU-UCN",
usuarioNombre = usuarioNombre,
content = @`
    <link rel="stylesheet" href="/css/biblioteca.css">
    <script src="/js/biblioteca.js"></script>

    <div class="bib-container">
        <h1>Catálogo de Libros</h1>

        <div class="spacer" style="display:flex;gap:10px;flex-wrap:wrap;margin-bottom:20px;">
            <a href="/biblioteca/buscar" class="bib-btn bib-btn-primary">Buscar libro</a>
            @if(usuarioRol != null && usuarioRol.equals("ADMIN"))
                <a href="/biblioteca/libros/nuevo" class="bib-btn bib-btn-primary">Registrar libro</a>
                <a href="/biblioteca/ejemplares/nuevo" class="bib-btn bib-btn-primary">Registrar ejemplar</a>
                <a href="/biblioteca/historial" class="bib-btn bib-btn-primary">Préstamos</a>
                <a href="/biblioteca/lectores" class="bib-btn bib-btn-primary">Lectores</a>
            @endif
            @if(usuarioRol != null && (usuarioRol.equals("DOCENTE") || usuarioRol.equals("ESTUDIANTE")))
                <a href="/biblioteca/prestamo/nuevo" class="bib-btn bib-btn-accent">Solicitar préstamo</a>
                <a href="/biblioteca/historial" class="bib-btn bib-btn-primary">Mis préstamos</a>
                <a href="/biblioteca/mis-datos" class="bib-btn bib-btn-primary">Mis datos</a>
            @endif
        </div>

        @if(libros != null && !libros.isEmpty())
            <table class="bib-table">
                <thead>
                    <tr>
                        <th>Título</th>
                        <th>Autor</th>
                        <th>Categoría</th>
                        <th>ISBN</th>
                        <th>Ejemplares</th>
                        <th>Disponibles</th>
                        @if(usuarioRol != null && usuarioRol.equals("ADMIN"))
                            <th>Acciones</th>
                        @endif
                    </tr>
                </thead>
                <tbody>
                    @for(Libro libro : libros)
                    <tr>
                        <td style="font-weight:600;color:var(--bib-primary);">${libro.getTitulo()}</td>
                        <td>${libro.getAutor()}</td>
                        <td>${libro.getCategoria()}</td>
                        <td>${libro.getIsbn()}</td>
                        <td>${totalEjemplares.getOrDefault(libro.getId(), 0)}</td>
                        <td>
                            @if(disponibles.getOrDefault(libro.getId(), 0) > 0)
                                <span class="bib-badge bib-badge-disponible">${disponibles.getOrDefault(libro.getId(), 0)} disp.</span>
                            @else
                                <span class="bib-badge bib-badge-bloqueado">Agotado</span>
                            @endif
                        </td>
                        @if(usuarioRol != null && usuarioRol.equals("ADMIN"))
                            <td>
                                <a href="/biblioteca/libros/editar?id=${libro.getId()}" class="bib-action-link">Editar</a>
                                <a href="/biblioteca/libros/eliminar?id=${libro.getId()}" class="bib-action-link-danger bib-confirm-delete" data-message="¿Está seguro de que desea eliminar el libro '${libro.getTitulo()}' y todos sus ejemplares/préstamos?" style="margin-left:12px;">Eliminar</a>
                            </td>
                        @endif
                    </tr>
                    @endfor
                </tbody>
            </table>
        @else
            <p>No hay libros registrados en el catálogo.</p>
        @endif
    </div>
`
)
