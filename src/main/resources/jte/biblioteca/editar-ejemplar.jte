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

    <p><a href="/biblioteca/libros">Volver al catalogo</a></p>

    <h1>Editar ejemplar</h1>

    @if(error != null)
        <div class="alert-error">${error}</div>
    @endif

    <div class="info-card">
        <form method="post" action="/biblioteca/ejemplares/editar">
            <input type="hidden" name="id" value="${ejemplar.getId()}"/>
            <div class="form-group">
                <label>Libro</label>
                <input type="text" value="${ejemplar.getLibro().getTitulo()}" disabled/>
            </div>
            <div class="form-group">
                <label>Estado</label>
                <select name="estado">
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
            <button type="submit" class="primary-btn">Guardar cambios</button>
            <a href="/biblioteca/ejemplares/eliminar?id=${ejemplar.getId()}" class="primary-btn" style="background:#a83232;margin-left:8px;">Eliminar</a>
        </form>
    </div>
`
)
