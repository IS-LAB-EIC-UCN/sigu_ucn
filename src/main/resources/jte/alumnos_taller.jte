@import java.util.List
@import cl.ucn.app.model.Inscripcion

@param String title
@param String usuarioNombre
@param String rol
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

        .table-container {
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
            overflow: hidden;
            border: 1px solid #e2e8f0;
        }

        .modern-table {
            width: 100%;
            border-collapse: collapse;
            text-align: left;
        }

        .modern-table th {
            background-color: #f1f5f9;
            color: #475569;
            font-weight: 600;
            padding: 1rem 1.5rem;
            border-bottom: 1px solid #e2e8f0;
        }

        .modern-table td {
            padding: 1rem 1.5rem;
            border-bottom: 1px solid #e2e8f0;
            color: #1e293b;
        }

        .modern-table tr:last-child td {
            border-bottom: none;
        }

        .modern-table tr:hover {
            background-color: #f8fafc;
        }

        .badge {
            display: inline-block;
            padding: 0.25rem 0.75rem;
            border-radius: 9999px;
            font-size: 0.85rem;
            font-weight: 600;
        }

        .badge-inscrito { background: #d1fae5; color: #065f46; }
        .badge-espera { background: #fef3c7; color: #92400e; }
        .badge-cancelado { background: #fee2e2; color: #991b1b; }

        .empty-state {
            padding: 3rem;
            text-align: center;
            color: #64748b;
        }
    </style>

    <div class="taller-container">
        <div class="taller-header">
            <div>
                <h1>Gestión de Alumnos</h1>
                <p>Lista de estudiantes registrados en tu taller.</p>
            </div>
            <a href="/talleres" class="btn-back">&larr; Volver a Mis Talleres</a>
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
                                    @else
                                        <span class="badge badge-cancelado">Cancelado</span>
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
