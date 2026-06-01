@import java.util.List
@import cl.ucn.app.model.biblioteca.PrestamoLibro
@param List<PrestamoLibro> prestamos
@param String error
@param String mensaje

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Registrar Devolución</title>
</head>
<body>
    <h1>Registrar Devolución</h1>

    @if(error != null)
        <p style="color:red">${error}</p>
    @endif
    @if(mensaje != null)
        <p style="color:green">${mensaje}</p>
    @endif

    <form method="post" action="/biblioteca/devolucion">
        <label>Préstamo activo:</label>
        <select name="prestamoId">
            @for(PrestamoLibro p : prestamos)
            <option value="${p.getId()}">
                ${p.getLector().getNombre()} —
                ${p.getEjemplar().getLibro().getTitulo()} —
                Vence: ${p.getFechaVencimiento()}
            </option>
            @endfor
        </select>
        <br/>
        <button type="submit">Registrar devolución</button>
    </form>
</body>
</html>