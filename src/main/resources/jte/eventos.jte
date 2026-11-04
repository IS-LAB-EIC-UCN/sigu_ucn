
@param String title
@param String usuarioNombre

@template.layout.main(
    title = title,
    usuarioNombre = usuarioNombre,
    content = @`
        <section class="dashboard">
            <h1>Gestión de Eventos Académicos</h1>

            <p>Bienvenido al módulo del Grupo 6.</p>

            <div class="card-grid">
                <div class="info-card">
                    <h3>Eventos académicos</h3>
                    <p>
                        Aquí se administrarán los eventos
                        universitarios.
                    </p>
                </div>

                <div class="info-card">
                    <h3>Inscripciones</h3>
                    <p>
                        Aquí se gestionarán las inscripciones
                        de los participantes.
                    </p>
                </div>
            </div>
        </section>
    `
)
