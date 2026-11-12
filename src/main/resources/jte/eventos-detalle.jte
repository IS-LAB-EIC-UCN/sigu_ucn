@param boolean admin = false
@param String title
@param String usuarioNombre
@param String csrfToken
@param cl.ucn.app.model.Evento evento
@param boolean consultaInscritos
@param boolean gestiona
@param boolean inscrito
@param long disponibles
@template.layout.main(administrador=admin,title=title,usuarioNombre=usuarioNombre,content=@`
<section class="eventos-panel"><a class="enlace-suave" href="/eventos">← Volver a la agenda</a>
<header class="eventos-cabecera"><div><p class="eyebrow">DETALLE DEL EVENTO</p><h1>${evento.getTitulo()}</h1><span class="badge tema">${evento.getTematica()}</span> <span class="badge estado-${evento.getEstado()}">${evento.getEstado()}</span></div></header>
<article class="catalogo"><dl class="evento-ficha"><div><dt>Fecha</dt><dd>${evento.getFecha().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))}</dd></div><div><dt>Horario</dt><dd>${evento.getHoraInicio().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))}–${evento.getHoraFin().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))}</dd></div><div><dt>Ubicación</dt><dd>${evento.getLugar()}</dd></div><div><dt>Cupos disponibles</dt><dd>${disponibles} de ${evento.getCupoMaximo()}</dd></div><div><dt>Categoría</dt><dd>${evento.getTematica()}</dd></div><div><dt>Organiza</dt><dd>${evento.getOrganizador().getNombre()}</dd></div></dl>
<h2>Sobre la actividad</h2><p class="evento-descripcion">${evento.getDescripcion()==null || evento.getDescripcion().isBlank() ? "Actividad de la comunidad universitaria." : evento.getDescripcion()}</p>
<h2>Expositores</h2><div class="catalogo-items">@if(evento.getExpositores().isEmpty())<p>Por confirmar.</p>@endif @for(var e:evento.getExpositores())<article><span class="avatar">${e.getNombre().substring(0,1).toUpperCase()}</span><div><h3>${e.getNombre()}</h3><p>${e.getInstitucion()}</p><small>${e.getEspecialidad()}</small></div></article>@endfor</div>
<p class="ayuda">Puedes cancelar tu inscripción hasta el inicio del evento: ${evento.getFecha().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))} a las ${evento.getHoraInicio().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))}.</p>
<div class="eventos-acciones">@if(evento.isDisponible())@if(inscrito)<form method="post" action="/eventos/${evento.getId()}/cancelar-inscripcion"><input type="hidden" name="csrfToken" value="${csrfToken}"><button class="eventos-btn secundario" type="submit">Cancelar mi inscripción</button></form>@else @if(disponibles>0)<form method="post" action="/eventos/${evento.getId()}/inscribir"><input type="hidden" name="csrfToken" value="${csrfToken}"><button class="eventos-btn" type="submit">Inscribirme</button></form>@else<span class="badge">Cupos agotados</span>@endif @endif @endif
@if(consultaInscritos)<a class="eventos-btn secundario" href="/eventos/${evento.getId()}/inscritos">Ver participantes</a>@endif</div></article></section>`)
