@import cl.ucn.app.dto.talleres.TallerResumen
@import cl.ucn.app.util.Formato

@param String title
@param String usuarioNombre
@param boolean esAdmin
@param String flashOk
@param String flashError
@param TallerResumen taller

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

        <%-- TODO [Tarea 4 - RF6]: si esAdmin, mostrar dos tablas: "Inscritos" y "Lista de espera"
             (posición, nombre, correo, fecha de inscripción). Recibir un @param ListadoInscritos listado.
             TODO [Tarea 3]: en cada fila, un botón "Anular" (form POST a /inscripciones/{id}/anular
             con un input hidden name="volver" value="/talleres/{idTaller}"). --%>
    </section>
`
)
