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

    <p><a href="/biblioteca/libros">Volver al catalogo</a></p>

    <h1>Editar libro</h1>

    @if(error != null)
        <div class="alert-error">${error}</div>
    @endif

    <div class="info-card">
        <form method="post" action="/biblioteca/libros/editar">
            <input type="hidden" name="id" value="${libro.getId()}"/>
            <div class="form-group">
                <label>Titulo</label>
                <input type="text" name="titulo" value="${titulo != null ? titulo : ""}" required/>
            </div>
            <div class="form-group">
                <label>Autor</label>
                <input type="text" name="autor" value="${autor != null ? autor : ""}" required/>
            </div>
            <div class="form-group">
                <label>Categoria</label>
                <input type="text" name="categoria" value="${categoria != null ? categoria : ""}" required/>
            </div>
            <div class="form-group">
                <label>ISBN</label>
                <input type="text" name="isbn" id="isbnInput" value="${isbn != null ? isbn : ""}" maxlength="17" required oninput="formatIsbn(this)"/>
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
            <button type="submit" class="primary-btn">Guardar cambios</button>
        </form>
    </div>

    <div class="info-card" style="margin-top:16px;">
        <h3>Ejemplares</h3>
        <p>Total: ${ejemplares != null ? ejemplares.size() : 0}</p>
        @if(ejemplares != null && !ejemplares.isEmpty())
            <table class="data-table">
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
                        <td>${e.getEstado()}</td>
                        <td>
                            <a href="/biblioteca/ejemplares/editar?id=${e.getId()}">Editar</a>
                            @if("DISPONIBLE".equalsIgnoreCase(e.getEstado()))
                                <a href="/biblioteca/ejemplares/eliminar?id=${e.getId()}" style="color:#a83232;margin-left:8px;">Eliminar</a>
                            @else
                                <span style="color:#999;margin-left:8px;" title="No se puede eliminar un ejemplar prestado">(prestado)</span>
                            @endif
                        </td>
                    </tr>
                    @endfor
                </tbody>
            </table>
        @else
            <p>Este libro no tiene ejemplares. Agrega uno desde "Registrar ejemplar" en el catalogo.</p>
        @endif
    </div>

    <div class="info-card" style="margin-top:16px;border-color:#a83232;">
        <h3 style="color:#a83232;">Zona peligrosa</h3>
        <p>Esta accion eliminara el libro y todos sus ejemplares disponibles. No se puede deshacer.</p>
        <a href="/biblioteca/libros/eliminar?id=${libro.getId()}" class="primary-btn" style="background:#a83232;">Eliminar libro</a>
    </div>
`
)
