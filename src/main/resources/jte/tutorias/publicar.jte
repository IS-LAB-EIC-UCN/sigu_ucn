@param String title
@param String usuarioNombre
@param String usuarioRol = null
@param boolean esTutor = false
@param java.util.List<cl.ucn.app.model.Asignatura> asignaturas
@param cl.ucn.app.model.ModalidadTutoria[] modalidades
@param java.util.Map<String, String> form = java.util.Map.of()
@param String error = null
@param String campoError = null

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
usuarioRol = usuarioRol,
esTutor = esTutor,
content = @`
    <link rel="stylesheet" href="/css/tutorias.css">
    <section class="tutorias">
        <h1>Publicar tutoría</h1>

        @if(error != null)
            <div class="alert-error">${error}</div>
        @endif

        @if(asignaturas.isEmpty())
            <div class="tut-vacio">No tienes asignaturas certificadas. Contacta al administrador.</div>
        @else
            <div class="info-card tut-formulario">
                <form method="post" action="/tutorias">
                    <div class="form-group">
                        <label for="asignaturaId">Asignatura</label>
                        <select id="asignaturaId" name="asignaturaId" required
                                class="${"asignaturaId".equals(campoError) ? "campo-invalido" : null}">
                            @for(var asignatura : asignaturas)
                                @if(String.valueOf(asignatura.getId()).equals(form.get("asignaturaId")))
                                    <option value="${asignatura.getId()}" selected>${asignatura.getCodigo()} - ${asignatura.getNombre()}</option>
                                @else
                                    <option value="${asignatura.getId()}">${asignatura.getCodigo()} - ${asignatura.getNombre()}</option>
                                @endif
                            @endfor
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="fecha">Fecha</label>
                        <input type="date" id="fecha" name="fecha" required value="${form.get("fecha")}"
                               class="${"fecha".equals(campoError) ? "campo-invalido" : null}">
                    </div>

                    <div class="form-group">
                        <label for="horaInicio">Hora de inicio</label>
                        <input type="time" id="horaInicio" name="horaInicio" min="07:00" max="22:00" required
                               value="${form.get("horaInicio")}"
                               class="${"horaInicio".equals(campoError) ? "campo-invalido" : null}">
                    </div>

                    <div class="form-group">
                        <label for="horaFin">Hora de término</label>
                        <input type="time" id="horaFin" name="horaFin" min="07:00" max="22:00" required
                               value="${form.get("horaFin")}"
                               class="${"horaFin".equals(campoError) ? "campo-invalido" : null}">
                    </div>

                    <div class="form-group">
                        <label for="modalidad">Modalidad</label>
                        <select id="modalidad" name="modalidad" required>
                            @for(var modalidad : modalidades)
                                @if(modalidad.name().equals(form.get("modalidad")))
                                    <option value="${modalidad.name()}" selected>${modalidad.name()}</option>
                                @else
                                    <option value="${modalidad.name()}">${modalidad.name()}</option>
                                @endif
                            @endfor
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="sala">Sala (solo presencial)</label>
                        <input type="text" id="sala" name="sala" maxlength="60" value="${form.get("sala")}"
                               class="${"sala".equals(campoError) ? "campo-invalido" : null}">
                    </div>

                    <div class="form-group">
                        <label for="capacidad">Cupo</label>
                        <input type="number" id="capacidad" name="capacidad" min="1" required
                               value="${form.get("capacidad")}"
                               class="${"capacidad".equals(campoError) ? "campo-invalido" : null}">
                    </div>

                    <div class="tut-botones">
                        <button type="submit" class="tut-btn">Publicar</button>
                        <a class="tut-btn tut-btn-secundario" href="/tutorias">Cancelar</a>
                    </div>
                </form>
            </div>
        @endif
    </section>
`
)
