@import gg.jte.Content

@param String title
@param String usuarioNombre
@param Content content

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
    <link rel="stylesheet" href="/css/styles.css">
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script src="/js/app.js"></script>
</head>
<body>
<header class="topbar">
    <div class="brand">SIGU-UCN</div>
    <div class="topbar-right">
        @if(usuarioNombre != null)
            <span class="user-label">Bienvenido, ${usuarioNombre}</span>
            <a class="logout-btn" href="/logout">Cerrar sesión</a>
        @endif
    </div>
</header>

<div class="app-container">
    @if(usuarioNombre != null)
        <aside class="sidebar">
            <h3>Módulos</h3>
            <ul class="menu-list">
                <li><a href="/home">Inicio</a></li>
                <li><a href="#">Grupo 1 - Reservas de salas</a></li>
                <li><a href="#">Grupo 2 - Biblioteca</a></li>
                <li><a href="#">Grupo 3 - Talleres</a></li>
                <li><a href="#">Grupo 4 - Soporte</a></li>
                <li><a href="#">Grupo 5 - Inventario</a></li>
                <li><a href="#">Grupo 6 - Eventos</a></li>
                <li><a href="#">Grupo 7 - Cafetería</a></li>
                <li><a href="/tutorias">Grupo 8 - Tutorias</a></li>
                <li><a href="#">Grupo 9 - Estacionamientos</a></li>
            </ul>
        </aside>
    @endif

    <main class="content-area">
        ${content}
    </main>
</div>
</body>
</html>