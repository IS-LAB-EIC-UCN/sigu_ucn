@import java.util.List
@import cl.ucn.app.model.Ticket
@import cl.ucn.app.model.Comentario

@param Ticket ticket
@param List<Comentario> comentarios
@param Object usuarioId
@param String usuarioRol

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Ticket #${ticket.getId()}</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; background: #f4f6f9; }
        .container { max-width: 750px; margin: auto; background: white; padding: 25px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .campo { margin-bottom: 12px; }
        .label { font-weight: bold; color: #555; margin-bottom: 3px; }
        .badge { padding: 4px 10px; border-radius: 12px; font-size: 0.85em; font-weight: bold; }
        .ABIERTO { background: #d5f5e3; color: #1e8449; }
        .CERRADO { background: #fadbd8; color: #922b21; }
        .EN_PROCESO { background: #fef9e7; color: #b7950b; }
        .seccion { margin-top: 25px; padding-top: 15px; border-top: 1px solid #eee; }
        select, input, textarea { padding: 6px; border: 1px solid #ccc; border-radius: 4px; }
        .btn-form { background: #e67e22; color: white; padding: 8px 16px; border: none; border-radius: 4px; cursor: pointer; }
        .comentario { background: #f8f9fa; padding: 10px; border-radius: 4px; margin-bottom: 8px; }
        .comentario .autor { font-weight: bold; font-size: 0.9em; color: #555; }
        .comentario .fecha { font-size: 0.8em; color: #999; }
        a { color: #3498db; }
    </style>
</head>
<body>
<div class="container">
    <h2>Ticket #${ticket.getId()} — ${ticket.getTitulo()}</h2>

    <div class="campo"><div class="label">Descripción</div><div>${ticket.getDescripcion()}</div></div>
    <div class="campo"><div class="label">Prioridad</div><div>${ticket.getPrioridad()}</div></div>
    <div class="campo"><div class="label">Estado</div>
        <div><span class="badge ${ticket.getEstado()}">${ticket.getEstado()}</span></div></div>
    <div class="campo"><div class="label">Solicitante</div>
        <div>${ticket.getSolicitante() != null ? ticket.getSolicitante().getNombre() : "-"}</div></div>
    <div class="campo"><div class="label">Técnico asignado</div>
        <div>${ticket.getTecnico() != null ? ticket.getTecnico().getNombre() : "Sin asignar"}</div></div>
    @if(ticket.getResolucion() != null)
        <div class="campo"><div class="label">Resolución</div><div>${ticket.getResolucion()}</div></div>
    @endif

    @if(!"CERRADO".equals(ticket.getEstado()))
        <div class="seccion">
            <h3>Asignar técnico</h3>
            <form action="/soporte/${ticket.getId()}/asignar" method="post">
                <input type="text" name="tecnicoId" placeholder="ID del técnico" required>
                <button type="submit" class="btn-form">Asignar</button>
            </form>
        </div>

        <div class="seccion">
            <h3>Cambiar estado</h3>
            <form action="/soporte/${ticket.getId()}/estado" method="post">
                <select name="estado">
                    <option value="ABIERTO">ABIERTO</option>
                    <option value="EN_PROCESO">EN_PROCESO</option>
                </select>
                <button type="submit" class="btn-form">Actualizar estado</button>
            </form>
        </div>

        <div class="seccion">
            <h3>Cerrar ticket</h3>
            <form action="/soporte/${ticket.getId()}/cerrar" method="post">
                <textarea name="resolucion" placeholder="Resolución (obligatoria)" rows="2" style="width:100%;box-sizing:border-box;"></textarea>
                <button type="submit" class="btn-form" style="margin-top:8px;">Cerrar ticket</button>
            </form>
        </div>
    @endif

    <div class="seccion">
        <h3>Comentarios</h3>
        @if(comentarios.isEmpty())
            <p style="color:#888;">Aún no hay comentarios.</p>
        @else
            @for(Comentario c : comentarios)
                <div class="comentario">
                    <div class="autor">${c.getAutor() != null ? c.getAutor().getNombre() : "Desconocido"}</div>
                    <div>${c.getContenido()}</div>
                </div>
            @endfor
        @endif

        @if(!"CERRADO".equals(ticket.getEstado()))
            <form action="/soporte/${ticket.getId()}/comentar" method="post" style="margin-top:10px;">
                <textarea name="contenido" placeholder="Escribe un comentario..." rows="2" style="width:100%;box-sizing:border-box;" required></textarea>
                <button type="submit" class="btn-form" style="margin-top:8px;">Agregar comentario</button>
            </form>
        @endif
    </div>

    <p style="margin-top:20px;"><a href="/soporte">← Volver a la lista</a></p>
</div>
</body>
</html>