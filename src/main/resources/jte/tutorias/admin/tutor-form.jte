@import cl.ucn.app.dto.TutorForm
@import cl.ucn.app.model.Asignatura
@import java.util.List

@param String title
@param String usuarioNombre
@param TutorForm form
@param List<Asignatura> asignaturas
@param String error = null
@param String campoError = null

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <link rel="stylesheet" href="/css/tutorias.css">
    <section class="tutorias">
        @template.tutorias.admin.menu(activo = "tutores")

        <h1>Registrar tutor</h1>
        <p>Ingrese el correo institucional de un estudiante registrado y marque las asignaturas que podrá impartir.</p>

        @if(error != null)
            <div class="alert-error">${error}</div>
        @endif

        <div class="info-card tut-formulario">
            <form method="post" action="/tutorias/admin/tutores">
                <div class="form-group">
                    <label for="correo">Correo institucional</label>
                    <input type="text" id="correo" name="correo" maxlength="120"
                           placeholder="nombre.apellido@alumnos.ucn.cl"
                           value="${form.correo()}" class="${"correo".equals(campoError) ? "campo-invalido" : null}">
                </div>

                <fieldset class="tut-fieldset${"asignaturas".equals(campoError) ? " campo-invalido" : ""}">
                    <legend>Asignaturas que puede impartir</legend>
                    @if(asignaturas.isEmpty())
                        <p class="tut-vacio">No hay asignaturas registradas. Primero cree una en la sección Asignaturas.</p>
                    @else
                        @for(Asignatura asignatura : asignaturas)
                            <label class="tut-opcion">
                                <input type="checkbox" name="asignaturaIds" value="${asignatura.getId()}"
                                       checked="${form.incluye(asignatura.getId())}">
                                ${asignatura.getCodigo()} - ${asignatura.getNombre()}
                            </label>
                        @endfor
                    @endif
                </fieldset>

                <div class="tut-botones">
                    <button type="submit" class="tut-btn">Registrar</button>
                    <a class="tut-btn tut-btn-secundario" href="/tutorias/admin/tutores">Cancelar</a>
                </div>
            </form>
        </div>
    </section>
`
)
