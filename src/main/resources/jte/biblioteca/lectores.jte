@import java.util.List
@import cl.ucn.app.model.biblioteca.Lector
@param String usuarioNombre
@param List<Lector> lectores
@param String error

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
                <input type="text" name="nombre" placeholder="Ej: Juan Pérez" required/>
            </div>
            <div class="form-group">
                <label>Correo electrónico</label>
                <input type="text" name="correo" placeholder="Ej: juan.perez@ucn.cl" required/>
            </div>
            <div class="form-group">
                <label>RUT</label>
                <input type="text" name="rut" placeholder="Ej: 12345678-9" required/>
            </div>
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
                </tr>
            </thead>
            <tbody>
                @for(Lector l : lectores)
                <tr>
                    <td>${l.getNombre()}</td>
                    <td>${l.getCorreo()}</td>
                    <td>${l.getRut()}</td>
                    <td>${l.isBloqueado() ? "BLOQUEADO" : "ACTIVO"}</td>
                </tr>
                @endfor
            </tbody>
        </table>
    @else
        <p>No hay lectores registrados.</p>
    @endif
`
)
