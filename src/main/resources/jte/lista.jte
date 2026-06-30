@import java.util.List
@import java.util.Map
@import cl.ucn.app.model.Ticket
@import cl.ucn.app.model.CategoriaTicket

@param List<Ticket> tickets
@param List<CategoriaTicket> categorias
@param String usuarioRol

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Soporte - Tickets</title>
    <link rel="stylesheet" href="/static/css/style.css">
</head>
<body>

<h1>Tickets de soporte</h1>

<p>
    <a href="/home">&larr; Volver al menú principal</a>
    @if(!"ADMIN".equals(usuarioRol))
        | <a href="/soporte/nuevo">+ Nuevo ticket</a>
    @endif
</p>

<form method="get" action="/soporte">
    <label>Estado:
        <select name="estado">
            <option value="">Todos</option>
            <option value="ABIERTO">Abierto</option>
            <option value="EN_PROCESO">En proceso</option>
            <option value="CERRADO">Cerrado</option>
        </select>
    </label>
    <label>Prioridad:
        <select name="prioridad">
            <option value="">Todas</option>
            <option value="BAJA">Baja</option>
            <option value="MEDIA">Media</option>
            <option value="ALTA">Alta</option>
        </select>
    </label>
    @if("ADMIN".equals(usuarioRol) || "TECNICO".equals(usuarioRol))
        <label>Técnico (id): <input type="text" name="tecnicoId"></label>
    @endif
    <button type="submit">Filtrar</button>
</form>

<table border="1" cellpadding="6">
    <thead>
    <tr>
        <th>ID</th>
        <th>Título</th>
        <th>Categoría</th>
        <th>Prioridad</th>
        <th>Estado</th>
        <th>Técnico</th>
        <th></th>
    </tr>
    </thead>
    <tbody>
    @for(Ticket t : tickets)
        <tr>
            <td>${t.getId()}</td>
            <td>${t.getTitulo()}</td>
            <td>${t.getCategoria() != null ? t.getCategoria().getNombreCategoria() : "-"}</td>
            <td>${t.getPrioridad()}</td>
            <td>${t.getEstado()}</td>
            <td>${t.getTecnico() != null ? t.getTecnico().getNombre() : "Sin asignar"}</td>
            <td><a href="/soporte/${t.getId()}">Ver</a></td>
        </tr>
    @endfor
    @if(tickets.isEmpty())
        <tr><td colspan="7">No hay tickets para mostrar.</td></tr>
    @endif
    </tbody>
</table>

</body>
</html>
