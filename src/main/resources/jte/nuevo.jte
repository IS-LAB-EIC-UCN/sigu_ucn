@import java.util.List
@import cl.ucn.app.model.CategoriaTicket

@param List<CategoriaTicket> categorias
@param String error

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Crear Ticket</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; background: #f4f6f9; }
        .container { max-width: 600px; margin: auto; background: white; padding: 25px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h2 { color: #2c3e50; }
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; font-weight: bold; }
        input, select, textarea { width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
        button { background-color: #2ecc71; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; }
        button:hover { background-color: #27ae60; }
        .error { background: #fadbd8; color: #922b21; padding: 10px; border-radius: 4px; margin-bottom: 15px; }
        a { color: #3498db; }
    </style>
</head>
<body>
<div class="container">
    <h2>Crear nuevo ticket</h2>

    @if(!error.isEmpty())
        <div class="error">${error}</div>
    @endif

    <form action="/soporte/nuevo" method="post">
        <div class="form-group">
            <label>Título:</label>
            <input type="text" name="titulo" required>
        </div>
        <div class="form-group">
            <label>Descripción:</label>
            <textarea name="descripcion" rows="4" required></textarea>
        </div>
        <div class="form-group">
            <label>Categoría:</label>
            <select name="categoriaId">
                @for(CategoriaTicket c : categorias)
                    <option value="${c.getId()}">${c.getNombreCategoria()}</option>
                @endfor
            </select>
        </div>
        <div class="form-group">
            <label>Prioridad:</label>
            <select name="prioridad">
                <option value="BAJA">Baja</option>
                <option value="MEDIA" selected>Media</option>
                <option value="ALTA">Alta</option>
            </select>
        </div>
        <button type="submit">Enviar ticket</button>
    </form>
    <p><a href="/soporte">← Volver a la lista</a></p>
</div>
</body>
</html>
