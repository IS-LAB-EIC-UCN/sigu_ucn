@param cl.ucn.app.dto.RegistroForm form
@param String error
@param String campoError

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registro - SIGU-UCN</title>
    <link rel="stylesheet" href="/css/styles.css">
</head>
<body>
<div class="login-container">
    <div class="login-card">
        <h1>SIGU-UCN</h1>
        <h2>Registro de estudiante</h2>
        @if(error != null)
            <div class="error-message">${error}</div>
        @endif
        <form method="post" action="/registro" class="login-form">
            <label for="nombre">Nombre</label>
            <input id="nombre" name="nombre" value="${form.nombre()}" required>
            <label for="correo">Correo institucional</label>
            <input id="correo" type="email" name="correo" value="${form.correo()}" required>
            <label for="carrera">Carrera</label>
            <input id="carrera" name="carrera" value="${form.carrera()}" required>
            <label for="semestre">Semestre</label>
            <input id="semestre" type="number" min="1" max="12" name="semestre" value="${form.semestre()}" required>
            <label for="password">Contraseña</label>
            <input id="password" type="password" name="password" required>
            <label for="confirmacion">Confirmar contraseña</label>
            <input id="confirmacion" type="password" name="confirmacion" required>
            <button type="submit" class="btn-login">Registrarse</button>
        </form>
        <p><a href="/login">Volver al login</a></p>
    </div>
</div>
</body>
</html>
