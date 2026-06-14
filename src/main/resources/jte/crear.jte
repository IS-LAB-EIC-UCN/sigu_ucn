@param String title
@param String usuarioNombre
@param java.util.List<cl.ucn.app.model.Asignatura> asignaturas
@param String usuarioRol

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <section class="dashboard">

        @if("ADMIN".equals(usuarioRol))
            <h1>Crear Asignatura</h1>

            <a href="/tutorias">← Volver al listado</a>

            <br><br>

            <form action="/tutorias/asignaturas" method="post">
                <label>Código:</label>
                <input type="text" name="codigo" required>

                <br><br>

                <label>Nombre:</label>
                <input type="text" name="nombre" required>

                <br><br>

                <button type="submit">Guardar Asignatura</button>
            </form>
        @endif

        @if("TUTOR".equals(usuarioRol))
            <h1>Crear Tutoría</h1>

            <a href="/tutorias">← Volver al listado</a>

            <br><br>

            <form action="/tutorias/guardar" method="post">
                <label>Asignatura:</label>

                <select name="asignaturaId" required>
                    <option value="">Seleccione una asignatura</option>

                    @for(cl.ucn.app.model.Asignatura a : asignaturas)
                        <option value="${a.getId()}">
                            ${a.getCodigo()} - ${a.getNombre()}
                        </option>
                    @endfor
                </select>

                <br><br>

                <label>Fecha:</label>
                <input type="date" name="fecha" required>

                <br><br>

                <label>Hora inicio:</label>
                <input type="time" name="horaInicio" required>

                <br><br>

                <label>Hora fin:</label>
                <input type="time" name="horaFin" required>

                <br><br>

                <button type="submit">Publicar Tutoría</button>
            </form>
        @endif

        @if(!"ADMIN".equals(usuarioRol) && !"TUTOR".equals(usuarioRol))
            <h1>Sin permisos</h1>
            <p>No tienes permisos para crear asignaturas ni publicar tutorías.</p>

            <a href="/tutorias">← Volver al listado</a>
        @endif

    </section>
`
)