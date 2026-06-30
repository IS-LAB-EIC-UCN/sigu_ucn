@param String title
@param String usuarioNombre
@param java.util.List<cl.ucn.app.model.Usuario> tutores
@param java.util.List<cl.ucn.app.model.Usuario> estudiantes
@param String usuarioRol

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <section class="dashboard">

        <h1>Registrar Estudiantes y Tutores</h1>

        <a href="/tutorias">← Volver al listado</a>

        <br><br>

        @if("ADMIN".equals(usuarioRol))

            <form action="/tutorias/usuarios" method="post">

                <label>Nombre:</label>
                <input type="text" name="nombre" required>

                <br><br>

                <label>Correo:</label>
                <input type="email" name="correo" required>

                <br><br>

                <label>Contraseña:</label>
                <input type="password" name="password" required>

                <br><br>

                <label>Rol:</label>

                <select name="rol" required>
                    <option value="ESTUDIANTE">Estudiante</option>
                    <option value="TUTOR">Tutor</option>
                </select>

                <br><br>

                <button type="submit">
                    Registrar Usuario
                </button>

            </form>

            <hr>

            <h2>Estudiantes registrados</h2>

            @if(estudiantes.isEmpty())
                <p>No existen estudiantes registrados.</p>
            @else

                <table border="1">

                    <tr>
                        <th>Nombre</th>
                        <th>Correo</th>
                    </tr>

                    @for(cl.ucn.app.model.Usuario usuario : estudiantes)

                        <tr>
                            <td>${usuario.getNombre()}</td>
                            <td>${usuario.getCorreo()}</td>
                        </tr>

                    @endfor

                </table>

            @endif

            <hr>

            <h2>Tutores registrados</h2>

            @if(tutores.isEmpty())
                <p>No existen tutores registrados.</p>
            @else

                <table border="1">

                    <tr>
                        <th>Nombre</th>
                        <th>Correo</th>
                    </tr>

                    @for(cl.ucn.app.model.Usuario usuario : tutores)

                        <tr>
                            <td>${usuario.getNombre()}</td>
                            <td>${usuario.getCorreo()}</td>
                        </tr>

                    @endfor

                </table>

            @endif

        @else

            <h2>No tienes permisos para acceder a esta sección.</h2>

        @endif

    </section>
`
)