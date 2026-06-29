@import java.util.List
@import cl.ucn.app.model.biblioteca.Lector
@param String usuarioNombre
@param String usuarioRol
@param List<Lector> lectores
@param String error
@param String nombre
@param String correo
@param String rut

@template.layout.main(
title = "Gestión de Lectores - SIGU-UCN",
usuarioNombre = usuarioNombre,
content = @`
    <link rel="stylesheet" href="/css/biblioteca.css">
    <script src="/js/biblioteca.js"></script>

    <div class="bib-container">
        <p><a href="/biblioteca/libros" class="bib-action-link">&larr; Volver al catálogo</a></p>

        <h1>Gestión de Lectores</h1>

        @if(error != null)
            <div class="bib-alert bib-alert-error">${error}</div>
        @endif

        @if(usuarioRol != null && usuarioRol.equals("ADMIN"))
            <div class="bib-card">
                <h2>Registrar nuevo lector</h2>
                <form method="post" action="/biblioteca/lectores/nuevo">
                    <div class="bib-form-group">
                        <label>Nombre completo</label>
                        <input type="text" name="nombre" class="bib-form-input" placeholder="Ej: Juan Perez" value="${nombre != null ? nombre : ""}" required/>
                    </div>
                    <div class="bib-form-group">
                        <label>Correo electrónico</label>
                        <input type="text" name="correo" class="bib-form-input" placeholder="juan.perez@ucn.cl" value="${correo != null ? correo : ""}" required/>
                    </div>
                    <div class="bib-form-group">
                        <label>RUT</label>
                        <input type="text" name="rut" id="rutInput" class="bib-form-input" placeholder="12345678-5" value="${rut != null ? rut : ""}" maxlength="10" required oninput="formatRut(this)"/>
                    </div>
                    <script>
                        function formatRut(input) {
                            let v = input.value.replace(/[^0-9kK]/g, '');
                            if (v.length > 1) v = v.slice(0,-1) + '-' + v.slice(-1);
                            input.value = v;
                        }
                    </script>
                    <button type="submit" class="bib-btn bib-btn-primary">Registrar lector</button>
                </form>
            </div>
        @endif

        <h2>Lectores registrados</h2>
        @if(lectores != null && !lectores.isEmpty())
            <table class="bib-table">
                <thead>
                    <tr>
                        <th>Nombre</th>
                        <th>Correo</th>
                        <th>RUT</th>
                        <th>Estado</th>
                        @if(usuarioRol != null && usuarioRol.equals("ADMIN"))
                            <th>Acciones</th>
                        @endif
                    </tr>
                </thead>
                <tbody>
                    @for(Lector l : lectores)
                    <tr>
                        <td class="bib-text-primary">${l.getNombre()}</td>
                        <td>${l.getCorreo()}</td>
                        <td>${l.getRut()}</td>
                        <td>
                            @if(l.isBloqueado())
                                <span class="bib-badge bib-badge-bloqueado">Bloqueado</span>
                            @else
                                <span class="bib-badge bib-badge-disponible">Activo</span>
                            @endif
                        </td>
                        @if(usuarioRol != null && usuarioRol.equals("ADMIN"))
                            <td>
                                <a href="/biblioteca/lectores/editar?id=${l.getId()}" class="bib-action-link">Editar</a>
                            </td>
                        @endif
                    </tr>
                    @endfor
                </tbody>
            </table>
        @else
            <p>No hay lectores registrados.</p>
        @endif
    </div>
`
)
