
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
                            <th>Estado</th>
                        </tr>
                    </thead>

                    <tbody>
                        @if(inscripciones.isEmpty())
                            <tr><td colspan="4">Todavía no hay inscripciones para este evento.</td></tr>
                        @endif
                        @for(var inscripcion : inscripciones)
                            <tr>
                                <td>${inscripcion.getUsuario().getNombre()}</td>
                                <td>${inscripcion.getUsuario().getCorreo()}</td>
                                <td>${inscripcion.getFechaInscripcion().format(
                                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                                    )}</td>
                                <td>${inscripcion.getEstado()}</td>
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
