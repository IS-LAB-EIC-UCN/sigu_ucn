@param String title
@param String usuarioNombre
@param String usuarioRol = null
@param boolean esTutor
@param java.util.List<cl.ucn.app.model.InscripcionTutoria> reservas
@param java.util.List<cl.ucn.app.model.Tutoria> tutorias

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
usuarioRol = usuarioRol,
esTutor = esTutor,
content = @`
    <link rel="stylesheet" href="/css/tutorias.css">
    <section class="tutorias">
        <h1>Mi agenda</h1>

        <h2>Mis reservas</h2>
        @if(reservas.isEmpty())
            <div class="info-card">No tienes reservas registradas.</div>
        @else
            @for(var reserva : reservas)
                <div class="info-card">
                    <strong>${reserva.getTutoria().getAsignatura().getNombre()}</strong>
                    <p>${reserva.getTutoria().getInicio().toString()} - ${reserva.getTutoria().getFin().toString()}</p>
                    <p>Estado: ${reserva.getEstado()}</p>
                </div>
            @endfor
        @endif

        @if(esTutor)
            <h2>Mis tutorías publicadas</h2>
            @if(tutorias.isEmpty())
                <div class="info-card">No tienes tutorías publicadas.</div>
            @else
                @for(var tutoria : tutorias)
                    <div class="info-card">
                        <strong>${tutoria.getAsignatura().getNombre()}</strong>
                        <p>${tutoria.getInicio().toString()} - ${tutoria.getFin().toString()}</p>
                        <p>Estado: ${tutoria.getEstado()}</p>
                    </div>
                @endfor
            @endif
        @endif
    </section>
`
)
