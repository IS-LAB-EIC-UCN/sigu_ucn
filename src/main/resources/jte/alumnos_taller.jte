@import java.util.List
@import cl.ucn.app.model.Inscripcion

@param String title
@param String usuarioNombre
@param String rol
@param Long tallerId
@param List<Inscripcion> inscripciones

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
        
        .taller-header p {
            color: #64748b;
            font-size: 1.1rem;
            margin: 0;
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

        .btn-csv {
            display: inline-block;
            padding: 0.6rem 1.2rem;
            background: #10b981;
            color: white;
            border: none;
            border-radius: 8px;
            font-weight: bold;
            text-decoration: none;
            transition: all 0.2s;
            margin-right: 1rem;
        }

        .btn-csv:hover {
            background: #059669;
        }
    </style>

    <div class="taller-container">
        <div class="taller-header">
            <div>
                <h1>Gestión de Alumnos</h1>
                <p>Lista de estudiantes registrados en tu taller.</p>
            </div>
            <div>
                <a href="/talleres/${tallerId}/alumnos/csv" class="btn-csv">Descargar CSV</a>
                <a href="/talleres" class="btn-back">&larr; Volver a Mis Talleres</a>
            </div>
        </div>

        <div class="table-container">
            @if(inscripciones == null || inscripciones.isEmpty())
                <div class="empty-state">
                    <h3>Aún no hay alumnos</h3>
                    <p>Cuando los estudiantes se inscriban a este taller, aparecerán aquí.</p>
                </div>
            @else
                <table class="modern-table">
                    <thead>
                        <tr>
                            <th>Nombre del Alumno</th>
                            <th>Correo Institucional</th>
                            <th>Estado</th>
                            <th>Justificación</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        @for(Inscripcion i : inscripciones)
                            <tr>
                                <td><strong>${i.getUsuario().getNombre()}</strong></td>
                                <td style="color: #64748b;">${i.getUsuario().getCorreo()}</td>
                                <td>
                                    @if("INSCRITO".equals(i.getEstado()))
                                        <span class="badge badge-inscrito">Cupo Confirmado</span>
                                    @elseif("EN_ESPERA".equals(i.getEstado()))
                                        <span class="badge badge-espera">En Lista de Espera</span>
                                    @elseif("ANULACION_PENDIENTE".equals(i.getEstado()))
                                        <span class="badge" style="background: #fef08a; color: #854d0e;">Anulación Pendiente</span>
                                    @else
                                        <span class="badge badge-cancelado">Cancelado</span>
                                    @endif
                                </td>
                                <td>
                                    @if(i.getJustificacion() != null)
                                        <small style="color: #475569;">${i.getJustificacion()}</small>
                                    @else
                                        <span style="color: #cbd5e1;">-</span>
                                    @endif
                                </td>
                                <td>
                                    @if("ANULACION_PENDIENTE".equals(i.getEstado()))
                                        <div style="display: flex; gap: 0.5rem;">
                                            <form action="/talleres/${tallerId}/procesar-anulacion" method="POST" style="margin: 0;">
                                                <input type="hidden" name="usuarioId" value="${i.getUsuario().getId()}">
                                                <input type="hidden" name="aprobada" value="true">
                                                <button type="submit" style="background: #10b981; color: white; border: none; padding: 0.4rem 0.8rem; border-radius: 6px; cursor: pointer; font-size: 0.85rem;">Aprobar</button>
                                            </form>
                                            <form action="/talleres/${tallerId}/procesar-anulacion" method="POST" style="margin: 0;">
                                                <input type="hidden" name="usuarioId" value="${i.getUsuario().getId()}">
                                                <input type="hidden" name="aprobada" value="false">
                                                <button type="submit" style="background: #ef4444; color: white; border: none; padding: 0.4rem 0.8rem; border-radius: 6px; cursor: pointer; font-size: 0.85rem;">Rechazar</button>
                                            </form>
                                        </div>
                                    @else
                                        <span style="color: #cbd5e1;">-</span>
                                    @endif
                                </td>
                            </tr>
                        @endfor
                    </tbody>
                </table>
            @endif
        </div>
    </div>
    `
)
