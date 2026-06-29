@param String title
@param String usuarioNombre
@param String usuarioRol = null
@param Long usuarioId = null
@param cl.ucn.app.model.Evento evento
@param java.util.List<cl.ucn.app.model.Inscripcion> inscripciones

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
usuarioRol = usuarioRol,
content = @`
    <section class="page-section">
        <div class="page-header">
            <h1>Asistentes: ${evento.getTitulo()}</h1>
            <a href="/eventos" class="btn-secondary">Volver a Eventos</a>
        </div>

        <div class="event-info-cards">
            <div class="info-card">
                <span class="info-label">Fecha</span>
                <span class="info-value">${evento.getFecha().toString()}</span>
            </div>
            <div class="info-card">
                <span class="info-label">Horario</span>
                <span class="info-value">${evento.getHoraInicio().toString()} - ${evento.getHoraFin().toString()}</span>
            </div>
            <div class="info-card">
                <span class="info-label">Espacio</span>
                <span class="info-value">${evento.getEspacio().getNombre()}</span>
            </div>
            <div class="info-card">
                <span class="info-label">Inscritos</span>
                <span class="info-value">${inscripciones.size()} / ${evento.getCapacidad()}</span>
            </div>
        </div>

        <div class="table-container">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Nombre</th>
                        <th>Correo</th>
                        <th>Rol</th>
                        <th>Fecha de Inscripción</th>
                    </tr>
                </thead>
                <tbody>
                    @if(inscripciones.isEmpty())
                        <tr>
                            <td colspan="5" class="empty-table">No hay asistentes inscritos en este evento.</td>
                        </tr>
                    @else
                        @for(var i = 0; i < inscripciones.size(); i++)
                            !{var insc = inscripciones.get(i);}
                            <tr>
                                <td>${i + 1}</td>
                                <td>${insc.getUsuario().getNombre()}</td>
                                <td>${insc.getUsuario().getCorreo()}</td>
                                <td>${insc.getUsuario().getRol().getNombre()}</td>
                                <td>${insc.getFechaInscripcion().toString()}</td>
                            </tr>
                        @endfor
                    @endif
                </tbody>
            </table>
        </div>
    </section>
`
)
