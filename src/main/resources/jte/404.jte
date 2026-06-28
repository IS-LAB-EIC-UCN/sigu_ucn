@param String title
@param String usuarioNombre

@template.layout.main(
    title = title,
    usuarioNombre = usuarioNombre,
    content = @`
    <style>
        .error-container {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            min-height: 70vh;
            text-align: center;
            font-family: Arial, sans-serif;
            color: #334155;
        }

        .error-code {
            font-size: 8rem;
            font-weight: 800;
            color: #e2e8f0;
            margin: 0;
            line-height: 1;
        }

        .error-title {
            font-size: 2rem;
            font-weight: 700;
            margin-top: -1rem;
            margin-bottom: 1rem;
            color: #0f172a;
        }

        .error-desc {
            font-size: 1.1rem;
            color: #64748b;
            max-width: 500px;
            margin-bottom: 2rem;
        }

        .btn-home {
            display: inline-block;
            padding: 0.8rem 1.5rem;
            background-color: #3b82f6;
            color: white;
            text-decoration: none;
            border-radius: 8px;
            font-weight: 600;
            transition: background-color 0.2s;
        }

        .btn-home:hover {
            background-color: #2563eb;
        }
    </style>

    <div class="error-container">
        <h1 class="error-code">404</h1>
        <h2 class="error-title">Página no encontrada</h2>
        <p class="error-desc">Lo sentimos, la página que estás buscando no existe, ha sido movida o te has equivocado al escribir la dirección.</p>
        <a href="/home" class="btn-home">Volver al Inicio</a>
    </div>
    `
)
