@param String title
@param String usuarioNombre
@param String rol

@template.layout.main(
    title = title,
    usuarioNombre = usuarioNombre,
    content = @`
        <section style="padding: 20px; font-family: sans-serif;">
            <h1>Módulo de Talleres (Grupo 3)</h1>
            <p>Usuario: <strong>${usuarioNombre}</strong></p>
            <p>Rol detectado: <strong style="color: blue;">${rol}</strong></p>

            <hr style="margin: 20px 0; border: 0; border-top: 1px solid #ccc;">

            @if(rol != null && "ADMIN".equalsIgnoreCase(rol.trim()))
                <div style="background-color: #ffebe9; padding: 20px; border-left: 5px solid red; margin-bottom: 20px; border-radius: 4px;">
                    <h2>[VISTA ADMIN] Registrar Nuevo Taller</h2>

                    <form action="/talleres" method="POST" style="display: flex; flex-direction: column; gap: 10px; max-width: 400px;">
                        <input type="text" name="nombreTaller" placeholder="Nombre del Taller (ej: Electrotecnia Avanzada)" required style="padding: 8px; border: 1px solid #ccc; border-radius: 4px;">
                        <input type="number" name="cupos" placeholder="Cantidad de Cupos (ej: 25)" required min="1" style="padding: 8px; border: 1px solid #ccc; border-radius: 4px;">
                        <button type="submit" style="background-color: red; color: white; padding: 10px; border: none; border-radius: 4px; cursor: pointer; font-weight: bold;">
                            Guardar Taller
                        </button>
                    </form>
                </div>
            @elseif(rol != null && "ESTUDIANTE".equalsIgnoreCase(rol.trim()))
                <div style="background-color: #e6f6ff; padding: 15px; border-left: 5px solid blue; border-radius: 4px;">
                    <h2>[VISTA ESTUDIANTE]</h2>
                    <p>Aquí va el catálogo de talleres disponibles para inscribirse.</p>
                </div>

            @else
                <div style="background-color: #e6f6ff; padding: 15px; border-left: 5px solid blue; border-radius: 4px;">
                    <h2>[Vista General]</h2>
                </div>
            @endif
        </section>
    `
)