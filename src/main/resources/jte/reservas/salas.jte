
@import cl.ucn.app.model.Espacio
@import java.util.List

@param String title
@param String usuarioNombre
@param List<Espacio> salas

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <section class="dashboard">
        <h1>Salas disponibles</h1>
        <p><a href="/reservas/mis-reservas">Mis reservas</a></p>

        <table>
            <thead>
            <tr>
                <th>Nombre</th>
                <th>Tipo</th>
                <th>Capacidad</th>
                <th>Ubicación</th>
                <th>Acciones</th>
            </tr>
            </thead>
            <tbody>
            @for(Espacio sala : salas)
                <tr>
                    <td>${sala.getNombre()}</td>
                    <td>${sala.getTipo()}</td>
                    <td>${sala.getCapacidad()}</td>
                    <td>${sala.getUbicacion() != null ? sala.getUbicacion() : "-"}</td>
                    <td>
                        <form action="/reservas/crear" method="post">
                            <input type="hidden" name="espacioId" value="${sala.getId()}">

                            <label>Fecha:</label>
                            <input type="date" name="fecha" required>

                            <label>Hora de inicio:</label>
                            <input type="time" name="horaInicio" required>

                            <label>Hora de término:</label>
                            <input type="time" name="horaFin" required>

                            <label>Motivo:</label>
                            <input type="text" name="motivo" maxlength="255" required>

                            <button type="submit">Reservar</button>
                        </form>
                    </td>
                </tr>
            @endfor
            </tbody>
        </table>
    </section>
`
)
