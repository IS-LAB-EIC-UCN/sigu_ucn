@param String title
@param String usuarioNombre
@param cl.ucn.app.model.Espacio espacio

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <section class="dashboard">

        <h1>Editar estacionamiento</h1>

        <p>Modifique los datos del espacio seleccionado.</p>

        <form method="post" action="/estacionamientos/editar/${espacio.getId()}">

            <div class="form-group">
                <label for="nombre">Nombre</label>
                <input
                        type="text"
                        id="nombre"
                        name="nombre"
                        value="${espacio.getNombre()}"
                        required>
            </div>

            <div class="form-group">
                <label for="tipo">Tipo</label>
                <input
                        type="text"
                        id="tipo"
                        name="tipo"
                        value="${espacio.getTipo()}"
                        required>
            </div>

            <div class="form-group">
                <label for="capacidad">Capacidad</label>
                <input
                        type="number"
                        id="capacidad"
                        name="capacidad"
                        value="${espacio.getCapacidad()}"
                        min="1"
                        required>
            </div>

            <button type="submit">Guardar cambios</button>
            <a href="/estacionamientos">Cancelar</a>

        </form>

    </section>
`
)