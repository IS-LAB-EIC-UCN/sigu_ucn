@param String title
@param String usuarioNombre
@param String csrfToken
@param String error
@param String aviso
@param cl.ucn.app.model.Evento evento
@param java.util.Map<String,String> datos
@param java.util.List<cl.ucn.app.model.Categoria> categorias
@param java.util.List<cl.ucn.app.model.Expositor> expositores
@param java.util.List<cl.ucn.app.model.Espacio> espacios
@template.layout.main(title=title,usuarioNombre=usuarioNombre,content=@`
<section class="eventos-panel"><a href="/eventos" class="enlace-suave">← Volver a eventos</a>
<header class="eventos-cabecera"><div><p class="eyebrow">ORGANIZACIÓN ACADÉMICA</p><h1>${evento==null ? "Crear un evento" : "Editar evento"}</h1><p>Define los detalles. Podrás publicar la actividad cuando esté lista.</p></div><span class="badge">${evento==null ? "Nuevo borrador" : evento.getEstado()}</span></header>
@if(!aviso.isBlank())<div class="evento-alerta exito" role="status">${aviso}</div>@endif
@if(!error.isBlank())<div class="evento-alerta error" role="alert">${error}</div>@endif
<div class="formulario-layout"><form class="eventos-formulario" action="${evento==null ? "/eventos/crear" : "/eventos/"+evento.getId()+"/editar"}" method="post">
<input type="hidden" name="csrfToken" value="${csrfToken}">
<h2>01 · Información del evento</h2><p class="ayuda">Los campos marcados con * son obligatorios.</p><div class="eventos-form-grid">
<div class="eventos-campo eventos-campo-completo"><label for="titulo">Título *</label><input id="titulo" name="titulo" value="${datos.getOrDefault("titulo","")}" maxlength="150" placeholder="Ej. Seminario de innovación universitaria" required></div>
<div class="eventos-campo"><label for="categoriaId">Categoría *</label><select id="categoriaId" name="categoriaId" required><option value="">Selecciona una categoría</option>@for(var c:categorias)<option value="${c.getId()}" selected="${c.getId().toString().equals(datos.get("categoriaId"))}">${c.getNombre()}</option>@endfor</select><a href="/eventos/catalogos" class="ayuda">Registrar una categoría</a></div>
<fieldset class="eventos-campo"><legend>Expositores *</legend><small class="ayuda">Selecciona uno o más expositores.</small><div class="expositores-opciones">@for(var x:expositores)<label class="expositor-opcion"><input type="checkbox" name="expositorIds" value="${x.getId()}" checked="${java.util.Arrays.asList(datos.getOrDefault("expositorIds","").split(",")).contains(x.getId().toString())}"><span>${x.getNombre()}<small>${x.getInstitucion()}</small></span></label>@endfor</div><a href="/eventos/catalogos" class="ayuda">Registrar o editar expositores</a></fieldset>
<div class="eventos-campo eventos-campo-completo"><label for="descripcion">Descripción</label><textarea id="descripcion" name="descripcion" rows="4" maxlength="5000" placeholder="¿De qué trata la actividad y a quién está dirigida?">${datos.getOrDefault("descripcion","")}</textarea></div>
</div><h2>02 · Programación y cupos</h2><div class="eventos-form-grid">
<div class="eventos-campo"><label for="fecha">Fecha *</label><input type="date" id="fecha" name="fecha" min="${java.time.LocalDate.now().toString()}" value="${datos.getOrDefault("fecha","")}" required></div>
<div class="eventos-campo"><label for="cupoMaximo">Cupo máximo *</label><input type="number" id="cupoMaximo" name="cupoMaximo" min="1" value="${datos.getOrDefault("cupoMaximo","")}" required></div>
<div class="eventos-campo"><label for="horaInicio">Hora de inicio *</label><input type="time" id="horaInicio" name="horaInicio" value="${datos.getOrDefault("horaInicio","")}" required></div>
<div class="eventos-campo"><label for="horaFin">Hora de término *</label><input type="time" id="horaFin" name="horaFin" value="${datos.getOrDefault("horaFin","")}" required></div>
<div class="eventos-campo eventos-campo-completo"><label for="espacioId">Ubicación *</label><select id="espacioId" name="espacioId" required><option value="">Selecciona una ubicación</option>@for(var x:espacios)@if(Boolean.TRUE.equals(x.getDisponible()))<option value="${x.getId()}" selected="${x.getId().toString().equals(datos.get("espacioId"))}">${x.getNombre()} · ${x.getCapacidad()} personas</option>@endif@endfor</select><small class="ayuda">El cupo no puede superar la capacidad de la sala.</small></div>
</div><div class="eventos-acciones"><button class="eventos-btn" type="submit">Guardar evento</button><a class="eventos-btn secundario" href="/eventos">Volver sin guardar</a></div></form>
<aside class="evento-consejos"><span class="eyebrow">ANTES DE PUBLICAR</span><h3>Una actividad bien organizada</h3><p>Elige un título claro y una temática que facilite encontrar el evento.</p><hr><p>La sala debe estar disponible. El sistema comprueba que no coincida con otro evento.</p><hr><p>Al publicar se abren las inscripciones. El cupo y los registros se controlan automáticamente.</p></aside></div>
</section>`)
