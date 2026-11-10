@param String title
@param String usuarioNombre
@param int status
@param String titulo
@param String detalle = null
@param String mensaje

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <link rel="stylesheet" href="/css/biblioteca.css">
    <section class="dashboard">
        <div class="info-card error-card">
            <p class="error-estado">${status}</p>
            <h1>${titulo}</h1>
            @if(detalle != null)
                <p class="error-detalle">${detalle}</p>
            @endif
            <div class="alert-error">${mensaje}</div>
            <div class="error-acciones">
                <button type="button" class="primary-btn" onclick="history.back()">Volver</button>
                <a href="/biblioteca">Ir al módulo de Biblioteca</a>
            </div>
        </div>
    </section>
`
)
