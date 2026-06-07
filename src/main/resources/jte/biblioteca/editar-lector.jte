@import cl.ucn.app.model.biblioteca.Lector
@param String usuarioNombre
@param Lector lector
@param String error

@template.layout.main(
title = "Editar Lector - SIGU-UCN",
usuarioNombre = usuarioNombre,
content = @`
    <link rel="stylesheet" href="/css/biblioteca.css">

    <p><a href="/biblioteca/lectores">Volver a lectores</a></p>

    <h1>Editar lector</h1>

    @if(error != null)
        <div class="alert-error">${error}</div>
    @endif

    <div class="info-card">
        <form method="post" action="/biblioteca/lectores/editar">
            <input type="hidden" name="id" value="${lector.getId()}"/>
            <div class="form-group">
                <label>Nombre completo</label>
                <input type="text" name="nombre" value="${lector.getNombre()}" required/>
            </div>
            <div class="form-group">
                <label>Correo electrónico</label>
                <input type="text" name="correo" value="${lector.getCorreo()}" required/>
            </div>
            <div class="form-group">
                <label>RUT</label>
                <input type="text" name="rut" value="${lector.getRut()}" required/>
            </div>
            <button type="submit" class="primary-btn">Guardar cambios</button>
        </form>
    </div>
`
)
