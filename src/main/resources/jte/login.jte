@param String error
@param String mensaje = ""

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Iniciar sesión</title>
    <link rel="stylesheet" href="/css/styles.css">
</head>
<body>
<div class="login-container">
    <div class="login-card">
        <h1>SIGU-UCN</h1>
        <h2>Iniciar sesión</h2>

        @if(mensaje != null && !mensaje.isBlank())
            <div class="alert-success" style="background:#e6f4ea;color:#1d6b32;border:1px solid #b7dfc2;padding:10px;border-radius:8px;margin-bottom:16px;">
                ${mensaje}
            </div>
        @endif

        @if(error != null && !error.isBlank())
            <div class="error-message">
                ${error}
            </div>
        @endif

        <form method="post" action="/login" class="login-form">
            <div class="form-group">
                <label for="correo">Correo</label>
                <input
                        type="email"
                        id="correo"
                        name="correo"
                        placeholder="Ingrese su correo"
                        required>
            </div>

            <div class="form-group">
                <label for="password">Contraseña</label>
                <input
                        type="password"
                        id="password"
                        name="password"
                        placeholder="Ingrese su contraseña"
                        required>
            </div>

            <button type="submit" class="btn-login">Ingresar</button>
        </form>

        <p style="text-align:center;margin-top:16px;">¿No tienes cuenta? <a href="/registro">Crear cuenta</a></p>
    </div>
</div>
</body>
</html>