@param String title
@param String usuarioNombre
@param String usuarioRol = null
@param Long usuarioId = null
@param java.util.List<cl.ucn.app.model.Evento> eventos
@param String error
@param String success

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
usuarioRol = usuarioRol,
content = @`
    <section class="page-section">
        <div class="page-header">
            <h1>Gestión de Eventos</h1>
            @if("ADMIN".equals(usuarioRol))
                <a href="/eventos/registrar" class="btn-primary">+ Nuevo Evento</a>
            @endif
        </div>

        <nav class="tab-nav">
            <a href="/eventos" class="tab tab-active">Eventos</a>
            @if("ADMIN".equals(usuarioRol))
                <a href="/expositores" class="tab">Expositores</a>
            @endif
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
                        <th>Cupos Libres</th>
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
                            !{int cuposDisponibles = evento.getCapacidad() - (evento.getInscripciones() != null ? evento.getInscripciones().size() : 0);}
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
                                <td>${cuposDisponibles} / ${evento.getCapacidad()}</td>
                                <td>
                                    <span class="status-badge status-${evento.getEstado().toLowerCase()}">${evento.getEstado()}</span>
                                </td>
                                <td>
                                    @if("ADMIN".equals(usuarioRol))
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
                                    @elseif("DOCENTE".equals(usuarioRol) || "ESTUDIANTE".equals(usuarioRol))
                                        @if(evento.getEstado().equals("CANCELADO"))
                                            <span class="text-muted">Cancelado</span>
                                        @else
                                            !{boolean estaInscrito = false;}
                                            @if(usuarioId != null && evento.getInscripciones() != null)
                                                !{estaInscrito = evento.getInscripciones().stream().anyMatch(i -> i.getUsuario().getId().equals(usuarioId));}
                                            @endif
                                            @if(estaInscrito)
                                                <form method="POST" action="/eventos/cancelar-inscripcion" style="display:inline;">
                                                    <input type="hidden" name="eventoId" value="${evento.getId()}">
                                                    <button type="submit" class="btn-warning btn-sm">Cancelar Inscripción</button>
                                                </form>
                                            @else
                                                @if(cuposDisponibles > 0)
                                                    <form method="POST" action="/eventos/inscribir" style="display:inline;">
                                                        <input type="hidden" name="eventoId" value="${evento.getId()}">
                                                        <button type="submit" class="btn-primary btn-sm">Inscribirme</button>
                                                    </form>
                                                @else
                                                    <button class="btn-secondary btn-sm" disabled style="opacity: 0.6; cursor: not-allowed;">Agotado</button>
                                                @endif
                                            @endif
                                        @endif
                                    @else
                                        -
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
