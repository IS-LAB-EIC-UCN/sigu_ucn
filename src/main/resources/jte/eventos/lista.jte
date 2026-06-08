@param String title
@param String usuarioNombre
@param java.util.List<cl.ucn.app.model.Evento> eventos
@param String error
@param String success

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <section class="page-section">
        <div class="page-header">
            <h1>Gestión de Eventos</h1>
            <a href="/eventos/registrar" class="btn-primary">+ Nuevo Evento</a>
        </div>

        <nav class="tab-nav">
            <a href="/eventos" class="tab tab-active">Eventos</a>
            <a href="/expositores" class="tab">Expositores</a>
        </nav>

        @if(error != null && !error.isBlank())
            <div class="alert alert-error">${error}</div>
        @endif
        @if(success != null && !success.isBlank())
            <div class="alert alert-success">${success}</div>
        @endif

        <div class="table-container">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Título</th>
                        <th>Fecha</th>
                        <th>Horario</th>
                        <th>Espacio</th>
                        <th>Temática</th>
                        <th>Expositores</th>
                        <th>Cupo</th>
                        <th>Estado</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    @if(eventos.isEmpty())
                        <tr>
                            <td colspan="9" class="empty-table">No hay eventos registrados.</td>
                        </tr>
                    @else
                        @for(var evento : eventos)
                            <tr>
                                <td>${evento.getTitulo()}</td>
                                <td>${evento.getFecha().toString()}</td>
                                <td>${evento.getHoraInicio().toString()} - ${evento.getHoraFin().toString()}</td>
                                <td>${evento.getEspacio().getNombre()}</td>
                                <td>${evento.getTematica() != null ? evento.getTematica() : "-"}</td>
                                <td>
                                    @if(evento.getExpositores() != null && !evento.getExpositores().isEmpty())
                                        ${evento.getExpositores().stream().map(ex -> ex.getNombre()).collect(java.util.stream.Collectors.joining(", "))}
                                    @else
                                        -
                                    @endif
                                </td>
                                <td>${evento.getCapacidad()}</td>
                                <td>
                                    <span class="status-badge status-${evento.getEstado().toLowerCase()}">${evento.getEstado()}</span>
                                </td>
                                <td>
                                    @if(evento.getEstado().equals("CANCELADO"))
                                        <form method="POST" action="/eventos/eliminar" style="display:inline;">
                                            <input type="hidden" name="id" value="${evento.getId()}">
                                            <button type="submit" class="btn-danger btn-sm" onclick="return confirm('¿Estás seguro de eliminar permanentemente este evento?')">Eliminar</button>
                                        </form>
                                    @else
                                        <form method="POST" action="/eventos/cancelar" style="display:inline;">
                                            <input type="hidden" name="id" value="${evento.getId()}">
                                            <button type="submit" class="btn-warning btn-sm" onclick="return confirm('¿Estás seguro de cancelar este evento?')">Cancelar</button>
                                        </form>
                                    @endif
                                </td>
                            </tr>
                        @endfor
                    @endif
                </tbody>
            </table>
        </div>
    </section>
`
)
