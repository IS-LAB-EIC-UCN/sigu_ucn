@import cl.ucn.app.dto.talleres.ListadoInscritos
@import cl.ucn.app.dto.talleres.TallerResumen
@import cl.ucn.app.model.talleres.Inscripcion
@import cl.ucn.app.util.Formato

@param String title
@param String usuarioNombre
@param boolean esAdmin
@param String flashOk
@param String flashError
@param TallerResumen taller
@param ListadoInscritos listado = null

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <section class="tl-page">
        <a class="tl-link" href="/talleres">← Volver a talleres</a>
        <div class="tl-header">
            <h1>${taller.nombre()}</h1>
            <span class="tl-tag">${taller.categoria()}</span>
        </div>

        @template.talleres.flash(flashOk = flashOk, flashError = flashError)

        <div class="tl-card">
            <dl class="tl-datos">
                <dt>Fecha</dt><dd>${Formato.fecha(taller.fecha())}</dd>
                <dt>Horario</dt><dd>${Formato.hora(taller.horaInicio())} – ${Formato.hora(taller.horaFin())}</dd>
                <dt>Lugar</dt><dd>${Formato.texto(taller.lugar())}</dd>
                <dt>Cupo máximo</dt><dd>${Formato.texto(taller.cupoMaximo())}</dd>
                <dt>Inscritos</dt><dd>${Formato.texto(taller.inscritos())}</dd>
                <dt>En lista de espera</dt><dd>${Formato.texto(taller.enEspera())}</dd>
            </dl>

            <form method="post" action="/talleres/${Formato.texto(taller.id())}/inscribir">
                @if(taller.estaLleno())
                    <p class="tl-aviso">El taller está lleno. Si te inscribes, quedarás en la lista de espera y entrarás automáticamente cuando se libere un cupo.</p>
                    <button class="tl-btn tl-btn-secondary" type="submit">Unirme a la lista de espera</button>
                @else
                    <button class="tl-btn" type="submit">Inscribirme</button>
                @endif
            </form>
        </div>

        @if(esAdmin && listado != null)
            <h2>Participantes inscritos (${Formato.texto(listado.inscritos().size())})</h2>
            @if(listado.inscritos().isEmpty())
                <p class="tl-vacio">No hay participantes inscritos todavía.</p>
            @else
                <table class="tl-tabla">
                    <thead>
                    <tr>
                        <th>#</th>
                        <th>Nombre</th>
                        <th>Correo</th>
                        <th>Fecha de inscripción</th>
                        <th>Acción</th>
                    </tr>
                    </thead>
                    <tbody>
                    @for(Inscripcion ins : listado.inscritos())
                        <tr>
                            <td>${Formato.texto(listado.inscritos().indexOf(ins) + 1)}</td>
                            <td>${ins.getUsuario().getNombre()}</td>
                            <td>${ins.getUsuario().getCorreo()}</td>
                            <td>${Formato.fechaHora(ins.getFechaInscripcion())}</td>
                            <td>
                                <form method="post" action="/inscripciones/${Formato.texto(ins.getId())}/anular">
                                    <input type="hidden" name="volver" value="/talleres/${Formato.texto(taller.id())}">
                                    <button class="tl-btn-mini" type="submit">Anular</button>
                                </form>
                            </td>
                        </tr>
                    @endfor
                    </tbody>
                </table>
            @endif

            <h2>Lista de espera (${Formato.texto(listado.listaEspera().size())})</h2>
            @if(listado.listaEspera().isEmpty())
                <p class="tl-vacio">No hay participantes en lista de espera.</p>
            @else
                <table class="tl-tabla">
                    <thead>
                    <tr>
                        <th>Posición</th>
                        <th>Nombre</th>
                        <th>Correo</th>
                        <th>Fecha de solicitud</th>
                        <th>Acción</th>
                    </tr>
                    </thead>
                    <tbody>
                    @for(Inscripcion esp : listado.listaEspera())
                        <tr>
                            <td>${Formato.texto(listado.listaEspera().indexOf(esp) + 1)}</td>
                            <td>${esp.getUsuario().getNombre()}</td>
                            <td>${esp.getUsuario().getCorreo()}</td>
                            <td>${Formato.fechaHora(esp.getFechaInscripcion())}</td>
                            <td>
                                <form method="post" action="/inscripciones/${Formato.texto(esp.getId())}/anular">
                                    <input type="hidden" name="volver" value="/talleres/${Formato.texto(taller.id())}">
                                    <button class="tl-btn-mini" type="submit">Anular</button>
                                </form>
                            </td>
                        </tr>
                    @endfor
                    </tbody>
                </table>
            @endif
        @endif
    </section>
`
)
