@param String usuarioNombre
@param String error

@template.layout.main(
title = "Registrar Libro - SIGU-UCN",
usuarioNombre = usuarioNombre,
content = @`
    <link rel="stylesheet" href="/css/biblioteca.css">

    <p><a href="/biblioteca/libros">Volver al catálogo</a></p>

    <h1>Registrar nuevo libro</h1>

    @if(error != null)
        <div class="alert-error">${error}</div>
    @endif

    <div class="info-card">
        <form method="post" action="/biblioteca/libros/nuevo">
            <div class="form-group">
                <label>Título</label>
                <input type="text" name="titulo" placeholder="Título del libro" required/>
            </div>
            <div class="form-group">
                <label>Autor</label>
                <input type="text" name="autor" placeholder="Nombre del autor" required/>
            </div>
            <div class="form-group">
                <label>Categoría</label>
                <input type="text" name="categoria" placeholder="Ej: Programación, Matemáticas" required/>
            </div>
            <div class="form-group">
                <label>ISBN</label>
                <input type="text" name="isbn" placeholder="Ej: 978-3-16-148410-0" required/>
            </div>
            <button type="submit" class="primary-btn">Registrar libro</button>
        </form>
    </div>
`
)
