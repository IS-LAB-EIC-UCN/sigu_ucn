@param String title
@param String usuarioNombre
@param String csrfToken
@param String error
@param cl.ucn.app.model.Expositor expositor
@template.layout.main(title=title,usuarioNombre=usuarioNombre,content=@`
<section class="eventos-panel"><a class="enlace-suave" href="/eventos/catalogos">← Volver al catálogo</a><header class="eventos-cabecera"><div><p class="eyebrow">ORGANIZACIÓN</p><h1>Editar expositor</h1><p>Los cambios se reflejan en todos los eventos donde participa.</p></div></header>
@if(!error.isBlank())<div class="evento-alerta error" role="alert">${error}</div>@endif
<form class="eventos-formulario" action="/eventos/expositores/${expositor.getId()}/editar" method="post"><input type="hidden" name="csrfToken" value="${csrfToken}"><div class="eventos-form-grid">
<div class="eventos-campo"><label for="nombre">Nombre *</label><input id="nombre" name="nombre" value="${expositor.getNombre()}" maxlength="100" required></div>
<div class="eventos-campo"><label for="correo">Correo</label><input id="correo" name="correo" type="email" value="${expositor.getCorreo()}" maxlength="120"></div>
<div class="eventos-campo"><label for="especialidad">Especialidad</label><input id="especialidad" name="especialidad" value="${expositor.getEspecialidad()}" maxlength="150"></div>
<div class="eventos-campo"><label for="institucion">Institución</label><input id="institucion" name="institucion" value="${expositor.getInstitucion()}" maxlength="150"></div>
</div><div class="eventos-acciones"><button class="eventos-btn" type="submit">Guardar expositor</button><a class="eventos-btn secundario" href="/eventos/catalogos">Cancelar</a></div></form></section>`)
