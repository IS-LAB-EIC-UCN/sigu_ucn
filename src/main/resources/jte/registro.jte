@import java.util.List
@import java.util.Map

@param String nombre
@param String correo
@param String rolSeleccionado
@param List<String> roles
@param Map<String, String> errores
@param String errorGeneral

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Crear cuenta - SIGU-UCN</title>
    <link rel="stylesheet" href="/css/styles.css">
    <style>
        .registro-container { min-height: 100vh; display: flex; align-items: center; justify-content: center; padding: 24px; }
        .campo-error { color: #8f1d1d; font-size: 0.9rem; margin-top: 4px; }
        .form-group input.invalido, .form-group select.invalido { border-color: #d9534f; }
        .form-group select { width: 100%; padding: 10px 12px; border: 1px solid #c9d2df; border-radius: 8px; background: white; }
        .ayuda { color: #666; font-size: 0.85rem; margin-top: 4px; }
        .btn-registro { width: 100%; padding: 10px 16px; border: none; border-radius: 8px; background: #1f3b73; color: white; cursor: pointer; font-size: 1rem; }
        .enlace-secundario { text-align: center; margin-top: 16px; }
    </style>
</head>
<body>
<div class="registro-container">
    <div class="login-card">
        <h1>SIGU-UCN</h1>
        <h2>Crear cuenta</h2>

        @if(!errorGeneral.isBlank())
            <div class="alert-error">${errorGeneral}</div>
        @endif
        @if(!errores.isEmpty())
            <div class="alert-error">Revise los campos marcados para continuar.</div>
        @endif

        <form method="post" action="/registro" novalidate>
            <div class="form-group">
                <label for="nombre">Nombre completo</label>
                <input type="text" id="nombre" name="nombre" maxlength="100" value="${nombre}"
                       class="${errores.containsKey("nombre") ? "invalido" : ""}" required>
                @if(errores.containsKey("nombre"))
                    <div class="campo-error">${errores.get("nombre")}</div>
                @endif
            </div>

            <div class="form-group">
                <label for="correo">Correo institucional</label>
                <input type="email" id="correo" name="correo" maxlength="120" value="${correo}"
                       placeholder="nombre@alumnos.ucn.cl"
                       class="${errores.containsKey("correo") ? "invalido" : ""}" required>
                @if(errores.containsKey("correo"))
                    <div class="campo-error">${errores.get("correo")}</div>
                @endif
            </div>

            <div class="form-group">
                <label for="rol">Tipo de usuario</label>
                <select id="rol" name="rol" class="${errores.containsKey("rol") ? "invalido" : ""}">
                    @for(String rol : roles)
                        <option value="${rol}" selected="${rol.equals(rolSeleccionado)}">${rol.charAt(0) + rol.substring(1).toLowerCase()}</option>
                    @endfor
                </select>
                @if(errores.containsKey("rol"))
                    <div class="campo-error">${errores.get("rol")}</div>
                @endif
            </div>

            <div class="form-group">
                <label for="password">Contraseña</label>
                <input type="password" id="password" name="password" maxlength="64"
                       class="${errores.containsKey("password") ? "invalido" : ""}" required>
                <div class="ayuda">Mínimo 8 caracteres, con al menos una letra y un número.</div>
                @if(errores.containsKey("password"))
                    <div class="campo-error">${errores.get("password")}</div>
                @endif
            </div>

            <div class="form-group">
                <label for="confirmacion">Confirmar contraseña</label>
                <input type="password" id="confirmacion" name="confirmacion" maxlength="64"
                       class="${errores.containsKey("confirmacion") ? "invalido" : ""}" required>
                @if(errores.containsKey("confirmacion"))
                    <div class="campo-error">${errores.get("confirmacion")}</div>
                @endif
            </div>

            <button type="submit" class="btn-registro">Crear cuenta</button>
        </form>

        <p class="enlace-secundario">¿Ya tienes cuenta? <a href="/login">Inicia sesión</a></p>
    </div>
</div>
</body>
</html>
