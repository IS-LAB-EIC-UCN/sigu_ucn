@import java.util.List
@import cl.ucn.app.model.biblioteca.Lector
@param String usuarioNombre
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

    <p><a href="/biblioteca/libros">Volver al catálogo</a></p>

    <h1>Gestión de Lectores</h1>

    @if(error != null)
        <div class="alert-error">${error}</div>
    @endif

    <div class="info-card">
        <h2>Registrar nuevo lector</h2>
        <form method="post" action="/biblioteca/lectores/nuevo">
            <div class="form-group">
                <label>Nombre completo</label>
                <input type="text" name="nombre" placeholder="Ej: Juan Perez" value="${nombre != null ? nombre : ""}" required/>
            </div>
            <div class="form-group">
                <label>Correo electronico</label>
                <input type="text" name="correo" placeholder="juan.perez@ucn.cl" value="${correo != null ? correo : ""}" required/>
            </div>
            <div class="form-group">
                <label>RUT</label>
                <input type="text" name="rut" id="rutInput" placeholder="12345678-5" value="${rut != null ? rut : ""}" maxlength="10" required oninput="formatRut(this)"/>
            </div>
            <script>
                function formatRut(input) {
                    let v = input.value.replace(/[^0-9kK]/g, '');
                    if (v.length > 1) v = v.slice(0,-1) + '-' + v.slice(-1);
                    input.value = v;
                }
            </script>
            <button type="submit" class="primary-btn">Registrar lector</button>
        </form>
    </div>

    <h2>Lectores registrados</h2>
    @if(lectores != null && !lectores.isEmpty())
        <table class="data-table">
            <thead>
                <tr>
                    <th>Nombre</th>
                    <th>Correo</th>
                    <th>RUT</th>
                    <th>Estado</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                @for(Lector l : lectores)
                <tr>
                    <td>${l.getNombre()}</td>
                    <td>${l.getCorreo()}</td>
                    <td>${l.getRut()}</td>
                    <td>${l.isBloqueado() ? "BLOQUEADO" : "ACTIVO"}</td>
                    <td>
                        <a href="/biblioteca/lectores/editar?id=${l.getId()}">Editar</a>
                        <a href="/biblioteca/lectores/eliminar?id=${l.getId()}" style="color:#a83232;margin-left:8px;">Eliminar</a>
                    </td>
                </tr>
                @endfor
            </tbody>
        </table>
    @else
        <p>No hay lectores registrados.</p>
    @endif
`
)
