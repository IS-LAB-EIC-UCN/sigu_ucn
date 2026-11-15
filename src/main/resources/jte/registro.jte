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
        <div class="form-group">
            <label for="nombre" >Nombre</label>
            <input id="nombre" name="nombre" placeholder="Ingrese su nombre" value="${form.nombre()}" required>
        </div>
        <div class="form-group">
            <label for="correo">Correo institucional</label>
            <input id="correo" type="email" name="correo" placeholder="Ingrese su correo" value="${form.correo()}" required>
        </div>
        <div class="form-group">
            <label for="carrera">Carrera</label>
            <input id="carrera" name="carrera" value="${form.carrera()}" required>
        </div>
        <div class="form-group">
            <label for="semestre">Semestre</label>
            <input id="semestre" type="number" min="1" max="12" name="semestre" value="${form.semestre()}" placeholder="Numero 1-12" required></div>
        <div class="form-group">
            <label for="password">Contraseña</label>
            <input id="password" type="password" name="password" placeholder="Ingrese su contraseña" required></div>
        <div class="form-group">
            <label for="confirmacion">Confirmar contraseña</label>
            <input id="confirmacion" type="password" name="confirmacion" placeholder="Repita su contraseña" required></div>
            <button type="submit" class="btn-login">Registrarse</button>
        </form>
        <p><a href="/login">Volver al login</a></p>
    </div>
</div>
</body>
</html>
