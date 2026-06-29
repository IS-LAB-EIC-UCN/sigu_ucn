@import cl.ucn.app.model.biblioteca.Lector
@param String usuarioNombre
@param Lector lector
@param String error

@template.layout.main(
title = "Editar Lector - SIGU-UCN",
usuarioNombre = usuarioNombre,
content = @`
    <link rel="stylesheet" href="/css/biblioteca.css">
    <script src="/js/biblioteca.js"></script>

    <div class="bib-container">
        <p><a href="/biblioteca/lectores" class="bib-action-link">&larr; Volver a lectores</a></p>

        <h1>Editar Lector</h1>

        @if(error != null)
            <div class="bib-alert bib-alert-error">${error}</div>
        @endif

        <div class="bib-card">
            <form method="post" action="/biblioteca/lectores/editar">
                <input type="hidden" name="id" value="${lector.getId()}"/>
                <div class="bib-form-group">
                    <label>Nombre completo</label>
                    <input type="text" name="nombre" class="bib-form-input" placeholder="Ej: Juan Perez" value="${lector.getNombre()}" required/>
                </div>
                <div class="bib-form-group">
                    <label>Correo electrónico</label>
                    <input type="text" name="correo" class="bib-form-input" placeholder="juan.perez@ucn.cl" value="${lector.getCorreo()}" required/>
                </div>
                <div class="bib-form-group">
                    <label>RUT</label>
                    <input type="text" name="rut" id="rutInput" class="bib-form-input" value="${lector.getRut()}" maxlength="10" required oninput="formatRut(this)"/>
                </div>
                <script>
                    function formatRut(input) {
                        let v = input.value.replace(/[^0-9kK]/g, '');
                        if (v.length > 1) v = v.slice(0,-1) + '-' + v.slice(-1);
                        input.value = v;
                    }
                </script>
                <button type="submit" class="bib-btn bib-btn-primary">Guardar cambios</button>
                <a href="/biblioteca/lectores/eliminar?id=${lector.getId()}" class="bib-btn bib-btn-danger bib-confirm-delete" data-message="¿Esta seguro de que desea eliminar al lector '${lector.getNombre()}'?">Eliminar</a>
            </form>
        </div>
    </div>
`
)
