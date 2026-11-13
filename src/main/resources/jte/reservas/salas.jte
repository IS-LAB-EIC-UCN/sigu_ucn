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

        <table>
            <thead>
            <tr>
                <th>Nombre</th>
                <th>Tipo</th>
                <th>Capacidad</th>
            </tr>
            </thead>
            <tbody>
            @for(Espacio sala : salas)
                <tr>
                    <td>${sala.getNombre()}</td>
                    <td>${sala.getTipo()}</td>
                    <td>${sala.getCapacidad()}</td>
                </tr>
            @endfor
            </tbody>
        </table>
    </section>
`
)