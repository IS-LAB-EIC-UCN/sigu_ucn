@param String usuarioNombre
@param String usuarioRol
@param String returnTo = null
@param String error = null
@param String nombre = null
@param String correo = null
@param String rut = null
@param cl.ucn.app.model.biblioteca.Lector lector = null
@param boolean guardado = false

@template.layout.main(
title = "Mis Datos - SIGU-UCN",
usuarioNombre = usuarioNombre,
content = @`
    <link rel="stylesheet" href="/css/biblioteca.css">
    <script src="/js/biblioteca.js"></script>

    <div class="bib-container">
        <h1>Mis Datos de Lector</h1>
        <p>Tus datos registrados en el sistema de biblioteca.</p>

        @if(lector == null && returnTo != null && !returnTo.isBlank() && !"/biblioteca/historial".equals(returnTo))
            <div class="bib-alert bib-alert-error" style="margin-bottom:20px;">
                <strong>Perfil incompleto:</strong> Debes registrar tus datos de lector (Nombre completo, Correo electrónico y RUT) antes de poder realizar solicitudes de libros y acceder a tu historial.
            </div>
        @endif

        @if(lector != null)
            <div class="bib-card">
                @if(guardado)
                    <div class="bib-alert bib-alert-success" style="margin-bottom:20px;">
                        Tus datos de lector han sido registrados correctamente.
                    </div>
                @endif
                <div style="margin-bottom:15px;">
                    <span style="display:block;font-size:0.9em;color:#666;text-transform:uppercase;font-weight:600;">Nombre completo</span>
                    <div style="font-size:1.15em;font-weight:600;color:var(--bib-text);margin-top:2px;">${lector.getNombre()}</div>
                </div>
                <div style="margin-bottom:15px;">
                    <span style="display:block;font-size:0.9em;color:#666;text-transform:uppercase;font-weight:600;">Correo electrónico</span>
                    <div style="font-size:1.15em;font-weight:600;color:var(--bib-text);margin-top:2px;">${lector.getCorreo()}</div>
                </div>
                <div style="margin-bottom:20px;">
                    <span style="display:block;font-size:0.9em;color:#666;text-transform:uppercase;font-weight:600;">RUT</span>
                    <div style="font-size:1.15em;font-weight:600;color:var(--bib-text);margin-top:2px;">${lector.getRut()}</div>
                </div>
                <div style="margin-bottom:20px;">
                    <span class="bib-badge bib-badge-activo" style="display:inline-block;padding:6px 12px;font-size:0.85em;">Perfil Activo</span>
                </div>
                <div style="margin-top:10px;">
                    <a href="/biblioteca/libros" class="bib-btn bib-btn-primary">Volver al catálogo</a>
                </div>
            </div>
        @else
            @if(error != null)
                <div class="bib-alert bib-alert-error">${error}</div>
            @endif

            <div class="bib-card">
                <form method="post" action="/biblioteca/mis-datos">
                    <input type="hidden" name="returnTo" value="${returnTo}"/>
                    <div class="bib-form-group">
                        <label>Nombre completo</label>
                        <input type="text" name="nombre" class="bib-form-input" placeholder="Ej: Juan Perez" value="${nombre != null ? nombre : ""}" required/>
                    </div>
                    <div class="bib-form-group">
                        <label>Correo electrónico</label>
                        <input type="email" name="correo" class="bib-form-input" placeholder="juan.perez@ucn.cl" value="${correo != null ? correo : ""}" readonly required/>
                    </div>
                    <div class="bib-form-group">
                        <label>RUT (formato 12345678-5)</label>
                        <input type="text" name="rut" id="rutInput" class="bib-form-input" placeholder="12345678-5" value="${rut != null ? rut : ""}" maxlength="10" required oninput="formatRut(this)"/>
                    </div>
                    <script>
                        function formatRut(input) {
                            let v = input.value.replace(/[^0-9kK]/g, '');
                            if (v.length > 1) v = v.slice(0,-1) + '-' + v.slice(-1);
                            input.value = v;
                        }
                    </script>
                    <button type="submit" class="bib-btn bib-btn-primary">Guardar mis datos</button>
                </form>
            </div>
        @endif
    </div>
`
)
