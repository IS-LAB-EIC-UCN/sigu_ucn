@param String title
@param String usuarioNombre
@param String usuarioRol = null
@param java.util.List<cl.ucn.app.model.Expositor> expositores
@param String error
@param String success

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
usuarioRol = usuarioRol,
content = @`
    <section class="page-section">
        <div class="page-header">
            <h1>Gestión de Expositores</h1>
            <a href="/expositores/registrar" class="btn-primary">+ Nuevo Expositor</a>
        </div>

        <nav class="tab-nav">
            <a href="/eventos" class="tab">Eventos</a>
            <a href="/expositores" class="tab tab-active">Expositores</a>
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
                        <th>Email</th>
                        <th>Afiliación</th>
                        <th>Teléfono</th>
                        <th>Biografía</th>
                    </tr>
                </thead>
                <tbody>
                    @if(expositores.isEmpty())
                        <tr>
                            <td colspan="5" class="empty-table">No hay expositores registrados.</td>
                        </tr>
                    @else
                        @for(var expositor : expositores)
                            <tr>
                                <td>${expositor.getNombre()}</td>
                                <td>${expositor.getEmail() != null ? expositor.getEmail() : "-"}</td>
                                <td>${expositor.getAfiliacion() != null ? expositor.getAfiliacion() : "-"}</td>
                                <td>${expositor.getTelefono() != null ? expositor.getTelefono() : "-"}</td>
                                <td>${expositor.getBio() != null ? expositor.getBio() : "-"}</td>
                            </tr>
                        @endfor
                    @endif
                </tbody>
            </table>
        </div>
    </section>
`
)
