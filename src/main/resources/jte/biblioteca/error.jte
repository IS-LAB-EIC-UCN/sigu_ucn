@param String error
@param int status

@template.layout.main(
title = "Error ${status} - SIGU-UCN",
usuarioNombre = "",
content = @`
    <link rel="stylesheet" href="/css/biblioteca.css">
    <div class="error-container">
        <h1>Error ${status}</h1>
        <p>${error}</p>
        <a href="/biblioteca/libros" class="primary-btn">Volver al catalogo</a>
    </div>
`
)
