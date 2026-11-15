@import cl.ucn.app.dto.AsignaturaForm

@param String title
@param String usuarioNombre
@param AsignaturaForm form
@param Long asignaturaId = null
@param String error = null
@param String campoError = null

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <link rel="stylesheet" href="/css/tutorias.css">
    <section class="tutorias">
        @template.tutorias.admin.menu(activo = "asignaturas")

        <h1>${asignaturaId == null ? "Nueva asignatura" : "Editar asignatura"}</h1>

        @if(error != null)
            <div class="alert-error">${error}</div>
        @endif

        <div class="info-card tut-formulario">
            <form method="post"
                  action="${asignaturaId == null ? "/tutorias/admin/asignaturas" : "/tutorias/admin/asignaturas/" + asignaturaId}">
                <div class="form-group">
                    <label for="codigo">Código</label>
                    <input type="text" id="codigo" name="codigo" maxlength="20" placeholder="Ej: DCCB-101"
                           value="${form.codigo()}" class="${"codigo".equals(campoError) ? "campo-invalido" : null}">
                </div>

                <div class="form-group">
                    <label for="nombre">Nombre</label>
                    <input type="text" id="nombre" name="nombre" maxlength="120"
                           value="${form.nombre()}" class="${"nombre".equals(campoError) ? "campo-invalido" : null}">
                </div>

                <div class="form-group">
                    <label for="carrera">Carrera</label>
                    <input type="text" id="carrera" name="carrera" maxlength="120"
                           value="${form.carrera()}" class="${"carrera".equals(campoError) ? "campo-invalido" : null}">
                </div>

                <div class="form-group">
                    <label for="semestre">Semestre</label>
                    <input type="number" id="semestre" name="semestre"
                           value="${form.semestre()}" class="${"semestre".equals(campoError) ? "campo-invalido" : null}">
                </div>

                <div class="tut-botones">
                    <button type="submit" class="tut-btn">Guardar</button>
                    <a class="tut-btn tut-btn-secundario" href="/tutorias/admin/asignaturas">Cancelar</a>
                </div>
            </form>
        </div>
    </section>
`
)
