@import cl.ucn.app.dto.TutoriaForm
@import cl.ucn.app.model.Asignatura
@import java.util.List
@import java.util.Map

@param String title
@param String usuarioNombre
@param String usuarioRol = null
@param boolean esTutor = false
@param TutoriaForm form
@param List<Asignatura> asignaturas
@param Map<String, String> errores

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
usuarioRol = usuarioRol,
esTutor = esTutor,
content = @`
    <link rel="stylesheet" href="/css/tutorias.css">
    <section class="tutorias">
        <div class="tut-encabezado">
            <h1>Publicar tutoría</h1>
            <a class="tut-btn tut-btn-secundario" href="/tutorias">Volver al listado</a>
        </div>
        <p>Horario permitido: 07:00 a 22:00, duración mínima de 30 minutos y al menos 3 horas de anticipación.</p>

        @if(!errores.isEmpty())
            <div class="alert-error">Revisa los campos marcados.</div>
        @endif

        <div class="info-card tut-formulario">
            <form method="post" action="/tutorias">
                <div class="form-group">
                    <label for="asignaturaId">Asignatura</label>
                    <select id="asignaturaId" name="asignaturaId" class="${errores.containsKey("asignaturaId") ? "campo-invalido" : null}">
                        <option value="">Seleccione...</option>
                        @for(Asignatura asignatura : asignaturas)
                            <option value="${asignatura.getId()}" selected="${form.esAsignatura(asignatura.getId())}">${asignatura.getCodigo()} - ${asignatura.getNombre()}</option>
                        @endfor
                    </select>
                    @if(errores.containsKey("asignaturaId"))
                        <span class="tut-error-campo">${errores.get("asignaturaId")}</span>
                    @endif
                </div>

                <div class="form-group">
                    <label for="fecha">Fecha</label>
                    <input type="date" id="fecha" name="fecha" value="${form.fecha()}" class="${errores.containsKey("fecha") ? "campo-invalido" : null}">
                    @if(errores.containsKey("fecha"))
                        <span class="tut-error-campo">${errores.get("fecha")}</span>
                    @endif
                </div>

                <div class="form-group">
                    <label for="horaInicio">Hora de inicio</label>
                    <input type="time" id="horaInicio" name="horaInicio" value="${form.horaInicio()}" class="${errores.containsKey("horaInicio") ? "campo-invalido" : null}">
                    @if(errores.containsKey("horaInicio"))
                        <span class="tut-error-campo">${errores.get("horaInicio")}</span>
                    @endif
                </div>

                <div class="form-group">
                    <label for="horaFin">Hora de término</label>
                    <input type="time" id="horaFin" name="horaFin" value="${form.horaFin()}" class="${errores.containsKey("horaFin") ? "campo-invalido" : null}">
                    @if(errores.containsKey("horaFin"))
                        <span class="tut-error-campo">${errores.get("horaFin")}</span>
                    @endif
                </div>

                <div class="form-group">
                    <label for="modalidad">Modalidad</label>
                    <select id="modalidad" name="modalidad" class="${errores.containsKey("modalidad") ? "campo-invalido" : null}">
                        <option value="PRESENCIAL" selected="${form.esModalidad("PRESENCIAL")}">Presencial</option>
                        <option value="ONLINE" selected="${form.esModalidad("ONLINE")}">Online</option>
                    </select>
                    @if(errores.containsKey("modalidad"))
                        <span class="tut-error-campo">${errores.get("modalidad")}</span>
                    @endif
                </div>

                <div class="form-group">
                    <label for="sala">Sala (obligatoria si es presencial)</label>
                    <input type="text" id="sala" name="sala" maxlength="50" value="${form.sala()}" class="${errores.containsKey("sala") ? "campo-invalido" : null}">
                    @if(errores.containsKey("sala"))
                        <span class="tut-error-campo">${errores.get("sala")}</span>
                    @endif
                </div>

                <div class="form-group">
                    <label for="capacidad">Capacidad (cupos)</label>
                    <input type="number" id="capacidad" name="capacidad" min="1" value="${form.capacidad()}" class="${errores.containsKey("capacidad") ? "campo-invalido" : null}">
                    @if(errores.containsKey("capacidad"))
                        <span class="tut-error-campo">${errores.get("capacidad")}</span>
                    @endif
                </div>

                <div class="tut-botones">
                    <button type="submit" class="tut-btn">Publicar</button>
                    <a class="tut-btn tut-btn-secundario" href="/tutorias">Cancelar</a>
                </div>
            </form>
        </div>
    </section>
`
)
