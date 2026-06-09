@import cl.ucn.app.model.Taller
@import cl.ucn.app.model.Usuario
@import cl.ucn.app.model.Espacio
@import java.util.List

@param String title
@param String usuarioNombre
@param String rol
@param Taller taller
@param List<Usuario> docentes
@param List<Espacio> espacios
@param String error = null

@template.layout.main(
    title = title,
    usuarioNombre = usuarioNombre,
    content = @`
    <style>
        .taller-container {
            font-family: Arial, sans-serif;
            padding: 2rem;
            background: #f8fafc;
            min-height: 100%;
        }
        
        .taller-header {
            margin-bottom: 2rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .taller-header h1 {
            font-size: 2.2rem;
            font-weight: 700;
            color: #0f172a;
            margin-bottom: 0.5rem;
            margin-top: 0;
        }

        .btn-back {
            display: inline-block;
            padding: 0.6rem 1.2rem;
            background: white;
            color: #3b82f6;
            border: 1px solid #3b82f6;
            border-radius: 8px;
            font-weight: bold;
            text-decoration: none;
            transition: all 0.2s;
        }

        .btn-back:hover {
            background: #eff6ff;
        }

        .form-container {
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
            padding: 2rem;
            max-width: 800px;
            margin: 0 auto;
        }

        .form-group {
            margin-bottom: 1.5rem;
        }

        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 600;
            color: #1e293b;
        }

        .form-control {
            width: 100%;
            padding: 0.75rem;
            border: 1px solid #cbd5e1;
            border-radius: 6px;
            font-size: 1rem;
            box-sizing: border-box;
        }

        .btn-submit {
            background: #3b82f6;
            color: white;
            padding: 0.75rem 1.5rem;
            border: none;
            border-radius: 6px;
            font-size: 1rem;
            font-weight: bold;
            cursor: pointer;
            width: 100%;
        }

        .btn-submit:hover {
            background: #2563eb;
        }
        
        .alert {
            padding: 1rem;
            border-radius: 8px;
            margin-bottom: 1.5rem;
            font-weight: bold;
        }
        .alert-error {
            background-color: #fee2e2;
            color: #991b1b;
            border: 1px solid #f87171;
        }
    </style>

    <div class="taller-container">
        <div class="taller-header">
            <div>
                <h1>Editar Taller</h1>
                <p>Modifica la información del taller seleccionado.</p>
            </div>
            <a href="/talleres" class="btn-back">&larr; Cancelar y Volver</a>
        </div>

        <div class="form-container">
            @if(error != null)
                <div class="alert alert-error">${error}</div>
            @endif

            <form action="/talleres/${taller.getId()}/editar" method="POST">
                <div class="form-group">
                    <label>Nombre del Taller</label>
                    <input type="text" name="nombre" class="form-control" value="${taller.getNombre()}" required>
                </div>
                <div class="form-group">
                    <label>Descripción</label>
                    <input type="text" name="descripcion" class="form-control" value="${taller.getDescripcion()}">
                </div>
                <div class="form-group">
                    <label>Categoría</label>
                    <input type="text" name="categoria" class="form-control" value="${taller.getCategoria()}" required>
                </div>
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label>Cupos Totales (Máximo)</label>
                        <input type="number" name="cupos_totales" class="form-control" min="1" value="${taller.getCuposTotales()}" required>
                        <small style="color: #64748b; font-size: 0.85rem;">No puede ser menor que los alumnos ya inscritos.</small>
                    </div>
                    <div class="form-group">
                        <label>Docente</label>
                        <select name="profesor_id" class="form-control" required>
                            @for(Usuario docente : docentes)
                                <option value="${docente.getId()}" selected="${docente.getId().equals(taller.getProfesor().getId())}">${docente.getNombre()}</option>
                            @endfor
                        </select>
                    </div>
                </div>
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label>Fecha Inicio</label>
                        <input type="date" name="fecha_inicio" class="form-control" value="${taller.getFechaInicio().toString()}" required>
                    </div>
                    <div class="form-group">
                        <label>Fecha Fin</label>
                        <input type="date" name="fecha_fin" class="form-control" value="${taller.getFechaFin().toString()}" required>
                    </div>
                </div>
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label>Espacio</label>
                        <select name="espacio_id" class="form-control">
                            <option value="">-- Por definir --</option>
                            @for(Espacio espacio : espacios)
                                <option value="${espacio.getId()}" selected="${taller.getEspacio() != null && espacio.getId().equals(taller.getEspacio().getId())}">${espacio.getNombre()} (Cap: ${espacio.getCapacidad()})</option>
                            @endfor
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Bloque Horario</label>
                        <select name="bloque_horario" class="form-control" required>
                            <option value="A" selected="${taller.getBloqueHorario().equals('A')}">Bloque A (08:10 - 09:40)</option>
                            <option value="B" selected="${taller.getBloqueHorario().equals('B')}">Bloque B (09:50 - 11:20)</option>
                            <option value="C" selected="${taller.getBloqueHorario().equals('C')}">Bloque C (11:30 - 13:00)</option>
                            <option value="D" selected="${taller.getBloqueHorario().equals('D')}">Bloque D (14:30 - 16:00)</option>
                            <option value="E" selected="${taller.getBloqueHorario().equals('E')}">Bloque E (16:10 - 17:40)</option>
                            <option value="F" selected="${taller.getBloqueHorario().equals('F')}">Bloque F (17:50 - 19:20)</option>
                        </select>
                    </div>
                </div>
                <div class="form-group" style="margin-top: 2rem;">
                    <button type="submit" class="btn-submit">Guardar Cambios</button>
                </div>
            </form>
        </div>
    </div>
    `
)
