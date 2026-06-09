@import java.util.List
@import cl.ucn.app.model.Taller
@import cl.ucn.app.model.Inscripcion

@param String title
@param String usuarioNombre
@param String rol
@param List<Taller> talleres
@param List<Inscripcion> misInscripciones
@param List<cl.ucn.app.model.Usuario> docentes

@template.layout.main(
    title = title,
    usuarioNombre = usuarioNombre,
    content = @`
        <section style="padding: 20px; font-family: sans-serif;">
            <h1>Módulo de Talleres (Grupo 3)</h1>
            <p>Usuario: <strong>${usuarioNombre}</strong></p>
            <p>Rol detectado: <strong style="color: blue;">${rol}</strong></p>

            <hr style="margin: 20px 0; border: 0; border-top: 1px solid #ccc;">

            @if(rol != null && "ADMIN".equalsIgnoreCase(rol.trim()))
                <div style="background-color: #ffebe9; padding: 20px; border-left: 5px solid red; margin-bottom: 20px; border-radius: 4px;">
                    <h2>[VISTA ADMIN] Registrar Nuevo Taller</h2>

                    <form action="/talleres" method="POST" style="display: flex; flex-direction: column; gap: 10px; max-width: 400px;">
                        <input type="text" name="nombre" placeholder="Nombre del Taller (ej: Electrotecnia Avanzada)" required style="padding: 8px; border: 1px solid #ccc; border-radius: 4px;">
                        <textarea name="descripcion" placeholder="Descripción del taller" required style="padding: 8px; border: 1px solid #ccc; border-radius: 4px;"></textarea>
                        <input type="number" name="cupos" placeholder="Cantidad de Cupos (ej: 25)" required min="1" style="padding: 8px; border: 1px solid #ccc; border-radius: 4px;">
                        
                        <label style="font-weight: bold;">Fecha Inicio:</label>
                        <input type="date" name="fechaInicio" required min="${java.time.LocalDate.now().toString()}" style="padding: 8px; border: 1px solid #ccc; border-radius: 4px;">
                        
                        <label style="font-weight: bold;">Fecha Fin:</label>
                        <input type="date" name="fechaFin" required min="${java.time.LocalDate.now().toString()}" style="padding: 8px; border: 1px solid #ccc; border-radius: 4px;">
                        
                        <label style="font-weight: bold;">Bloque:</label>
                        <select name="bloque" required style="padding: 8px; border: 1px solid #ccc; border-radius: 4px;">
                            <option value="A">Bloque A</option>
                            <option value="B">Bloque B</option>
                            <option value="C">Bloque C</option>
                            <option value="D">Bloque D</option>
                            <option value="E">Bloque E</option>
                            <option value="F">Bloque F</option>
                        </select>

                        <label style="font-weight: bold;">Docente:</label>
                        <select name="profesorId" required style="padding: 8px; border: 1px solid #ccc; border-radius: 4px;">
                            <option value="" disabled selected>Seleccione un docente</option>
                            @for(cl.ucn.app.model.Usuario docente : docentes)
                                <option value="${docente.getId()}">${docente.getNombre()}</option>
                            @endfor
                        </select>

                        <button type="submit" style="background-color: red; color: white; padding: 10px; border: none; border-radius: 4px; cursor: pointer; font-weight: bold;">
                            Guardar Taller
                        </button>
                    </form>
                </div>
            @elseif(rol != null && "ESTUDIANTE".equalsIgnoreCase(rol.trim()))
                <div style="background-color: #e6f6ff; padding: 15px; border-left: 5px solid blue; border-radius: 4px;">
                    <h2>[VISTA ESTUDIANTE]</h2>
                    <p>Aquí va el catálogo de talleres disponibles para inscribirse.</p>
                </div>
            @elseif(rol != null && "DOCENTE".equalsIgnoreCase(rol.trim()))
                <div style="background-color: #f0fdf4; padding: 15px; border-left: 5px solid green; border-radius: 4px;">
                    <h2>[VISTA DOCENTE] Mis Talleres</h2>
                    @if(talleres == null || talleres.isEmpty())
                        <p>No tienes talleres asignados.</p>
                    @else
                        <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 15px; margin-top: 15px;">
                            @for(Taller taller : talleres)
                                <div style="border: 1px solid #ccc; border-radius: 4px; padding: 15px; background: white;">
                                    <h3 style="margin-top: 0;">${taller.getNombre()}</h3>
                                    <p>${taller.getDescripcion()}</p>
                                    <p><strong>Cupos:</strong> ${taller.getCuposTotales()}</p>
                                    <p><strong>Estado:</strong> ${taller.getEstado()}</p>
                                    <a href="/talleres/${taller.getId()}/alumnos" style="display: inline-block; padding: 8px 12px; background-color: #10b981; color: white; text-decoration: none; border-radius: 4px; font-weight: bold; margin-top: 10px;">
                                        Ver Alumnos
                                    </a>
                                </div>
                            @endfor
                        </div>
                    @endif
                </div>
            @else
                <div style="background-color: #f3f4f6; padding: 15px; border-left: 5px solid gray; border-radius: 4px;">
                    <h2>[Vista General]</h2>
                </div>
            @endif
        </section>
    `
)