@param String title
@param String usuarioNombre
@param String csrfToken
@param String error
@param cl.ucn.app.model.Espacio espacio
@template.layout.main(title=title,usuarioNombre=usuarioNombre,content=@`
<section class="eventos-panel"><a class="enlace-suave" href="/eventos/catalogos">← Volver al catálogo</a><header class="eventos-cabecera"><div><p class="eyebrow">ADMINISTRACIÓN</p><h1>Editar ubicación</h1><p>Actualiza sus datos sin comprometer los cupos de los eventos programados.</p></div></header>
@if(!error.isBlank())<div class="evento-alerta error" role="alert">${error}</div>@endif
<form class="eventos-formulario" action="/eventos/ubicaciones/${espacio.getId()}/editar" method="post"><input type="hidden" name="csrfToken" value="${csrfToken}"><div class="eventos-form-grid">
<div class="eventos-campo"><label for="nombre">Nombre *</label><input id="nombre" name="nombre" value="${espacio.getNombre()}" maxlength="100" required></div>
<div class="eventos-campo"><label for="tipo">Tipo *</label><input id="tipo" name="tipo" value="${espacio.getTipo()}" maxlength="50" required></div>
<div class="eventos-campo"><label for="capacidad">Capacidad *</label><input id="capacidad" name="capacidad" type="number" min="1" value="${espacio.getCapacidad()}" required></div>
<div class="eventos-campo"><label for="disponible">Disponibilidad *</label><select id="disponible" name="disponible"><option value="true" selected="${Boolean.TRUE.equals(espacio.getDisponible())}">Disponible</option><option value="false" selected="${!Boolean.TRUE.equals(espacio.getDisponible())}">No disponible</option></select></div>
</div><div class="eventos-acciones"><button class="eventos-btn" type="submit">Guardar ubicación</button><a class="eventos-btn secundario" href="/eventos/catalogos">Cancelar</a></div></form></section>`)
