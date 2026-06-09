@import java.util.List
@import cl.ucn.app.model.Taller
@import cl.ucn.app.model.Inscripcion
@import cl.ucn.app.model.Usuario
@import cl.ucn.app.model.Espacio

@param String title
@param String usuarioNombre
@param String rol
@param List<Taller> talleres
@param List<Inscripcion> misInscripciones
@param List<Usuario> docentes
@param List<Espacio> espacios
@param String successMsg = ""
@param String errorMsg = ""

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
        }
        
        .taller-header h1 {
            font-size: 2.5rem;
            font-weight: 700;
            color: #0f172a;
            margin-bottom: 0.5rem;
        }
        
        .taller-header p {
            color: #64748b;
            font-size: 1.1rem;
        }

        .section-title {
            font-size: 1.5rem;
            font-weight: 600;
            color: #1e293b;
            margin-bottom: 1.5rem;
            border-bottom: 2px solid #e2e8f0;
            padding-bottom: 0.5rem;
        }

        .grid-cards {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 1.5rem;
            margin-bottom: 3rem;
        }

        .card {
            background: white;
            border-radius: 12px;
            padding: 1.5rem;
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
            border: 1px solid #e2e8f0;
        }

        .card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
        }

        .card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 4px;
            background: linear-gradient(90deg, #3b82f6, #2563eb);
        }

        .card.card-inscrito::before {
            background: linear-gradient(90deg, #10b981, #059669);
        }
        
        .card.card-espera::before {
            background: linear-gradient(90deg, #f59e0b, #d97706);
        }

        .card-title {
            font-size: 1.25rem;
            font-weight: 700;
            color: #0f172a;
            margin-bottom: 0.5rem;
        }

        .badge {
            display: inline-block;
            padding: 0.25rem 0.75rem;
            border-radius: 9999px;
            font-size: 0.875rem;
            font-weight: 600;
            margin-bottom: 1rem;
        }

        .badge-inscrito { background: #d1fae5; color: #065f46; }
        .badge-espera { background: #fef3c7; color: #92400e; }
        .badge-abierto { background: #dbeafe; color: #1e40af; }

        .card-info {
            color: #475569;
            font-size: 0.95rem;
            margin-bottom: 0.25rem;
        }

        .btn {
            display: block;
            width: 100%;
            padding: 0.75rem;
            border: none;
            border-radius: 8px;
            font-weight: 600;
            text-align: center;
            cursor: pointer;
            transition: background 0.2s;
            margin-top: 1.5rem;
            text-decoration: none;
        }

        .btn-primary {
            background: #3b82f6;
            color: white;
        }
        
        .btn-primary:hover {
            background: #2563eb;
        }

        .btn-danger {
            background: #ef4444;
            color: white;
        }

        .btn-danger:hover {
            background: #dc2626;
        }

        .empty-state {
            background: white;
            padding: 3rem;
            text-align: center;
            border-radius: 12px;
            border: 2px dashed #cbd5e1;
            color: #64748b;
        }

        .alert {
            padding: 1rem 1.5rem;
            border-radius: 8px;
            margin-bottom: 2rem;
            font-weight: 600;
            display: flex;
            align-items: center;
            animation: slideIn 0.3s ease-out;
        }

        @keyframes slideIn {
            from { transform: translateY(-10px); opacity: 0; }
            to { transform: translateY(0); opacity: 1; }
        }

        .alert-success {
            background-color: #d1fae5;
            color: #065f46;
            border-left: 4px solid #10b981;
        }

        .alert-error {
            background-color: #fee2e2;
            color: #991b1b;
            border-left: 4px solid #ef4444;
        }
        
        /* Admin Form Styles */
        .admin-form {
            background: white;
            padding: 2rem;
            border-radius: 12px;
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
            border: 1px solid #e2e8f0;
        }
        .form-group {
            margin-bottom: 1rem;
        }
        .form-group label {
            display: block;
            font-weight: 600;
            margin-bottom: 0.5rem;
            color: #334155;
        }
        .form-control {
            width: 100%;
            padding: 0.75rem;
            border: 1px solid #cbd5e1;
            border-radius: 6px;
            font-family: inherit;
        }
    </style>

    <div class="taller-container">
        <div class="taller-header">
            <h1>Portal de Talleres Extracurriculares</h1>
            <p>Bienvenido al sistema de actividades extracurriculares de la UCN.</p>
        </div>

        @if(errorMsg != null && !errorMsg.isEmpty())
            <div class="alert alert-error">
                [Error] ${errorMsg}
            </div>
        @endif

        @if(successMsg != null && !successMsg.isEmpty())
            <div class="alert alert-success">
                [Exito] ${successMsg}
            </div>
        @endif

        @if(rol != null && "ESTUDIANTE".equalsIgnoreCase(rol.trim()))
            <!-- SECCIÓN ESTUDIANTE: MIS TALLERES -->
            <h2 class="section-title">Mis Inscripciones</h2>
            
            @if(misInscripciones == null || misInscripciones.isEmpty())
                <div class="empty-state">
                    <h3>Aún no estás inscrito en ningún taller</h3>
                    <p>Explora el catálogo abajo y únete a tu primera actividad.</p>
                </div>
            @else
                <div class="grid-cards">
                    @for(Inscripcion i : misInscripciones)
                        @if(!"CANCELADO".equals(i.getEstado()))
                            <div class="card @if("INSCRITO".equals(i.getEstado())) card-inscrito @else card-espera @endif">
                                <div class="card-title">${i.getTaller().getNombre()}</div>
                                
                                @if("INSCRITO".equals(i.getEstado()))
                                    <span class="badge badge-inscrito">Cupo Confirmado</span>
                                @else
                                    <span class="badge badge-espera">En Lista de Espera</span>
                                @endif
                                
                                <div class="card-info"><strong>Profesor:</strong> ${i.getTaller().getProfesor().getNombre()}</div>
                                <div class="card-info"><strong>Inicio:</strong> ${i.getTaller().getFechaInicio().toString()}</div>
                                <div class="card-info"><strong>Lugar:</strong> ${i.getTaller().getEspacio() != null ? i.getTaller().getEspacio().getNombre() : "Por definir"}</div>
                                <div class="card-info"><strong>Bloque Horario:</strong> Bloque ${i.getTaller().getBloqueHorario()}</div>
                                
                                <form action="/talleres/${i.getTaller().getId()}/cancelar" method="POST">
                                    <button type="submit" class="btn btn-danger">Cancelar Inscripción</button>
                                </form>
                            </div>
                        @endif
                    @endfor
                </div>
            @endif

            <!-- SECCIÓN ESTUDIANTE: CATÁLOGO -->
            <h2 class="section-title">Catálogo de Talleres</h2>
            @if(talleres == null || talleres.isEmpty())
                <div class="empty-state">No hay talleres disponibles en este momento.</div>
            @else
                <div class="grid-cards">
                    @for(Taller t : talleres)
                        <div class="card">
                            <div class="card-title">${t.getNombre()}</div>
                            <span class="badge badge-abierto">Disponible</span>
                            
                            <div class="card-info"><strong>Cupos Totales:</strong> ${t.getCuposTotales()}</div>
                            <div class="card-info"><strong>Categoría:</strong> ${t.getCategoria()}</div>
                            <div class="card-info"><strong>Profesor:</strong> ${t.getProfesor().getNombre()}</div>
                            <div class="card-info"><strong>Fechas:</strong> ${t.getFechaInicio().toString()} a ${t.getFechaFin().toString()}</div>
                            <div class="card-info"><strong>Lugar:</strong> ${t.getEspacio() != null ? t.getEspacio().getNombre() : "Por definir"}</div>
                            <div class="card-info"><strong>Bloque Horario:</strong> Bloque ${t.getBloqueHorario()}</div>
                            <div class="card-info" style="margin-top: 10px; font-style: italic;">
                                ${t.getDescripcion() != null ? t.getDescripcion() : "Sin descripción"}
                            </div>
                            
                            <form action="/talleres/${t.getId()}/inscribir" method="POST">
                                <button type="submit" class="btn btn-primary">Inscribirme</button>
                            </form>
                        </div>
                    @endfor
                </div>
            @endif

        @elseif(rol != null && "ADMIN".equalsIgnoreCase(rol.trim()))
            <!-- SECCIÓN ADMIN -->
            <h2 class="section-title">Panel de Control: Talleres</h2>
            
            <div style="display: grid; grid-template-columns: 1fr 2fr; gap: 2rem;">
                <!-- Formulario -->
                <div class="admin-form">
                    <h3 style="margin-top: 0; color: #1e293b; margin-bottom: 1.5rem;">Crear Taller</h3>
                    <form action="/talleres" method="POST">
                        <div class="form-group">
                            <label>Nombre del Taller</label>
                            <input type="text" name="nombre" class="form-control" required>
                        </div>
                        <div class="form-group">
                            <label>Descripción</label>
                            <input type="text" name="descripcion" class="form-control">
                        </div>
                        <div class="form-group">
                            <label>Categoría</label>
                            <input type="text" name="categoria" class="form-control" required>
                        </div>
                        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                            <div class="form-group">
                                <label>Cupos Totales</label>
                                <input type="number" name="cupos" class="form-control" required min="1">
                            </div>
                            <div class="form-group">
                                <label>Bloque (A-F)</label>
                                <select name="bloque" class="form-control" required>
                                    <option value="A">Bloque A</option>
                                    <option value="B">Bloque B</option>
                                    <option value="C">Bloque C</option>
                                    <option value="D">Bloque D</option>
                                    <option value="E">Bloque E</option>
                                    <option value="F">Bloque F</option>
                                </select>
                            </div>
                        </div>
                        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                            <div class="form-group">
                                <label>Fecha de Inicio</label>
                                <input type="date" name="fechaInicio" class="form-control" required min="${java.time.LocalDate.now().toString()}">
                            </div>
                            <div class="form-group">
                                <label>Fecha de Fin</label>
                                <input type="date" name="fechaFin" class="form-control" required min="${java.time.LocalDate.now().toString()}">
                            </div>
                        </div>
                        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                            <div class="form-group">
                                <label>Profesor Asignado</label>
                                <select name="profesorId" class="form-control" required>
                                    <option value="" disabled selected>Seleccione docente</option>
                                    @if(docentes != null)
                                        @for(Usuario docente : docentes)
                                            <option value="${docente.getId()}">${docente.getNombre()}</option>
                                        @endfor
                                    @endif
                                </select>
                            </div>
                            <div class="form-group">
                                <label>Lugar (Opcional)</label>
                                <select name="espacioId" class="form-control">
                                    <option value="">Por definir</option>
                                    @if(espacios != null)
                                        @for(Espacio espacio : espacios)
                                            <option value="${espacio.getId()}">${espacio.getNombre()} (${espacio.getTipo()})</option>
                                        @endfor
                                    @endif
                                </select>
                            </div>
                        </div>
                        <button type="submit" class="btn btn-primary" style="margin-top: 1rem;">Registrar Taller</button>
                    </form>
                </div>
                
                <!-- Lista de Talleres -->
                <div>
                    <h3 style="margin-top: 0; color: #1e293b; margin-bottom: 1.5rem;">Talleres Existentes</h3>
                    <div style="background: white; border-radius: 12px; border: 1px solid #e2e8f0; overflow: hidden; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);">
                        @if(talleres == null || talleres.isEmpty())
                            <div class="empty-state" style="padding: 2rem;">No hay talleres registrados en el sistema.</div>
                        @else
                            <table style="width: 100%; border-collapse: collapse; text-align: left;">
                                <thead>
                                    <tr style="background-color: #f1f5f9; color: #475569;">
                                        <th style="padding: 1rem; border-bottom: 1px solid #e2e8f0;">Nombre</th>
                                        <th style="padding: 1rem; border-bottom: 1px solid #e2e8f0;">Docente</th>
                                        <th style="padding: 1rem; border-bottom: 1px solid #e2e8f0;">Categoría</th>
                                        <th style="padding: 1rem; border-bottom: 1px solid #e2e8f0;">Cupos</th>
                                        <th style="padding: 1rem; border-bottom: 1px solid #e2e8f0;">Lugar</th>
                                        <th style="padding: 1rem; border-bottom: 1px solid #e2e8f0;">Bloque</th>
                                        <th style="padding: 1rem; border-bottom: 1px solid #e2e8f0;">Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    @for(Taller t : talleres)
                                        <tr style="border-bottom: 1px solid #e2e8f0;">
                                            <td style="padding: 1rem; color: #0f172a;"><strong>${t.getNombre()}</strong></td>
                                            <td style="padding: 1rem; color: #475569;">${t.getProfesor().getNombre()}</td>
                                            <td style="padding: 1rem; color: #475569;">${t.getCategoria()}</td>
                                            <td style="padding: 1rem; color: #475569;">${t.getCuposTotales()}</td>
                                            <td style="padding: 1rem; color: #475569;">${t.getEspacio() != null ? t.getEspacio().getNombre() : "Por definir"}</td>
                                            <td style="padding: 1rem; color: #475569;">${t.getBloqueHorario()}</td>
                                            <td style="padding: 1rem; display: flex; gap: 0.5rem; flex-wrap: wrap;">
                                                <a href="/talleres/${t.getId()}/editar" class="btn btn-primary" style="padding: 0.5rem; margin-top: 0; font-size: 0.85rem; text-decoration: none;">Editar</a>
                                                <form action="/talleres/${t.getId()}/eliminar" method="POST" onsubmit="return confirm('¿Está seguro que desea eliminar este taller? Esta acción es irreversible.');" style="margin: 0;">
                                                    <button type="submit" class="btn btn-danger" style="padding: 0.5rem; margin-top: 0; font-size: 0.85rem;">Eliminar</button>
                                                </form>
                                            </td>
                                        </tr>
                                    @endfor
                                </tbody>
                            </table>
                        @endif
                    </div>
                </div>
            </div>
            
        @else
            <!-- SECCIÓN DOCENTE -->
            <h2 class="section-title">Mis Talleres Asignados</h2>
            <div class="grid-cards">
                @if(talleres != null && !talleres.isEmpty())
                    @for(Taller t : talleres)
                        <div class="card">
                            <div class="card-title">${t.getNombre()}</div>
                            <div class="card-info"><strong>Cupos:</strong> ${t.getCuposTotales()}</div>
                            <div class="card-info"><strong>Lugar:</strong> ${t.getEspacio() != null ? t.getEspacio().getNombre() : "Por definir"}</div>
                            <div class="card-info"><strong>Bloque:</strong> ${t.getBloqueHorario()}</div>
                            
                            <a href="/talleres/${t.getId()}/alumnos" class="btn btn-primary" style="text-decoration: none;">Ver Lista de Alumnos</a>
                        </div>
                    @endfor
                @else
                    <div class="empty-state">No tienes talleres asignados actualmente.</div>
                @endif
            </div>
        @endif
    </div>
    `
)