@param String title
@param String usuarioNombre

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <section class="dashboard">
        <h1>Panel principal</h1>
        <p>Bienvenido al sistema integrado de gestión universitaria.</p>

        <div class="card-grid">
            <div class="info-card">
                <h3>Instrucciones para los equipos</h3>
                <p>
                    Cada grupo deberá desarrollar su módulo y reemplazar la opción de menú
                    correspondiente por la ruta real de acceso a su funcionalidad.
                </p>
            </div>

            <div class="info-card">
                <h3>Integración</h3>
                <p>
                    Las nuevas rutas, vistas y controladores deben respetar la estructura
                    común del sistema base.
                </p>
            </div>

            <div class="info-card">
                <h3>Tecnologías base</h3>
                <p>
                    Java, Javalin, JPA/Hibernate, PostgreSQL, JTE y jQuery.
                </p>
            </div>
        </div>
    </section>
`
)