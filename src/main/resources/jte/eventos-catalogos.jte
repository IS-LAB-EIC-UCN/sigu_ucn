@param String title
@param String usuarioNombre
@param String csrfToken
@param String error
@param String aviso
@param boolean admin
@param java.util.List<cl.ucn.app.model.Expositor> expositores
@param java.util.List<cl.ucn.app.model.Espacio> espacios
@template.layout.main(title=title,usuarioNombre=usuarioNombre,content=@`
<section class="eventos-panel"><header class="eventos-cabecera"><div><p class="eyebrow">RECURSOS PARA TUS ACTIVIDADES</p><h1>Expositores y ubicaciones</h1><p>Prepara las personas y los espacios de tus próximos eventos.</p></div><a href="/eventos/nuevo" class="eventos-btn">Crear evento</a></header>
<nav class="eventos-nav" aria-label="Secciones de eventos"><a href="/eventos">Todos los eventos</a><a href="/eventos?vista=agenda">Agenda</a><a href="/eventos?vista=mios">Mis actividades</a><a href="/eventos/catalogos">Expositores y ubicaciones</a></nav>@if(!aviso.isBlank())<div class="evento-alerta exito" role="status">${aviso}</div>@endif
@if(!error.isBlank())<div class="evento-alerta error" role="alert">${error}</div>@endif
<div class="catalogos-grid"><section class="catalogo"><h2>Expositores <span class="badge">${expositores.size()}</span></h2><form class="catalogo-form" action="/eventos/expositores" method="post"><input type="hidden" name="csrfToken" value="${csrfToken}"><label for="nombre-expositor">Nombre *</label><input id="nombre-expositor" name="nombre" maxlength="100" required><label for="correo">Correo</label><input id="correo" name="correo" type="email" maxlength="120"><label for="especialidad">Especialidad o institución</label><input id="especialidad" name="especialidad" maxlength="150"><button class="eventos-btn" type="submit">Registrar expositor</button></form>
<div class="catalogo-items">@if(expositores.isEmpty())<p class="ayuda">Todavía no hay expositores. Registra el primero.</p>@endif @for(var x:expositores)<article><span class="avatar">${x.getNombre().substring(0,1).toUpperCase()}</span><div><h3>${x.getNombre()}</h3><p>${x.getEspecialidad()}</p><small>${x.getCorreo()}</small></div></article>@endfor</div></section>
<section class="catalogo"><h2>Ubicaciones <span class="badge">${espacios.size()}</span></h2>
@if(admin)<form class="catalogo-form" action="/eventos/ubicaciones" method="post"><input type="hidden" name="csrfToken" value="${csrfToken}"><label for="nombre-sala">Nombre de la ubicación *</label><input id="nombre-sala" name="nombre" maxlength="100" required><label for="capacidad">Capacidad *</label><input id="capacidad" type="number" name="capacidad" min="1" required><button class="eventos-btn" type="submit">Registrar ubicación</button></form>@else<p class="ayuda">El administrador gestiona la disponibilidad y el registro de ubicaciones.</p>@endif
<div class="catalogo-items">@for(var x:espacios)<article><div class="avatar">${x.getCapacidad()}</div><div><h3>${x.getNombre()}</h3><p>${x.getCapacidad()} personas · ${Boolean.TRUE.equals(x.getDisponible()) ? "Disponible" : "No disponible"}</p>@if(admin)<form action="/eventos/ubicaciones/${x.getId()}/disponibilidad" method="post"><input type="hidden" name="csrfToken" value="${csrfToken}"><button class="boton-texto" type="submit">${Boolean.TRUE.equals(x.getDisponible()) ? "Deshabilitar" : "Habilitar"}</button></form>@endif</div></article>@endfor</div></section></div></section>`)
