
@param String title
@param String usuarioNombre
@param String patente
@param String error
@param String mensaje

@template.layout.main(
    title = title,
    usuarioNombre = usuarioNombre,
    content = @`
        <section class="dashboard">

            <h1>Registrar vehículo</h1>

            <p>
                Registra un vehículo asociado a tu
                cuenta universitaria.
            </p>

            <div class="info-card">

                @if(mensaje != null && !mensaje.isBlank())
                    <div role="status"
                         style="background:#e6f5ec;
                                color:#22683e;
                                padding:12px;
                                border-radius:8px;
                                margin-bottom:16px;">
                        ${mensaje}
                    </div>
                @endif

                @if(error != null && !error.isBlank())
                    <div class="alert-error" role="alert">
                        ${error}
                    </div>
                @endif

                <form method="post"
                      action="/estacionamientos/vehiculos">

                    <div class="form-group">
                        <label for="patente">
                            Patente del vehículo
                        </label>

                        <input
                            type="text"
                            id="patente"
                            name="patente"
                            value="${patente}"
                            placeholder="Ej: ABCD12"
                            maxlength="6"
                            required>
                    </div>

                    <button type="submit"
                            class="primary-btn"
                            style="background:#1f3b73;
                                   color:white;">
                        Registrar vehículo
                    </button>

                </form>
            </div>

        </section>
    `
)
