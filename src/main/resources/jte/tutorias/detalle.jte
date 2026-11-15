@import cl.ucn.app.dto.TutoriaDetalle

@param String title
@param String usuarioNombre
@param TutoriaDetalle tutoria
@param boolean yaReservada = false
@param String mensaje = null
@param String error = null

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <link rel="stylesheet" href="/css/tutorias.css">
    <section class="tutorias">
        <div class="tut-encabezado">
            <h1>${tutoria.asignaturaTexto()}</h1>
            <a class="tut-btn tut-btn-secundario" href="/tutorias">Volver al listado</a>
        </div>

        @if(mensaje != null)
            <div class="tut-alerta-exito">${mensaje}</div>
        @endif
        @if(error != null)
            <div class="alert-error">${error}</div>
        @endif

        <div class="info-card tut-formulario">
            <dl class="tut-detalle">
                <dt>Tutor</dt>
                <dd>${tutoria.tutorNombre()}</dd>
                <dt>Fecha</dt>
                <dd>${tutoria.fechaTexto()}</dd>
                <dt>Horario</dt>
                <dd>${tutoria.horarioTexto()}</dd>
                <dt>Modalidad</dt>
                <dd>${tutoria.modalidadTexto()}</dd>
                <dt>Cupos disponibles</dt>
                <dd>${tutoria.cuposDisponibles()} de ${tutoria.capacidad()}</dd>
                <dt>Estado</dt>
                <dd>${tutoria.estadoTexto()}</dd>
            </dl>

            @if(yaReservada)
                <div class="tut-alerta-exito">Ya tienes una reserva en esta tutoría.</div>
            @elseif(tutoria.sinCupos())
                <p class="tut-vacio">Esta tutoría no tiene cupos disponibles.</p>
            @else
                <form method="post" action="/tutorias/${tutoria.id()}/reservar">
                    <button type="submit" class="tut-btn">Reservar tutoría</button>
                </form>
            @endif
        </div>
    </section>
`
)
