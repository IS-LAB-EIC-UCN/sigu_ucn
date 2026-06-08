@param String title
@param String usuarioNombre

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <section class="dashboard">
        <h1>Nuevo estacionamiento</h1>
        <p>Complete los datos para registrar un nuevo espacio de estacionamiento.</p>

        <form method="post" action="/estacionamientos" class="form">

            <div class="form-group">
                <label for="nombre">Nombre</label>
                <input type="text" id="nombre" name="nombre" required>
            </div>

            <div class="form-group">
                <label for="tipo">Tipo</label>
                <input type="text" id="tipo" name="tipo" value="ESTACIONAMIENTO" required>
            </div>

            <div class="form-group">
                <label for="capacidad">Capacidad</label>
                <input type="number" id="capacidad" name="capacidad" value="1" min="1" required>
            </div>

            <button type="submit">Guardar</button>
            <a href="/estacionamientos">Cancelar</a>

        </form>
    </section>
`
)