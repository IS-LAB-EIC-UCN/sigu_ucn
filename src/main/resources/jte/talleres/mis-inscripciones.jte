@import cl.ucn.app.model.talleres.EstadoInscripcion
@import cl.ucn.app.model.talleres.Inscripcion
@import cl.ucn.app.util.Formato
@import java.util.List

@param String title
@param String usuarioNombre
@param boolean esAdmin
@param String flashOk
@param String flashError
@param List<Inscripcion> inscripciones

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <section class="tl-page">
        <a class="tl-link" href="/talleres">← Volver a talleres</a>
        <div class="tl-header">
            <h1>Mis inscripciones</h1>
        </div>

        @template.talleres.flash(flashOk = flashOk, flashError = flashError)

        @if(inscripciones.isEmpty())
            <p class="tl-vacio">Aún no tienes inscripciones registradas.</p>
        @else
            <table class="tl-tabla">
                <thead>
                <tr>
                    <th>Taller</th>
                    <th>Fecha</th>
                    <th>Horario</th>
                    <th>Lugar</th>
                    <th>Estado</th>
                    <th>Acción</th>
                </tr>
                </thead>
                <tbody>
                @for(Inscripcion ins : inscripciones)
                    <tr>
                        <td>
                            <a class="tl-link" href="/talleres/${Formato.texto(ins.getTaller().getId())}">
                                ${ins.getTaller().getNombre()}
                            </a>
                        </td>
                        <td>${Formato.fecha(ins.getTaller().getFecha())}</td>
                        <td>${Formato.hora(ins.getTaller().getHoraInicio())} – ${Formato.hora(ins.getTaller().getHoraFin())}</td>
                        <td>${Formato.texto(ins.getTaller().getLugar())}</td>
                        <td>
                            @if(ins.getEstado() == EstadoInscripcion.INSCRITO)
                                <span class="tl-badge tl-badge-ok">Inscrito</span>
                            @elseif(ins.getEstado() == EstadoInscripcion.EN_ESPERA)
                                <span class="tl-badge tl-badge-espera">En lista de espera</span>
                            @else
                                <span class="tl-badge tl-badge-anulada">Anulada</span>
                            @endif
                        </td>
                        <td>
                            @if(ins.getEstado().esVigente())
                                <form method="post" action="/inscripciones/${Formato.texto(ins.getId())}/anular">
                                    <input type="hidden" name="volver" value="/mis-inscripciones">
                                    <button class="tl-btn-mini" type="submit">Anular</button>
                                </form>
                            @else
                                <span class="tl-vacio">—</span>
                            @endif
                        </td>
                    </tr>
                @endfor
                </tbody>
            </table>
        @endif
    </section>
`
)
