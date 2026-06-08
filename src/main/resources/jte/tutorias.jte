@param String title
@param String usuarioNombre

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <section class="dashboard">
        <h1>Panel de tutorías</h1>
        <p>En esta seccion podras tanto ofrecer como buscar tutorías.</p>

        <div class="card-grid">

        </div>
    </section>
`
)