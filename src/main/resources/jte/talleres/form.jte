@param String title
@param String usuarioNombre
@param boolean esAdmin
@param String flashOk
@param String flashError
@param String nombre
@param String categoria
@param String descripcion
@param String fecha
@param String horaInicio
@param String horaFin
@param String lugar
@param String cupoMaximo

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <section class="tl-page">
        <a class="tl-link" href="/talleres">← Volver a talleres</a>
        <div class="tl-header">
            <h1>Registrar nuevo taller</h1>
        </div>

        @template.talleres.flash(flashOk = flashOk, flashError = flashError)

        <div class="tl-card">
            <form class="tl-form" method="post" action="/talleres">
                <label>Nombre del taller *
                    <input type="text" name="nombre" maxlength="120" value="${nombre}" placeholder="Ej. Introducción a Python" required>
                </label>

                <div class="tl-fila">
                    <label>Categoría *
                        <input type="text" name="categoria" maxlength="50" value="${categoria}" placeholder="Ej. TECNOLOGIA, ARTE, DEPORTE" required>
                    </label>
                    <label>Cupo máximo *
                        <input type="number" name="cupoMaximo" min="1" value="${cupoMaximo}" placeholder="Ej. 20" required>
                    </label>
                </div>

                <div class="tl-fila">
                    <label>Fecha *
                        <input type="date" name="fecha" value="${fecha}" required>
                    </label>
                    <label>Hora inicio *
                        <input type="time" name="horaInicio" value="${horaInicio}" required>
                    </label>
                    <label>Hora término *
                        <input type="time" name="horaFin" value="${horaFin}" required>
                    </label>
                </div>

                <label>Lugar / Sala
                    <input type="text" name="lugar" maxlength="100" value="${lugar}" placeholder="Ej. Laboratorio L1">
                </label>

                <label>Descripción
                    <textarea name="descripcion" rows="3" maxlength="500" placeholder="Breve descripción de los contenidos del taller">${descripcion}</textarea>
                </label>

                <div class="tl-actions">
                    <button class="tl-btn" type="submit">Guardar taller</button>
                    <a class="tl-btn tl-btn-secondary" href="/talleres">Cancelar</a>
                </div>
            </form>
        </div>
    </section>
`
)
