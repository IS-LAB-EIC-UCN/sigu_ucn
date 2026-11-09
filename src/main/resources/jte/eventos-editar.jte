
@param String title
@param String usuarioNombre
@param cl.ucn.app.model.Evento evento

@template.layout.main(
    title = title,
    usuarioNombre = usuarioNombre,
    content = @`

<section class="dashboard eventos-panel">
  <h1>Editar evento académico</h1>
  <p>Modifica la información del evento.</p>

  <form
    action="/eventos/${evento.getId()}/editar"
    method="post"
    class="eventos-formulario"
  >
    <div class="eventos-form-grid">
      <div class="eventos-campo eventos-campo-completo">
        <label for="titulo">Título del evento</label>
        <input
          type="text"
          id="titulo"
          name="titulo"
          value="${evento.getTitulo()}"
          maxlength="150"
          required
        />
      </div>

      <div class="eventos-campo eventos-campo-completo">
        <label for="descripcion">Descripción</label>
        <textarea id="descripcion" name="descripcion" rows="4">
${evento.getDescripcion()}</textarea
        >
      </div>

      <div class="eventos-campo">
        <label for="fecha">Fecha</label>
        <input
          type="date"
          id="fecha"
          name="fecha"
          value="${evento.getFecha().toString()}"
          required
        />
      </div>

      <div class="eventos-campo">
        <label for="cupoMaximo">Cupo máximo</label>
        <input
          type="number"
          id="cupoMaximo"
          name="cupoMaximo"
          value="${evento.getCupoMaximo()}"
          min="1"
          required
        />
      </div>

      <div class="eventos-campo">
        <label for="horaInicio">Hora de inicio</label>
        <input
          type="time"
          id="horaInicio"
          name="horaInicio"
          value="${evento.getHoraInicio().toString()}"
          required
        />
      </div>

      <div class="eventos-campo">
        <label for="horaFin">Hora de término</label>
        <input
          type="time"
          id="horaFin"
          name="horaFin"
          value="${evento.getHoraFin().toString()}"
          required
        />
      </div>

      <div class="eventos-campo eventos-campo-completo">
        <label for="lugar">Lugar</label>
        <input
          type="text"
          id="lugar"
          name="lugar"
          value="${evento.getLugar()}"
          maxlength="150"
          required
        />
      </div>
    </div>

    <div class="eventos-acciones">
      <button type="submit" class="eventos-btn">Guardar cambios</button>

      <a href="/eventos">Cancelar y volver</a>
    </div>
  </form>
</section>
` )
