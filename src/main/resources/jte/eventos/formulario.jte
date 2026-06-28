@param String title
@param String usuarioNombre
@param String usuarioRol = null
@param java.util.List<cl.ucn.app.model.Espacio> espacios
@param java.util.List<cl.ucn.app.model.Expositor> expositores
@param String error

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
usuarioRol = usuarioRol,
content = @`
    <section class="page-section">
        <div class="page-header">
            <h1>Registrar Evento</h1>
            <a href="/eventos" class="btn-secondary">Volver</a>
        </div>

        @if(error != null && !error.isBlank())
            <div class="alert alert-error">${error}</div>
        @endif

        <form method="post" action="/eventos/registrar" class="form-card">
            <div class="form-row">
                <div class="form-group">
                    <label for="titulo">Título del evento *</label>
                    <input type="text" id="titulo" name="titulo" placeholder="Ej: Charla de IA" required>
                </div>

                <div class="form-group">
                    <label for="tematica">Temática</label>
                    <input type="text" id="tematica" name="tematica" placeholder="Ej: Tecnología, Ciencia">
                </div>
            </div>

            <div class="form-group">
                <label for="descripcion">Descripción</label>
                <textarea id="descripcion" name="descripcion" rows="3" placeholder="Breve descripción del evento"></textarea>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="fecha">Fecha *</label>
                    <input type="date" id="fecha" name="fecha" required>
                </div>
                <div class="form-group">
                    <label for="horaInicio">Hora inicio *</label>
                    <input type="time" id="horaInicio" name="horaInicio" required>
                </div>
                <div class="form-group">
                    <label for="horaFin">Hora fin *</label>
                    <input type="time" id="horaFin" name="horaFin" required>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="espacioId">Ubicación / Espacio *</label>
                    <select id="espacioId" name="espacioId" required>
                        <option value="">Seleccione un espacio</option>
                        @for(var espacio : espacios)
                            <option value="${espacio.getId()}">
                                ${espacio.getNombre()} (Cap. ${espacio.getCapacidad()})
                            </option>
                        @endfor
                    </select>
                </div>

                <div class="form-group">
                    <label for="capacidad">Cupo máximo *</label>
                    <input type="number" id="capacidad" name="capacidad" min="1" placeholder="Ej: 30" required>
                </div>
            </div>

            <div class="form-group">
                <label>Expositores</label>
                <div class="checkbox-grid">
                    @if(expositores.isEmpty())
                        <p class="text-muted">No hay expositores registrados. <a href="/expositores/registrar">Crear expositor</a></p>
                    @else
                        @for(var expositor : expositores)
                            <label class="checkbox-label">
                                <input type="checkbox" name="expositorIds" value="${expositor.getId()}">
                                <span>${expositor.getNombre()}</span>
                                @if(expositor.getAfiliacion() != null)
                                    <small>(${expositor.getAfiliacion()})</small>
                                @endif
                            </label>
                        @endfor
                    @endif
                </div>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn-primary">Registrar Evento</button>
                <a href="/eventos" class="btn-secondary">Cancelar</a>
            </div>
        </form>
    </section>
`
)
