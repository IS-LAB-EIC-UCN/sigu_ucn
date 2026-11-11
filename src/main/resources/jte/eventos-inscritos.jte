
@param String title
@param String usuarioNombre
@param cl.ucn.app.model.Evento evento
@param java.util.List<cl.ucn.app.model.Inscripcion> inscripciones

@template.layout.main(
    title = title,
    usuarioNombre = usuarioNombre,
    content = @`
        <section class="dashboard eventos-panel">

            <h1>Participantes inscritos</h1>

            <h2>${evento.getTitulo()}</h2>

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
