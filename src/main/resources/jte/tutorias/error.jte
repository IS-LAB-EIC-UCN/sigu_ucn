@param String title
@param String usuarioNombre
@param String mensaje

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <link rel="stylesheet" href="/css/tutorias.css">
    <section class="tutorias">
        <div class="info-card">
            <h1>No es posible acceder a esta sección</h1>
            <div class="alert-error">${mensaje}</div>
            <a class="tut-btn tut-btn-secundario" href="/home">Volver al inicio</a>
        </div>
    </section>
`
)
