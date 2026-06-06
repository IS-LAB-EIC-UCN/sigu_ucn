@import java.util.List
@import cl.ucn.app.model.biblioteca.Lector
@param String usuarioNombre
@param List<Lector> lectores
@param String error

@template.layout.main(
title = "Gestión de Lectores - SIGU-UCN",
usuarioNombre = usuarioNombre,
content = @`
    <h1>Gestión de Lectores</h1>

    @if(error != null)
        <p style="color:red">${error}</p>
    @endif

    <h2>Registrar nuevo lector</h2>
    <form method="post" action="/biblioteca/lectores/nuevo">
        <input type="text" name="nombre" placeholder="Nombre completo" required/><br/>
        <input type="text" name="correo" placeholder="Correo" required/><br/>
        <input type="text" name="rut" placeholder="RUT (ej: 12345678-9)" required/><br/>
        <button type="submit">Registrar</button>
    </form>

    <h2>Lectores registrados</h2>
    <table border="1">
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
`
)