@import java.util.List
@param List<cl.ucn.app.model.Recurso> recursos
@param String errorMsg = null

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inventario Laboratorio</title>
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; background-color: #f4f6f9; color: #333; }
        header { background-color: #003366; color: white; padding: 20px; text-align: center; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        main { padding: 30px; max-width: 1000px; margin: 0 auto; }
        .container { background: white; padding: 25px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
        .alert-error { background-color: #f8d7da; color: #721c24; padding: 15px; border-radius: 4px; margin-bottom: 20px; border: 1px solid #f5c6cb; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
        th { background-color: #003366; color: white; }
        tr:hover { background-color: #f1f5f9; }
        .badge { padding: 5px 10px; border-radius: 20px; font-size: 0.85em; font-weight: bold; }
        .insumo { background-color: #e2e8f0; color: #475569; }
        .equipo { background-color: #dbeafe; color: #1e40af; }
        footer { text-align: center; padding: 20px; font-size: 0.85em; color: #666; margin-top: 50px; }
    </style>
</head>
<body>

    <header>
        <h1>Sistema Integrado de Gestión Universitaria (SIGU)</h1>
        <p>Módulo de Inventario G5 - Universidad Católica del Norte</p>
    </header>

    <main>
        <div class="container">
            <h2>Consulta de Recursos Disponibles</h2>
            <p>Interfaz de visualización de Stock e Insumos.</p>
            
            @if(errorMsg != null)
                <div class="alert-error">
                    ${errorMsg}
                </div>
            @endif

            @if(recursos == null || recursos.isEmpty())
                <div class="alert-error">
                    No hay recursos disponibles en el inventario en este momento.
                </div>
            @else
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre del Recurso</th>
                            <th>Tipo</th>
                            <th>Stock Disponible</th>
                        </tr>
                    </thead>
                    <tbody>
                        @for(var recurso : recursos)
                            <tr>
                                <td>${recurso.getId()}</td>
                                <td>${recurso.getNombre()}</td>
                                <td>
                                    <span class="badge ${recurso.getTipo().toLowerCase().equals("insumo") ? "insumo" : "equipo"}">
                                        ${recurso.getTipo()}
                                    </span>
                                </td>
                                <td><strong>${recurso.getStock()}</strong></td>
                            </tr>
                        @endfor
                    </tbody>
                </table>
            @endif
        </div>
    </main>

    <footer>
        <p>&copy; 2026 Escuela de Ingeniería - UCN Coquimbo</p>
    </footer>

</body>
</html>
