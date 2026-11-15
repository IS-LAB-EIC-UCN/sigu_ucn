@param String title
@param String usuarioNombre
@param String usuarioRol = null
@param boolean esTutor = false
@param cl.ucn.app.model.Tutoria tutoria
@param long cuposDisponibles
@param String mensaje = null
@param String error = null

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
usuarioRol = usuarioRol,
esTutor = esTutor,
content = @`
    <link rel="stylesheet" href="/css/tutorias.css">
    <section class="tutorias">
        <div class="tut-encabezado">
            <h1>${tutoria.getAsignatura().getCodigo()} - ${tutoria.getAsignatura().getNombre()}</h1>
            <a class="tut-btn tut-btn-secundario" href="/tutorias">Volver al listado</a>
        </div>

        @if(mensaje != null)
            <div class="tut-alerta-exito">${mensaje}</div>
        @endif
        @if(error != null)
            <div class="alert-error">${error}</div>
        @endif

        <div class="info-card">
            <p><strong>Tutor:</strong> ${tutoria.getTutor().getEstudiante().getNombre()}</p>
            <p><strong>Inicio:</strong> ${tutoria.getInicio().toString()}</p>
            <p><strong>Fin:</strong> ${tutoria.getFin().toString()}</p>
            <p><strong>Modalidad:</strong> ${String.valueOf(tutoria.getModalidad())}</p>
            @if(tutoria.getSala() != null && !tutoria.getSala().isBlank())
                <p><strong>Sala:</strong> ${tutoria.getSala()}</p>
            @endif
            <p><strong>Capacidad:</strong> ${tutoria.getCapacidad()}</p>
            <p><strong>Cupos disponibles:</strong> ${cuposDisponibles}</p>
            <p><strong>Estado:</strong> ${String.valueOf(tutoria.getEstado())}</p>
        </div>

        @if(!"ADMIN".equals(usuarioRol))
            @if("PUBLICADA".equals(String.valueOf(tutoria.getEstado())) && cuposDisponibles > 0)
                <form method="post" action="/tutorias/${tutoria.getId()}/reservar">
                    <button type="submit" class="tut-btn">Reservar tutoría</button>
                </form>
            @else
                <div class="tut-vacio">Esta tutoría no admite nuevas reservas.</div>
            @endif
        @endif
    </section>
`
)
