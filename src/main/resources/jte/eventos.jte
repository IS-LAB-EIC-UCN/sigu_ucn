
@param String title
@param String usuarioNombre
@param java.util.List<cl.ucn.app.model.Evento> eventos

@template.layout.main(
    title = title,
    usuarioNombre = usuarioNombre,
    content = @`
        <section class="dashboard">
            <h1>Gestión de Eventos Académicos</h1>

            <p>Bienvenido al módulo del Grupo 6.</p>

            <div class="card-grid">
                <div class="info-card">
                    <h3>Eventos académicos</h3>
                    <p>Administración de eventos universitarios.</p>
                </div>

                <div class="info-card">
                    <h3>Inscripciones</h3>
                    <p>Gestión de inscripciones de participantes.</p>
                </div>
            </div>

            <h2>Listado de eventos</h2>

            @if(eventos.isEmpty())
                <p>No hay eventos registrados actualmente.</p>
            @else
                <table>
                    <thead>
                        <tr>
                            <th>Título</th>
                            <th>Fecha</th>
                            <th>Horario</th>
                            <th>Lugar</th>
                            <th>Cupos</th>
                            <th>Estado</th>
                        </tr>
                    </thead>
                    <tbody>
                        @for(var evento : eventos)
                            <tr>
                                <td>${evento.getTitulo()}</td>
                                <td>${evento.getFecha().toString()}</td>
                                <td>
                                    ${evento.getHoraInicio().toString()}
                                    -
                                    ${evento.getHoraFin().toString()}
                                </td>
                                <td>${evento.getLugar()}</td>
                                <td>${evento.getCupoMaximo()}</td>
                                <td>${evento.getEstado()}</td>
                            </tr>
                        @endfor
                    </tbody>
                </table>
            @endif
        </section>
    `
)
