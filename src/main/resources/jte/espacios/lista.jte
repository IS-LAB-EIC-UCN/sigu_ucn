@param String title
@param String usuarioNombre
@param String usuarioRol = null
@param java.util.List<cl.ucn.app.model.Espacio> espacios
@param String error
@param String success

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
usuarioRol = usuarioRol,
content = @`
    <section class="page-section">
        <div class="page-header">
            <h1>Gestión de Ubicaciones</h1>
            <a href="/espacios/registrar" class="btn-primary">+ Nueva Ubicación</a>
        </div>

        <nav class="tab-nav">
            <a href="/eventos" class="tab">Eventos</a>
            <a href="/expositores" class="tab">Expositores</a>
            <a href="/espacios" class="tab tab-active">Ubicaciones</a>
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
                        <th>Nombre</th>
                        <th>Tipo</th>
                        <th>Capacidad</th>
                        <th>Disponible</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    @if(espacios.isEmpty())
                        <tr>
                            <td colspan="5" class="empty-table">No hay ubicaciones registradas.</td>
                        </tr>
                    @else
                        @for(var espacio : espacios)
                            <tr>
                                <td>${espacio.getNombre()}</td>
                                <td>${espacio.getTipo()}</td>
                                <td>${espacio.getCapacidad()}</td>
                                <td>
                                    @if(espacio.getDisponible() != null && espacio.getDisponible())
                                        <span class="status-badge status-activo">Disponible</span>
                                    @else
                                        <span class="status-badge status-cancelado">No disponible</span>
                                    @endif
                                </td>
                                <td>
                                    <a href="/espacios/editar/${espacio.getId()}" class="btn-secondary btn-sm">Editar</a>
                                    <form method="POST" action="/espacios/eliminar" style="display:inline;">
                                        <input type="hidden" name="id" value="${espacio.getId()}">
                                        <button type="submit" class="btn-danger btn-sm" onclick="return confirm('¿Estás seguro de eliminar esta ubicación?')">Eliminar</button>
                                    </form>
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
