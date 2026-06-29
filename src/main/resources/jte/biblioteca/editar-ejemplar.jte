@import cl.ucn.app.model.biblioteca.Ejemplar
@param String usuarioNombre
@param Ejemplar ejemplar
@param String error
@param String estado

@template.layout.main(
title = "Editar Ejemplar - SIGU-UCN",
usuarioNombre = usuarioNombre,
content = @`
    <link rel="stylesheet" href="/css/biblioteca.css">

    <div class="bib-container">
        <p><a href="/biblioteca/libros" class="bib-action-link">&larr; Volver al catalogo</a></p>

        <h1>Editar ejemplar</h1>

        @if(error != null)
            <div class="bib-alert bib-alert-error">${error}</div>
        @endif

        <div class="bib-card">
            <form method="post" action="/biblioteca/ejemplares/editar">
                <input type="hidden" name="id" value="${ejemplar.getId()}"/>
                <div class="bib-form-group">
                    <label>Libro</label>
                    <input type="text" class="bib-form-input" value="${ejemplar.getLibro() != null ? ejemplar.getLibro().getTitulo() : "N/A"}" disabled/>
                </div>
                <div class="bib-form-group">
                    <label>Estado</label>
                    <select name="estado" class="bib-form-input">
                        @if(estado != null && estado.equals("DISPONIBLE"))
                            <option value="DISPONIBLE" selected>DISPONIBLE</option>
                        @else
                            <option value="DISPONIBLE">DISPONIBLE</option>
                        @endif
                        @if(estado != null && estado.equals("PRESTADO"))
                            <option value="PRESTADO" selected>PRESTADO</option>
                        @else
                            <option value="PRESTADO">PRESTADO</option>
                        @endif
                    </select>
                </div>
                <button type="submit" class="bib-btn bib-btn-primary">Guardar cambios</button>
                <a href="/biblioteca/ejemplares/eliminar?id=${ejemplar.getId()}" class="bib-btn bib-btn-danger bib-confirm-delete" data-message="¿Esta seguro de que desea eliminar el ejemplar?">Eliminar</a>
            </form>
        </div>
    </div>
`
)
