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
                <input type="text" name="nombre" placeholder="Ej: Juan Perez" value="${lector.getNombre()}" required/>
            </div>
            <div class="form-group">
                <label>Correo electronico</label>
                <input type="text" name="correo" placeholder="juan.perez@ucn.cl" value="${lector.getCorreo()}" required/>
            </div>
            <div class="form-group">
                <label>RUT</label>
                <input type="text" name="rut" id="rutInput" value="${lector.getRut()}" maxlength="10" required oninput="formatRut(this)"/>
            </div>
            <script>
                function formatRut(input) {
                    let v = input.value.replace(/[^0-9kK]/g, '');
                    if (v.length > 1) v = v.slice(0,-1) + '-' + v.slice(-1);
                    input.value = v;
                }
            </script>
            <button type="submit" class="primary-btn">Guardar cambios</button>
        </form>
    </div>
`
)
