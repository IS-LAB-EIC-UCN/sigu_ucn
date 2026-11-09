
@param String title
@param String usuarioNombre

@template.layout.main(
    title = title,
    usuarioNombre = usuarioNombre,
    content = @`


<section class="dashboard eventos-panel">
  <h1>Crear evento académico</h1>
  <p>Completa la información para registrar un evento.</p>

  <form action="/eventos/crear" method="post" class="eventos-formulario">
    <div class="eventos-form-grid">
      <div class="eventos-campo eventos-campo-completo">
        <label for="titulo">Título del evento</label>
        <input type="text" id="titulo" name="titulo" maxlength="150" required />
      </div>

      <div class="eventos-campo eventos-campo-completo">
        <label for="descripcion">Descripción</label>
        <textarea id="descripcion" name="descripcion" rows="4"></textarea>
      </div>

      <div class="eventos-campo">
        <label for="fecha">Fecha</label>
        <input type="date" id="fecha" name="fecha" required />
      </div>

      <div class="eventos-campo">
        <label for="cupoMaximo">Cupo máximo</label>
        <input
          type="number"
          id="cupoMaximo"
          name="cupoMaximo"
          min="1"
          required
        />
      </div>

      <div class="eventos-campo">
        <label for="horaInicio">Hora de inicio</label>
        <input type="time" id="horaInicio" name="horaInicio" required />
      </div>

      <div class="eventos-campo">
        <label for="horaFin">Hora de término</label>
        <input type="time" id="horaFin" name="horaFin" required />
      </div>

      <div class="eventos-campo eventos-campo-completo">
        <label for="lugar">Lugar</label>
        <input type="text" id="lugar" name="lugar" maxlength="150" required />
      </div>
    </div>

    <div class="eventos-acciones">
      <button type="submit" class="eventos-btn">Guardar evento</button>
      <a href="/eventos">Cancelar y volver</a>
    </div>
  </form>
</section>
` )
