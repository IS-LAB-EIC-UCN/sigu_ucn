@param String title
@param String usuarioNombre

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <section class="dashboard">
        <h1>Acceso denegado</h1>
        <div class="alert-error">No tiene permisos para acceder a esta sección.</div>
        <p><a href="/home">Volver al inicio</a></p>
    </section>
`
)
