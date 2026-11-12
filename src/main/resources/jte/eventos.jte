@param String title
@param String usuarioNombre
@param boolean puedeInscribirse
@param String csrfToken
@param String error
@param String aviso
@param boolean puedeOrganizar
@param boolean admin
@param Long usuarioId
@param java.util.List<cl.ucn.app.model.Evento> eventos
@param java.util.Map<Long,long[]> resumen
@param java.util.List<String> tematicas
@param String q
@param String tematica
@param String fecha
@param String vista
@param long proximos
@param long misInscripciones
@param long organizados
@template.layout.main(title=title, usuarioNombre=usuarioNombre, content=@`
<section class="eventos-panel">
<header class="eventos-cabecera"><div><p class="eyebrow">VIDA UNIVERSITARIA / MÓDULO 06</p><h1>Eventos académicos</h1><p>Conecta con nuevas ideas. Encuentra tu próxima actividad.</p></div>@if(puedeOrganizar)<a class="eventos-btn" href="/eventos/nuevo"><span aria-hidden="true">+</span> Crear evento</a>@endif</header>
<nav class="eventos-nav" aria-label="Secciones de eventos"><a href="/eventos">Todos los eventos</a><a href="/eventos?vista=agenda">Agenda</a>@if(!admin)<a href="/eventos?vista=mios">Mis actividades</a>@endif@if(puedeOrganizar || admin)<a href="/eventos/catalogos">${admin ? "Salas y categorías" : "Expositores y ubicaciones"}</a>@endif</nav>@if(!aviso.isBlank())<div class="evento-alerta exito" role="status">${aviso}</div>@endif
@if(!error.isBlank())<div class="evento-alerta error" role="alert">${error}</div>@endif
<div class="eventos-metricas"><article><span>PRÓXIMOS EVENTOS</span><strong>${proximos}</strong><small>Actividades con inscripción abierta</small></article>@if(puedeInscribirse)<article><span>MIS INSCRIPCIONES</span><strong>${misInscripciones}</strong><small>Participaciones activas</small></article>@endif @if(puedeOrganizar)<article><span>MIS EVENTOS</span><strong>${organizados}</strong><small>Actividades que organizas</small></article>@endif</div>
<form class="eventos-filtros" method="get" action="/eventos">
<input type="hidden" name="vista" value="${vista}">
<div><label for="buscar">Buscar evento</label><input id="buscar" type="search" name="q" value="${q}" placeholder="Título o ubicación"></div>
<div><label for="tema">Categoría</label><select id="tema" name="tematica"><option value="">Todas las categorías</option>@for(var t:tematicas)<option value="${t}" selected="${t.equals(tematica)}">${t}</option>@endfor</select></div>
<div><label for="fecha">Fecha</label><input id="fecha" type="date" name="fecha" value="${fecha}"></div>
<button class="eventos-btn" type="submit">Aplicar filtros</button><a class="enlace-suave" href="/eventos">Limpiar</a>
</form>
<div class="eventos-seccion"><h2>${"agenda".equals(vista) ? "Agenda de próximos eventos" : "mios".equals(vista) ? "Mis actividades" : "Explora los eventos"}</h2><span>${eventos.size()} resultados</span></div>
@if(eventos.isEmpty())
<div class="eventos-vacio"><span class="vacio-icono" aria-hidden="true">◇</span><h3>No hay eventos para mostrar</h3><p>Prueba con otros filtros o crea una nueva actividad académica.</p>@if(puedeOrganizar)<a href="/eventos/nuevo" class="eventos-btn">Crear un evento</a>@endif</div>
@else
<div class="eventos-lista">
@for(var e:eventos)
<article class="evento-tarjeta">
<div class="evento-fecha"><strong>${e.getFecha().getDayOfMonth()}</strong><span>${e.getFecha().format(java.time.format.DateTimeFormatter.ofPattern("MMM",java.util.Locale.forLanguageTag("es"))).toUpperCase()}</span><small>${e.getFecha().getYear()}</small></div>
<div class="evento-info"><div class="evento-etiquetas"><span class="badge tema">${e.getTematica()}</span><span class="badge estado-${e.getEstado()}">${e.getEstado()}</span>@if(resumen.getOrDefault(e.getId(),new long[]{0,0})[1]>0)<span class="badge inscrito">Inscripción activa</span>@endif</div>
<h3><a class="evento-titulo" href="/eventos/${e.getId()}/detalle">${e.getTitulo()}</a></h3><p class="evento-descripcion">${e.getDescripcion()==null || e.getDescripcion().isBlank() ? "Actividad académica de la comunidad universitaria." : e.getDescripcion()}</p>
<div class="evento-detalles"><span>Horario · ${e.getHoraInicio().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))}–${e.getHoraFin().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))}</span><span>Cancelación hasta · ${e.getFecha().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))} ${e.getHoraInicio().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))}</span><span>Ubicación · ${e.getLugar()}</span><span>Expositores · ${e.getExpositores().isEmpty() ? "Por confirmar" : e.getNombresExpositores()}</span></div>
<div class="evento-pie"><span class="evento-cupo"><strong>${Math.max(0,e.getCupoMaximo()-resumen.getOrDefault(e.getId(),new long[]{0,0})[0])}</strong> de ${e.getCupoMaximo()} cupos disponibles</span>
<div class="eventos-acciones">
@if(puedeInscribirse && e.isDisponible())
@if(resumen.getOrDefault(e.getId(),new long[]{0,0})[1]>0)
<form method="post" action="/eventos/${e.getId()}/cancelar-inscripcion" onsubmit="return confirm('¿Cancelar tu inscripción?');"><input type="hidden" name="csrfToken" value="${csrfToken}"><button class="eventos-btn secundario" type="submit">Cancelar inscripción</button></form>
@else
@if(resumen.getOrDefault(e.getId(),new long[]{0,0})[0]<e.getCupoMaximo())
<form method="post" action="/eventos/${e.getId()}/inscribir"><input type="hidden" name="csrfToken" value="${csrfToken}"><button class="eventos-btn" type="submit">Inscribirme</button></form>
@else <span class="badge">Cupos agotados</span> @endif
@endif
@endif
@if(admin && ("PUBLICADO".equals(e.getEstado()) || "FINALIZADO".equals(e.getEstado())))
<a class="eventos-btn secundario" href="/eventos/${e.getId()}/inscritos">Supervisar inscritos</a>
@endif
@if(puedeOrganizar && usuarioId.equals(e.getOrganizador().getId()))
<a class="eventos-btn secundario" href="/eventos/${e.getId()}/inscritos">Participantes</a>
@if("CANCELADO".equals(e.getEstado()))
<form method="post" action="/eventos/${e.getId()}/reactivar" onsubmit="return confirm('El evento volvera a borrador. Las inscripciones anteriores quedaran canceladas. Deberas revisar los datos y publicarlo nuevamente. Continuar?');"><input type="hidden" name="csrfToken" value="${csrfToken}"><button class="eventos-btn" type="submit">Reactivar como borrador</button></form>
@endif
@if(!"CANCELADO".equals(e.getEstado()) && !"FINALIZADO".equals(e.getEstado()))
<a class="eventos-btn secundario" href="/eventos/${e.getId()}/editar">Editar</a>
@if("BORRADOR".equals(e.getEstado()))
<form method="post" action="/eventos/${e.getId()}/publicar"><input type="hidden" name="csrfToken" value="${csrfToken}"><button class="eventos-btn" type="submit">Publicar</button></form>
@endif
<form method="post" action="/eventos/${e.getId()}/cancelar" onsubmit="return confirm('¿Cancelar el evento? Se conservará su historial.');"><input type="hidden" name="csrfToken" value="${csrfToken}"><button class="eventos-btn peligro" type="submit">Cancelar evento</button></form>
@endif
@endif
</div></div></div></article>
@endfor
</div>
@endif
</section>`)
