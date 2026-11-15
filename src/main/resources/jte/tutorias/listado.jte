@param String title
@param String usuarioNombre
@param String usuarioRol = null
@param java.util.List<cl.ucn.app.dto.TutoriaListado> tutorias
@param cl.ucn.app.dto.TutoriaFiltro filtro
@param java.util.List<cl.ucn.app.model.Asignatura> asignaturas
@param java.util.List<cl.ucn.app.model.Tutor> tutores

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
usuarioRol = usuarioRol,
content = @`
    <link rel="stylesheet" href="/css/tutorias.css">
    <section class="tutorias">
        <h1>Tutorías disponibles</h1>
        <form method="get" action="/tutorias" class="filter-form">
            <label for="asignaturaId">Asignatura</label>
            <select id="asignaturaId" name="asignaturaId">
                <option value="">Todas</option>
                @for(var asignatura : asignaturas)
                    @if(filtro.asignaturaId() != null && filtro.asignaturaId().equals(asignatura.getId()))
                        <option value="${asignatura.getId()}" selected>
                            ${asignatura.getCodigo()} - ${asignatura.getNombre()}
                        </option>
                    @else
                        <option value="${asignatura.getId()}">
                            ${asignatura.getCodigo()} - ${asignatura.getNombre()}
                        </option>
                    @endif
                @endfor
            </select>
            <label for="tutorId">Tutor</label>
            <select id="tutorId" name="tutorId">
                <option value="">Todos</option>
                @for(var tutor : tutores)
                    @if(filtro.tutorId() != null && filtro.tutorId().equals(tutor.getId()))
                        <option value="${tutor.getId()}" selected>${tutor.getEstudiante().getNombre()}</option>
                    @else
                        <option value="${tutor.getId()}">${tutor.getEstudiante().getNombre()}</option>
                    @endif
                @endfor
            </select>
            <label for="fecha">Fecha</label>
            <input id="fecha" type="date" name="fecha"
                   value="${filtro.fechaTexto()}">
            <button type="submit" class="tut-btn">Buscar</button>
        </form>

        @if(tutorias.isEmpty())
            <div class="info-card">No hay tutorías disponibles para los filtros seleccionados.</div>
        @else
            <div class="card-grid">
                @for(var item : tutorias)
                    <article class="info-card">
                        <h3>${item.tutoria().getAsignatura().getNombre()}</h3>
                        <p>Tutor: ${item.tutoria().getTutor().getEstudiante().getNombre()}</p>
                        <p>Inicio: ${item.tutoria().getInicio().toString()}</p>
                        <p>Fin: ${item.tutoria().getFin().toString()}</p>
                        <p>Modalidad: ${item.tutoria().getModalidad()}</p>
                        <p>Cupos disponibles: ${item.cuposDisponibles()}</p>
                    </article>
                @endfor
            </div>
        @endif
    </section>
`
)
