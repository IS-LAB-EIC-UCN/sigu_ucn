@param String title
@param String usuarioNombre
@param String usuarioRol = null
@param cl.ucn.app.model.Espacio espacio = null
@param String error

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
usuarioRol = usuarioRol,
content = @`
    <section class="page-section">
        @if(espacio != null)
            <div class="page-header">
                <h1>Editar Ubicación</h1>
                <a href="/espacios" class="btn-secondary">Volver</a>
            </div>
        @else
            <div class="page-header">
                <h1>Nueva Ubicación</h1>
                <a href="/espacios" class="btn-secondary">Volver</a>
            </div>
        @endif

        @if(error != null && !error.isBlank())
            <div class="alert alert-error">${error}</div>
        @endif

        <form method="post" action="/espacios/registrar" class="form-card">
            @if(espacio != null)
                <input type="hidden" name="id" value="${espacio.getId()}">
            @endif

            <div class="form-row">
                <div class="form-group">
                    <label for="nombre">Nombre del espacio *</label>
                    @if(espacio != null)
                        <input type="text" id="nombre" name="nombre" placeholder="Ej: Sala C201" value="${espacio.getNombre()}" required>
                    @else
                        <input type="text" id="nombre" name="nombre" placeholder="Ej: Sala C201" value="" required>
                    @endif
                </div>

                <div class="form-group">
                    <label for="tipo">Tipo *</label>
                    <select id="tipo" name="tipo" required>
                        <option value="">Seleccione un tipo</option>
                        @if(espacio != null && espacio.getTipo().equals("Sala"))
                            <option value="Sala" selected>Sala</option>
                        @else
                            <option value="Sala">Sala</option>
                        @endif
                        @if(espacio != null && espacio.getTipo().equals("Laboratorio"))
                            <option value="Laboratorio" selected>Laboratorio</option>
                        @else
                            <option value="Laboratorio">Laboratorio</option>
                        @endif
                        @if(espacio != null && espacio.getTipo().equals("Auditorio"))
                            <option value="Auditorio" selected>Auditorio</option>
                        @else
                            <option value="Auditorio">Auditorio</option>
                        @endif
                        @if(espacio != null && espacio.getTipo().equals("Oficina"))
                            <option value="Oficina" selected>Oficina</option>
                        @else
                            <option value="Oficina">Oficina</option>
                        @endif
                        @if(espacio != null && espacio.getTipo().equals("Salón"))
                            <option value="Salón" selected>Salón</option>
                        @else
                            <option value="Salón">Salón</option>
                        @endif
                    </select>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="capacidad">Capacidad máxima *</label>
                    @if(espacio != null)
                        <input type="number" id="capacidad" name="capacidad" min="1" placeholder="Ej: 30" value="${espacio.getCapacidad()}" required>
                    @else
                        <input type="number" id="capacidad" name="capacidad" min="1" placeholder="Ej: 30" value="" required>
                    @endif
                </div>

                <div class="form-group">
                    <label class="checkbox-label-inline">
                        @if(espacio != null && espacio.getDisponible() != null && espacio.getDisponible())
                            <input type="checkbox" id="disponible" name="disponible" checked>
                        @else
                            <input type="checkbox" id="disponible" name="disponible">
                        @endif
                        <span>Disponible para uso</span>
                    </label>
                </div>
            </div>

            <div class="form-actions">
                @if(espacio != null)
                    <button type="submit" class="btn-primary">Guardar Cambios</button>
                @else
                    <button type="submit" class="btn-primary">Registrar Espacio</button>
                @endif
                <a href="/espacios" class="btn-secondary">Cancelar</a>
            </div>
        </form>
    </section>
`
)
