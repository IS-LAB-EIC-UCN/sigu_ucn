@param String title
@param String usuarioNombre
@param java.util.List<cl.ucn.app.model.Tutoria> tutorias
@param java.util.List<cl.ucn.app.model.TutoriaReserva> reservas
@param java.util.List<cl.ucn.app.model.Asignatura> asignaturas
@param java.util.List<cl.ucn.app.model.Usuario> tutores
@param String usuarioRol
@param String filtroTutorId
@param String filtroAsignaturaId
@param String filtroFecha

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <section class="dashboard">
        <h1>Listado de Tutorías</h1>

        @if("ADMIN".equals(usuarioRol))
            <a href="/tutorias/crear">Crear Asignatura</a>
        @endif

        @if("TUTOR".equals(usuarioRol))
            <a href="/tutorias/crear">Crear Tutoría</a>
        @endif

        <hr>

        <h2>Filtros</h2>

        <form action="/tutorias" method="get">

            @if(!"TUTOR".equals(usuarioRol))
                <label>Tutor:</label>
                <select name="tutorId">
                    <option value="">Todos</option>

                    @for(cl.ucn.app.model.Usuario tutor : tutores)
                        <option value="${tutor.getId()}">
                            ${tutor.getNombre()}
                        </option>
                    @endfor
                </select>
            @endif

            <label>Asignatura:</label>
            <select name="asignaturaId">
                <option value="">Todas</option>

                @for(cl.ucn.app.model.Asignatura a : asignaturas)
                    <option value="${a.getId()}">
                        ${a.getCodigo()} - ${a.getNombre()}
                    </option>
                @endfor
            </select>

            <label>Fecha:</label>
            <input type="date" name="fecha" value="${filtroFecha}">

            <button type="submit">Filtrar</button>
            <a href="/tutorias">Limpiar filtros</a>
        </form>

        <hr>

        <h2>Tutorías activas</h2>

        @if(tutorias.isEmpty())
            <p>No hay tutorías activas para mostrar.</p>
        @else
            <table border="1">
                <tr>
                    <th>ID</th>
                    <th>Asignatura</th>
                    <th>Tutor</th>
                    <th>Fecha</th>
                    <th>Hora Inicio</th>
                    <th>Hora Fin</th>
                    <th>Estado</th>
                    <th>Gestión</th>
                </tr>

                @for(cl.ucn.app.model.Tutoria t : tutorias)
                    @if(!"CANCELADA".equals(t.getEstado()))
                        <tr>
                            <td>${t.getId()}</td>
                            <td>${t.getAsignatura().getNombre()}</td>
                            <td>${t.getUsuario().getNombre()}</td>
                            <td>${t.getFecha().toString()}</td>
                            <td>${t.getHoraInicio().toString()}</td>
                            <td>${t.getHoraFin().toString()}</td>
                            <td>${t.getEstado()}</td>
                            <td>
                                @if("ESTUDIANTE".equals(usuarioRol) && "DISPONIBLE".equals(t.getEstado()))
                                    <form action="/tutorias/reservar" method="post">
                                        <input type="hidden" name="tutoriaId" value="${t.getId()}">
                                        <button type="submit">Reservar</button>
                                    </form>
                                @endif

                                @if("ESTUDIANTE".equals(usuarioRol) && "RESERVADA".equals(t.getEstado()))
                                    Ya fue reservada
                                @endif

                                @if("ESTUDIANTE".equals(usuarioRol) && "REALIZADA".equals(t.getEstado()))
                                    Tutoría finalizada
                                @endif

                                @if(("ADMIN".equals(usuarioRol) || "TUTOR".equals(usuarioRol)) && "DISPONIBLE".equals(t.getEstado()))
                                    <form action="/tutorias/cancelar" method="post">
                                        <input type="hidden" name="tutoriaId" value="${t.getId()}">
                                        <button type="submit">Cancelar</button>
                                    </form>
                                @endif

                                @if(("ADMIN".equals(usuarioRol) || "TUTOR".equals(usuarioRol)) && "RESERVADA".equals(t.getEstado()))
                                    Tutoría reservada
                                @endif

                                @if(("ADMIN".equals(usuarioRol) || "TUTOR".equals(usuarioRol)) && "REALIZADA".equals(t.getEstado()))
                                    Tutoría finalizada
                                @endif
                            </td>
                        </tr>
                    @endif
                @endfor
            </table>
        @endif

        <hr>

        <h2>Historial de tutorías canceladas</h2>

        @if(!"ADMIN".equals(usuarioRol) && !"TUTOR".equals(usuarioRol))
            <p>No tienes acceso al historial de tutorías canceladas.</p>
        @else
            <table border="1">
                <tr>
                    <th>ID</th>
                    <th>Asignatura</th>
                    <th>Tutor</th>
                    <th>Fecha</th>
                    <th>Hora Inicio</th>
                    <th>Hora Fin</th>
                    <th>Estado</th>
                </tr>

                @for(cl.ucn.app.model.Tutoria t : tutorias)
                    @if("CANCELADA".equals(t.getEstado()))
                        <tr>
                            <td>${t.getId()}</td>
                            <td>${t.getAsignatura().getNombre()}</td>
                            <td>${t.getUsuario().getNombre()}</td>
                            <td>${t.getFecha().toString()}</td>
                            <td>${t.getHoraInicio().toString()}</td>
                            <td>${t.getHoraFin().toString()}</td>
                            <td>${t.getEstado()}</td>
                        </tr>
                    @endif
                @endfor
            </table>
        @endif

        <hr>

        <h2>Reservas</h2>

        @if(reservas.isEmpty())
            <p>No hay reservas registradas.</p>
        @else
            <table border="1">
                <tr>
                    <th>ID Reserva</th>
                    <th>Asignatura</th>
                    <th>Fecha</th>
                    <th>Asistencia</th>
                    <th>Registro</th>
                </tr>

                @for(cl.ucn.app.model.TutoriaReserva r : reservas)
                    <tr>
                        <td>${r.getId()}</td>
                        <td>${r.getTutoria().getAsignatura().getNombre()}</td>
                        <td>${r.getTutoria().getFecha().toString()}</td>
                        <td>
                            @if(r.getAsistio() == null)
                                Pendiente
                            @endif

                            @if(Boolean.TRUE.equals(r.getAsistio()))
                                Asistió
                            @endif

                            @if(Boolean.FALSE.equals(r.getAsistio()))
                                Faltó
                            @endif
                        </td>
                        <td>
                            @if("TUTOR".equals(usuarioRol) && r.getAsistio() == null)
                                <form action="/tutorias/asistencia" method="post">
                                    <input type="hidden" name="reservaId" value="${r.getId()}">

                                    <select name="asistio">
                                        <option value="true">Asistió</option>
                                        <option value="false">Faltó</option>
                                    </select>

                                    <button type="submit">Guardar asistencia</button>
                                </form>
                            @endif

                            @if("TUTOR".equals(usuarioRol) && r.getAsistio() != null)
                                Asistencia registrada
                            @endif

                            @if(!"TUTOR".equals(usuarioRol) && r.getAsistio() == null)
                                En espera de registro
                            @endif

                            @if(!"TUTOR".equals(usuarioRol) && r.getAsistio() != null)
                                Registro finalizado
                            @endif
                        </td>
                    </tr>
                @endfor
            </table>
        @endif
    </section>
`
)