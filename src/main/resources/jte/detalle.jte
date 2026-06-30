@import java.util.List
@import cl.ucn.app.model.Ticket
@import cl.ucn.app.model.Comentario

@param Ticket ticket
@param List<Comentario> comentarios
@param Long usuarioId
@param String usuarioRol

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Ticket #${ticket.getId()}</title>
    <link rel="stylesheet" href="/static/css/style.css">
</head>
<body>

<p><a href="/soporte">&larr; Volver al listado</a></p>

<h1>${ticket.getTitulo()}</h1>
<p><strong>Estado:</strong> ${ticket.getEstado()} &nbsp; <strong>Prioridad:</strong> ${ticket.getPrioridad()}</p>
<p><strong>Categoría:</strong> ${ticket.getCategoria() != null ? ticket.getCategoria().getNombreCategoria() : "-"}</p>
<p><strong>Descripción:</strong><br>${ticket.getDescripcion()}</p>
<p><strong>Solicitante:</strong> ${ticket.getSolicitante() != null ? ticket.getSolicitante().getNombre() : "-"}</p>
<p><strong>Técnico asignado:</strong> ${ticket.getTecnico() != null ? ticket.getTecnico().getNombre() : "Sin asignar"}</p>

@if("CERRADO".equals(ticket.getEstado()))
    <p><strong>Resolución:</strong> ${ticket.getResolucion()}</p>
@endif

@boolean esTecnicoAsignado = ticket.getTecnico() != null && usuarioId != null && ticket.getTecnico().getId().equals(usuarioId)
@boolean ticketCerrado = "CERRADO".equals(ticket.getEstado())

<hr>

@if(!ticketCerrado)

    <!-- Solo ADMIN asigna técnico -->
    @if("ADMIN".equals(usuarioRol))
        <h3>Asignar técnico</h3>
        <form method="post" action="/soporte/${ticket.getId()}/asignar">
            <label>ID del técnico: <input type="text" name="tecnicoId" required></label>
            <button type="submit">Asignar</button>
        </form>
    @endif

    <!-- Solo el técnico asignado pasa a EN_PROCESO -->
    @if("TECNICO".equals(usuarioRol) && esTecnicoAsignado && "ABIERTO".equals(ticket.getEstado()))
        <h3>Marcar en proceso</h3>
        <form method="post" action="/soporte/${ticket.getId()}/estado">
            <input type="hidden" name="estado" value="EN_PROCESO">
            <button type="submit">Marcar como En Proceso</button>
        </form>
    @endif

    <!-- Solo el técnico asignado cierra el ticket -->
    @if("TECNICO".equals(usuarioRol) && esTecnicoAsignado)
        <h3>Cerrar ticket</h3>
        <form method="post" action="/soporte/${ticket.getId()}/cerrar">
            <label>Resolución:<br>
                <textarea name="resolucion" required></textarea>
            </label><br>
            <button type="submit">Cerrar ticket</button>
        </form>
    @endif

    <!-- Cualquier usuario autenticado puede comentar -->
    <h3>Agregar comentario</h3>
    <form method="post" action="/soporte/${ticket.getId()}/comentar">
        <textarea name="contenido" required></textarea><br>
        <button type="submit">Comentar</button>
    </form>

@else
    <p><em>Este ticket está cerrado y no admite más modificaciones.</em></p>
@endif

<hr>

<h3>Historial de comentarios</h3>
<ul>
    @for(Comentario c : comentarios)
        <li>
            <strong>${c.getAutor() != null ? c.getAutor().getNombre() : "Desconocido"}</strong>
            (${c.getFecha()}): ${c.getContenido()}
        </li>
    @endfor
    @if(comentarios.isEmpty())
        <li>Sin comentarios todavía.</li>
    @endif
</ul>

</body>
</html>
