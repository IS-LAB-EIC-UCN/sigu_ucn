@param String title
@param String usuarioNombre
@param boolean esAdmin

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <section class="dashboard">
        <h1>Acceso denegado</h1>
        <div class="alert-error">No tiene permisos para acceder a esta sección.</div>
        @template.reservas.nav(esAdmin = esAdmin, activo = "")
    </section>
`
)
