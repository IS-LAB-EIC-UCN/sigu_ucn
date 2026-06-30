@import java.util.List
@import cl.ucn.app.model.Ticket
@import cl.ucn.app.model.CategoriaTicket

@param List<Ticket> tickets
@param List<CategoriaTicket> categorias
@param String usuarioRol

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Soporte - Lista de Tickets</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; background: #f4f6f9; }
        .container { max-width: 1000px; margin: auto; background: white; padding: 25px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }
        .filtros { display: flex; gap: 10px; margin: 15px 0; }
        select, input { padding: 6px; border: 1px solid #ccc; border-radius: 4px; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th { background: #3498db; color: white; padding: 10px; text-align: left; }
        td { padding: 10px; border-bottom: 1px solid #eee; }
        tr:hover { background: #f9f9f9; }
        .badge { padding: 4px 10px; border-radius: 12px; font-size: 0.85em; font-weight: bold; }
        .ABIERTO { background: #d5f5e3; color: #1e8449; }
        .CERRADO { background: #fadbd8; color: #922b21; }
        .EN_PROCESO { background: #fef9e7; color: #b7950b; }
        .btn { background: #3498db; color: white; padding: 8px 16px; border-radius: 4px; text-decoration: none; display: inline-block; }
        .btn-verde { background: #2ecc71; }
        a { color: #3498db; text-decoration: none; }
    </style>
</head>
<body>
<div class="container">
    <h1>Tickets de Soporte</h1>
    <p>Rol actual: <strong>${usuarioRol}</strong></p>

    <a href="/soporte/nuevo" class="btn btn-verde">+ Crear nuevo ticket</a>
    <a href="/home" class="btn" style="margin-left:8px;">← Volver al inicio</a>

    <form method="get" action="/soporte" class="filtros">
        <select name="estado">
            <option value="">Todos los estados</option>
            <option value="ABIERTO">Abierto</option>
            <option value="EN_PROCESO">En proceso</option>
            <option value="CERRADO">Cerrado</option>
        </select>
        <select name="prioridad">
            <option value="">Todas las prioridades</option>
            <option value="BAJA">Baja</option>
            <option value="MEDIA">Media</option>
            <option value="ALTA">Alta</option>
        </select>
        <button type="submit" class="btn">Filtrar</button>
    </form>

    @if(tickets.isEmpty())
        <p style="color:#888;">No hay tickets que coincidan con el filtro.</p>
    @else
        <table>
            <tr>
                <th>#</th>
                <th>Título</th>
                <th>Categoría</th>
                <th>Prioridad</th>
                <th>Estado</th>
                <th>Técnico</th>
            </tr>
            @for(Ticket t : tickets)
                <tr>
                    <td>${t.getId()}</td>
                    <td><a href="/soporte/${t.getId()}">${t.getTitulo()}</a></td>
                    <td>${t.getCategoria() != null ? t.getCategoria().getNombreCategoria() : "-"}</td>
                    <td>${t.getPrioridad()}</td>
                    <td><span class="badge ${t.getEstado()}">${t.getEstado()}</span></td>
                    <td>${t.getTecnico() != null ? t.getTecnico().getNombre() : "Sin asignar"}</td>
                </tr>
            @endfor
        </table>
    @endif
</div>
</body>
</html>
