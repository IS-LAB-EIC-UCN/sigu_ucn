@import cl.ucn.app.model.Asignatura
@import java.util.List

@param String title
@param String usuarioNombre
@param List<Asignatura> asignaturas
@param String mensaje = null
@param String error = null

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <link rel="stylesheet" href="/css/tutorias.css">
    <section class="tutorias">
        @template.tutorias.admin.menu(activo = "asignaturas")

        <div class="tut-encabezado">
            <h1>Asignaturas</h1>
            <a class="tut-btn" href="/tutorias/admin/asignaturas/nueva">Nueva asignatura</a>
        </div>

        @if(mensaje != null)
            <div class="tut-alerta-exito">${mensaje}</div>
        @endif
        @if(error != null)
            <div class="alert-error">${error}</div>
        @endif

        <div class="info-card">
            @if(asignaturas.isEmpty())
                <p class="tut-vacio">No hay asignaturas registradas.</p>
            @else
                <table class="tut-tabla">
                    <thead>
                    <tr>
                        <th>Código</th>
                        <th>Nombre</th>
                        <th>Carrera</th>
                        <th>Semestre</th>
                        <th>Acciones</th>
                    </tr>
                    </thead>
                    <tbody>
                    @for(Asignatura asignatura : asignaturas)
                        <tr>
                            <td>${asignatura.getCodigo()}</td>
                            <td>${asignatura.getNombre()}</td>
                            <td>${asignatura.getCarrera()}</td>
                            <td>${asignatura.getSemestre()}</td>
                            <td>
                                <div class="tut-acciones">
                                    <a class="tut-btn tut-btn-secundario"
                                       href="/tutorias/admin/asignaturas/${asignatura.getId()}/editar">Editar</a>
                                    <form method="post"
                                          action="/tutorias/admin/asignaturas/${asignatura.getId()}/eliminar"
                                          onsubmit="return confirm('¿Seguro que desea eliminar esta asignatura?');">
                                        <button type="submit" class="tut-btn tut-btn-peligro">Eliminar</button>
                                    </form>
                                </div>
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
