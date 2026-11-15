@import cl.ucn.app.dto.TutoriaDetalle
@import cl.ucn.app.dto.TutoriaFiltro
@import java.util.List

@param String title
@param String usuarioNombre
@param List<TutoriaDetalle> tutorias
@param TutoriaFiltro filtro
@param boolean esTutor = false
@param boolean esAdmin = false
@param String mensaje = null
@param String error = null

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <link rel="stylesheet" href="/css/tutorias.css">
    <section class="tutorias">
        <div class="tut-encabezado">
            <h1>Tutorías disponibles</h1>
            <div class="tut-acciones">
                @if(esTutor)
                    <a class="tut-btn" href="/tutorias/publicar">Publicar tutoría</a>
                @endif
                @if(esAdmin)
                    <a class="tut-btn tut-btn-secundario" href="/tutorias/admin/asignaturas">Administración</a>
                @endif
            </div>
        </div>

        @if(mensaje != null)
            <div class="tut-alerta-exito">${mensaje}</div>
        @endif
        @if(error != null)
            <div class="alert-error">${error}</div>
        @endif

        <div class="info-card">
            @if(tutorias.isEmpty())
                <p class="tut-vacio">No hay tutorías disponibles por el momento.</p>
            @else
                <table class="tut-tabla">
                    <thead>
                    <tr>
                        <th>Asignatura</th>
                        <th>Tutor</th>
                        <th>Fecha</th>
                        <th>Horario</th>
                        <th>Modalidad</th>
                        <th>Cupos</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
                    @for(TutoriaDetalle t : tutorias)
                        <tr>
                            <td>${t.asignaturaTexto()}</td>
                            <td>${t.tutorNombre()}</td>
                            <td>${t.fechaTexto()}</td>
                            <td>${t.horarioTexto()}</td>
                            <td>${t.modalidadTexto()}</td>
                            <td>
                                @if(t.sinCupos())
                                    <span class="tut-etiqueta tut-etiqueta-sin-cupos">Sin cupos</span>
                                @else
                                    <span class="tut-etiqueta">${t.cuposDisponibles()} de ${t.capacidad()}</span>
                                @endif
                            </td>
                            <td><a class="tut-btn tut-btn-secundario" href="/tutorias/${t.id()}">Ver</a></td>
                        </tr>
                    @endfor
                    </tbody>
                </table>
            @endif
        </div>
    </section>
`
)
