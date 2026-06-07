@param String title
@param String usuarioNombre
@param String error

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <section class="page-section">
        <div class="page-header">
            <h1>Registrar Expositor</h1>
            <a href="/expositores" class="btn-secondary">Volver</a>
        </div>

        @if(error != null && !error.isBlank())
            <div class="alert alert-error">${error}</div>
        @endif

        <form method="post" action="/expositores/registrar" class="form-card">
            <div class="form-group">
                <label for="nombre">Nombre completo *</label>
                <input type="text" id="nombre" name="nombre" placeholder="Ej: Dr. Juan Pérez" required>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="email">Correo electrónico</label>
                    <input type="email" id="email" name="email" placeholder="Ej: jperez@universidad.cl">
                </div>
                <div class="form-group">
                    <label for="telefono">Teléfono</label>
                    <input type="text" id="telefono" name="telefono" placeholder="Ej: +56 9 1234 5678">
                </div>
            </div>

            <div class="form-group">
                <label for="afiliacion">Afiliación / Institución</label>
                <input type="text" id="afiliacion" name="afiliacion" placeholder="Ej: Universidad Católica del Norte">
            </div>

            <div class="form-group">
                <label for="bio">Biografía / Resumen curricular</label>
                <textarea id="bio" name="bio" rows="4" placeholder="Breve descripción de su trayectoria y áreas de especialización"></textarea>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn-primary">Registrar Expositor</button>
                <a href="/expositores" class="btn-secondary">Cancelar</a>
            </div>
        </form>
    </section>
`
)
