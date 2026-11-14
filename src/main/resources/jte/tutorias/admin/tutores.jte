@import cl.ucn.app.model.Asignatura
@import cl.ucn.app.model.Tutor
@import java.util.List

@param String title
@param String usuarioNombre
@param List<Tutor> tutores
@param String mensaje = null

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <link rel="stylesheet" href="/css/tutorias.css">
    <section class="tutorias">
        @template.tutorias.admin.menu(activo = "tutores")

        <div class="tut-encabezado">
            <h1>Tutores</h1>
            <a class="tut-btn" href="/tutorias/admin/tutores/nuevo">Registrar tutor</a>
        </div>

        @if(mensaje != null)
            <div class="tut-alerta-exito">${mensaje}</div>
        @endif

        <div class="info-card">
            @if(tutores.isEmpty())
                <p class="tut-vacio">No hay tutores registrados.</p>
            @else
                <table class="tut-tabla">
                    <thead>
                    <tr>
                        <th>Nombre</th>
                        <th>Correo</th>
                        <th>Asignaturas certificadas</th>
                    </tr>
                    </thead>
                    <tbody>
                    @for(Tutor tutor : tutores)
                        <tr>
                            <td>${tutor.getEstudiante().getNombre()}</td>
                            <td>${tutor.getEstudiante().getCorreo()}</td>
                            <td>
                                @for(Asignatura asignatura : tutor.getAsignaturasCertificadas())
                                    <span class="tut-etiqueta" title="${asignatura.getNombre()}">${asignatura.getCodigo()}</span>
                                @endfor
                            </td>
                        </tr>
                    @endfor
                    </tbody>
                </table>
            @endif
        </div>
    </section>
`
)
