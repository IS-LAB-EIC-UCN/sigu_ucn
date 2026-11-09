
@param String title
@param String usuarioNombre

@template.layout.main(
    title = title,
    usuarioNombre = usuarioNombre,
    content = @`
        <section class="dashboard">

            <h1>Crear evento académico</h1>

            <form action="/eventos/crear" method="post">

                <label>Título</label>
                <input type="text" name="titulo"
                       maxlength="150" required>

                <label>Descripción</label>
                <textarea name="descripcion"></textarea>

                <label>Fecha</label>
                <input type="date" name="fecha" required>

                <label>Hora de inicio</label>
                <input type="time" name="horaInicio" required>

                <label>Hora de término</label>
                <input type="time" name="horaFin" required>

                <label>Lugar</label>
                <input type="text" name="lugar"
                       maxlength="150" required>

                <label>Cupo máximo</label>
                <input type="number" name="cupoMaximo"
                       min="1" required>

                <button type="submit">
                    Guardar evento
                </button>

            </form>

            <p>
                <a href="/eventos">Volver a eventos</a>
            </p>

        </section>
    `
)
