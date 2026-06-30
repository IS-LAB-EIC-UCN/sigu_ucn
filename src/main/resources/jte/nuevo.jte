@import java.util.List
@import cl.ucn.app.model.CategoriaTicket

@param List<CategoriaTicket> categorias
@param String error

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Nuevo ticket</title>
    <link rel="stylesheet" href="/static/css/style.css">
</head>
<body>

<h1>Crear nuevo ticket</h1>
<p><a href="/soporte">&larr; Volver al listado</a></p>

@if(error != null && !error.isBlank())
    <p style="color:red;">${error}</p>
@endif

<form method="post" action="/soporte/nuevo">
    <label>Título:<br>
        <input type="text" name="titulo" required maxlength="100">
    </label><br><br>

    <label>Descripción:<br>
        <textarea name="descripcion" required></textarea>
    </label><br><br>

    <label>Prioridad:<br>
        <select name="prioridad">
            <option value="BAJA">Baja</option>
            <option value="MEDIA" selected>Media</option>
            <option value="ALTA">Alta</option>
        </select>
    </label><br><br>

    <label>Categoría:<br>
        <select name="categoriaId" required>
            <option value="">-- Seleccione --</option>
            @for(CategoriaTicket c : categorias)
                <option value="${c.getId()}">${c.getNombreCategoria()}</option>
            @endfor
        </select>
    </label><br><br>

    <button type="submit">Crear ticket</button>
</form>

</body>
</html>
