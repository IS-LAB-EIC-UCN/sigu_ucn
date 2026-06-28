@import cl.ucn.app.model.biblioteca.Libro
@import cl.ucn.app.model.biblioteca.Ejemplar
@import java.util.List
@param String usuarioNombre
@param Libro libro
@param List<Ejemplar> ejemplares
@param String error
@param String titulo
@param String autor
@param String categoria
@param String isbn

@template.layout.main(
title = "Editar Libro - SIGU-UCN",
usuarioNombre = usuarioNombre,
content = @`
    <link rel="stylesheet" href="/css/biblioteca.css">
    <script src="/js/biblioteca.js"></script>

    <div class="bib-container">
        <p><a href="/biblioteca/libros" class="bib-action-link">&larr; Volver al catálogo</a></p>

        <h1>Editar libro</h1>

        @if(error != null)
            <div class="bib-alert bib-alert-error">${error}</div>
        @endif

        <div class="bib-card">
            <form method="post" action="/biblioteca/libros/editar">
                <input type="hidden" name="id" value="${libro.getId()}"/>
                <div class="bib-form-group">
                    <label>Título</label>
                    <input type="text" name="titulo" class="bib-form-input" value="${titulo != null ? titulo : ""}" required/>
                </div>
                <div class="bib-form-group">
                    <label>Autor</label>
                    <input type="text" name="autor" class="bib-form-input" value="${autor != null ? autor : ""}" required/>
                </div>
                <div class="bib-form-group">
                    <label>Categoría</label>
                    <input type="text" name="categoria" class="bib-form-input" value="${categoria != null ? categoria : ""}" required/>
                </div>
                <div class="bib-form-group">
                    <label>ISBN</label>
                    <input type="text" name="isbn" id="isbnInput" class="bib-form-input" value="${isbn != null ? isbn : ""}" maxlength="17" required oninput="formatIsbn(this)"/>
                </div>
                <script>
                    function formatIsbn(input) {
                        let v = input.value.replace(/[^0-9Xx]/g, '');
                        if (v.length > 3) v = v.slice(0,3) + '-' + v.slice(3);
                        if (v.length > 7) v = v.slice(0,7) + '-' + v.slice(7);
                        if (v.length > 11) v = v.slice(0,11) + '-' + v.slice(11);
                        if (v.length > 15) v = v.slice(0,15) + '-' + v.slice(15);
                        input.value = v;
                    }
                </script>
                <button type="submit" class="bib-btn bib-btn-primary">Guardar cambios</button>
            </form>
        </div>

        <div class="bib-card" style="margin-top:20px;">
            <h3>Ejemplares</h3>
            <p>Total: <strong>${ejemplares != null ? ejemplares.size() : 0}</strong></p>
            @if(ejemplares != null && !ejemplares.isEmpty())
                <table class="bib-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        @for(Ejemplar e : ejemplares)
                        <tr>
                            <td>${e.getId()}</td>
                            <td>
                                @if(e.getEstado() == cl.ucn.app.model.biblioteca.EstadoEjemplar.DISPONIBLE)
                                    <span class="bib-badge bib-badge-disponible">Disponible</span>
                                @else
                                    <span class="bib-badge bib-badge-prestado">Prestado</span>
                                @endif
                            </td>
                            <td>
                                <a href="/biblioteca/ejemplares/editar?id=${e.getId()}" class="bib-action-link">Editar</a>
                                @if(e.getEstado() == cl.ucn.app.model.biblioteca.EstadoEjemplar.DISPONIBLE)
                                    <a href="/biblioteca/ejemplares/eliminar?id=${e.getId()}" class="bib-action-link-danger bib-confirm-delete" data-message="¿Está seguro de que desea eliminar el ejemplar con ID ${e.getId()}?" style="margin-left:12px;">Eliminar</a>
                                @else
                                    <span style="color:#999;margin-left:12px;" title="No se puede eliminar un ejemplar prestado">(prestado)</span>
                                @endif
                            </td>
                        </tr>
                        @endfor
                    </tbody>
                </table>
            @else
                <p>Este libro no tiene ejemplares. Agrega uno desde "Registrar ejemplar" en el catálogo.</p>
            @endif
        </div>

        <div class="bib-card" style="margin-top:20px;border-color:var(--bib-danger);">
            <h3 style="color:var(--bib-danger);">Zona peligrosa</h3>
            <p style="margin-bottom:16px;">Esta acción eliminará el libro y todos sus ejemplares y registros asociados. No se puede deshacer.</p>
            <a href="/biblioteca/libros/eliminar?id=${libro.getId()}" class="bib-btn bib-btn-danger bib-confirm-delete" data-message="¿Está seguro de que desea eliminar el libro '${libro.getTitulo()}' y todos sus ejemplares/préstamos?">Eliminar libro</a>
        </div>
    </div>
`
)
