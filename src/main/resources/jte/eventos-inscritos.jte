
@param boolean puedeRegistrarAsistencia
@param String csrfToken
@param String title
@param String usuarioNombre
@param cl.ucn.app.model.Evento evento
@param java.util.List<cl.ucn.app.model.Inscripcion> inscripciones

@template.layout.main(
    title = title,
    usuarioNombre = usuarioNombre,
    content = @`
        <section class="dashboard eventos-panel">

            <p class="eyebrow">LISTA DE ASISTENTES</p><h1>Participantes del evento</h1>

            <h2>${evento.getTitulo()}</h2>

            <div class="eventos-acciones no-imprimir"><button class="eventos-btn" type="button" data-imprimir>Imprimir lista</button><a href="/eventos/${evento.getId()}/inscritos?activos=true" class="eventos-btn secundario">Solo inscripciones activas</a><a href="/eventos/${evento.getId()}/inscritos" class="eventos-btn secundario">Ver historial completo</a></div>
            <p>
                Total de registros:
                ${inscripciones.size()}
            </p>

            <div class="eventos-tabla-contenedor">
                <table class="eventos-tabla">
                    <thead>
                        <tr>
                            <th>Nombre</th>
                            <th>Correo</th>
                            <th>Fecha de inscripción</th>
                            <th>Estado</th><th>Asistencia</th>
                        </tr>
                    </thead>

                    <tbody>
                        @if(inscripciones.isEmpty())
                            <tr><td colspan="5">Todavía no hay inscripciones para este evento.</td></tr>
                        @endif
                        @for(var inscripcion : inscripciones)
                            <tr>
                                <td>${inscripcion.getUsuario().getNombre()}</td>
                                <td>${inscripcion.getUsuario().getCorreo()}</td>
                                <td>${inscripcion.getFechaInscripcion().format(
                                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                                    )}</td>
                                <td>${inscripcion.getEstado()}</td><td>
<span>${Boolean.TRUE.equals(inscripcion.getAsistencia()) ? "Asistió" : "Sin asistencia registrada"}</span>
@if(puedeRegistrarAsistencia && "INSCRITO".equals(inscripcion.getEstado()) && ("PUBLICADO".equals(evento.getEstado()) || "FINALIZADO".equals(evento.getEstado())))
<form class="no-imprimir" method="post" action="/eventos/${evento.getId()}/inscripciones/${inscripcion.getId()}/asistencia"><input type="hidden" name="csrfToken" value="${csrfToken}"><input type="hidden" name="asistencia" value="${!Boolean.TRUE.equals(inscripcion.getAsistencia())}"><button type="submit" class="boton-texto">${Boolean.TRUE.equals(inscripcion.getAsistencia()) ? "Quitar marca" : "Marcar asistencia"}</button></form>
@endif</td>
                            </tr>
                        @endfor
                    </tbody>
                </table>
            </div>

            <p>
                <a href="/eventos" class="eventos-btn">
                    Volver a eventos
                </a>
            </p>

        </section>
    `)
