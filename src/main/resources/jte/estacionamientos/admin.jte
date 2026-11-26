@import java.util.List
@import cl.ucn.app.model.Estacionamiento

@param String usuarioNombre
@param List<Estacionamiento> estacionamientos
@param String mensaje
@param String error

@template.layout.main(
    title = "Administrar estacionamientos",
    usuarioNombre = usuarioNombre,
    content = @`
        <section class="dashboard">
            <h1>Administrar espacios de estacionamiento</h1>
            <p><a href="/estacionamientos/vehiculos/nuevo">Volver a mis vehículos y reservas</a></p>

            @if(!mensaje.isBlank())
                <div role="status" style="background:#e6f5ec; color:#22683e; padding:12px; border-radius:8px; margin-bottom:16px;">
                    ${mensaje}
                </div>
            @endif
            @if(!error.isBlank())
                <div class="alert-error" role="alert">${error}</div>
            @endif

            <section class="info-card">
                <h2>Registrar espacio</h2>
                <form method="post" action="/estacionamientos/admin">
                    <div class="form-group">
                        <label for="codigo">Código</label>
                        <input type="text" id="codigo" name="codigo" placeholder="Ej. C01"
                               aria-describedby="ayuda-codigo" required>
                        <small id="ayuda-codigo">
                            Usa entre 2 y 10 letras o números. Se recomiendan C01, C02 para espacios comunes y D01, D02 para docentes.
                        </small>
                    </div>
                    <div class="form-group">
                        <label for="tipo">Tipo</label>
                        <select id="tipo" name="tipo" required>
                            <option value="">Selecciona un tipo</option>
                            <option value="COMUN">Común</option>
                            <option value="DOCENTES">Docentes</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="habilitado">Estado inicial</label>
                        <select id="habilitado" name="habilitado" required>
                            <option value="">Selecciona un estado</option>
                            <option value="true">Habilitado</option>
                            <option value="false">Deshabilitado</option>
                        </select>
                    </div>
                    <button type="submit" class="primary-btn">Registrar espacio</button>
                </form>
            </section>

            <section class="info-card" style="margin-top:24px; overflow-x:auto;">
                <h2>Espacios registrados</h2>
                @if(estacionamientos.isEmpty())
                    <p>Todavía no hay espacios registrados.</p>
                @else
                    <table style="width:100%; text-align:left; border-spacing:8px;">
                        <thead>
                            <tr>
                                <th scope="col">Código</th>
                                <th scope="col">Tipo</th>
                                <th scope="col">Estado</th>
                                <th scope="col">Acción</th>
                            </tr>
                        </thead>
                        <tbody>
                            @for(Estacionamiento espacio : estacionamientos)
                                <tr>
                                    <th scope="row">${espacio.getCodigo()}</th>
                                    <td>${espacio.getTipo()}</td>
                                    <td>${espacio.getHabilitado() ? "Habilitado" : "Deshabilitado"}</td>
                                    <td>
                                        <form method="post" action="/estacionamientos/admin/${espacio.getId()}/habilitacion">
                                            <input type="hidden" name="habilitado" value="${espacio.getHabilitado() ? "false" : "true"}">
                                            <button type="submit" class="primary-btn">
                                                ${espacio.getHabilitado() ? "Deshabilitar" : "Habilitar"}
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            @endfor
                        </tbody>
                    </table>
                @endif
            </section>
        </section>
    `
)
