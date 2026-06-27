@param String title
@param String usuarioNombre
@param String usuarioRol

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`

    <section class="dashboard">

        <h1>Registrar Usuario y Vehículo</h1>

        <p>
            <a href="/estacionamientos">Volver a estacionamientos</a>
        </p>

        <form method="post" action="/estacionamientos/usuarios-vehiculos/nuevo">

            <h3>Datos del usuario</h3>

            <label>Nombre</label>
            <input type="text" name="nombre" required>

            <label>Correo</label>
            <input type="email" name="correo" required>

            <label>Contraseña</label>
            <input type="password" name="password" required>

            <label>Rol</label>
            <select name="rol" required>
                <option value="DOCENTE">Docente</option>
                <option value="ESTUDIANTE">Estudiante</option>
            </select>

            <h3>Datos del vehículo</h3>

            <label>Patente</label>
            <input type="text" name="patente" maxlength="10" required>

            <label>Marca</label>
            <input type="text" name="marca" required>

            <label>Modelo</label>
            <input type="text" name="modelo" required>

            <br>

            <button type="submit">
                Registrar usuario y vehículo
            </button>

        </form>

    </section>

`
)