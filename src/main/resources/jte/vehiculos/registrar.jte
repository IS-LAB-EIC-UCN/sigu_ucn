@import java.util.List
@import cl.ucn.app.model.Vehiculo
@import cl.ucn.app.model.Estacionamiento

@param String title
@param String usuarioNombre
@param boolean mostrarAccesoAdmin
@param String patente
@param String error
@param String mensaje
@param List<Vehiculo> vehiculos
@param List<Estacionamiento> estacionamientos
@param String reservaMensaje
@param String reservaError


@template.layout.main(
    title = title,
    usuarioNombre = usuarioNombre,
    content = @`
        <section class="dashboard">

            <h1>Gestión de vehículos</h1>

            <p>
                Registra vehículos y consulta los que están
                asociados a tu cuenta universitaria.
            </p>

            @if(mostrarAccesoAdmin)
                <p><a href="/estacionamientos/admin">Administrar espacios de estacionamiento</a></p>
            @endif

            <div style="
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
                gap: 24px;
                align-items: start;
            ">

                <div class="info-card">

                    <h2>Registrar vehículo (HU-01)</h2>

                    <p>Ingresa la patente del vehículo que deseas asociar a tu cuenta.</p>

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
                                style="background:#1f3b73; color:white;">
                            Registrar vehículo
                        </button>

                    </form>

                </div>

                <div class="info-card">

                    <h2>Mis vehículos</h2>

                    <p>
                        Vehículos registrados en tu cuenta.
                    </p>

                    <div style="
                        background:#eef2f8;
                        padding:12px;
                        border-radius:8px;
                        margin-bottom:16px;
                    ">
                        <strong>Total de vehículos:</strong>
                        ${vehiculos.size()}
                    </div>

                    @if(vehiculos.isEmpty())

                        <div style="
                            text-align:center;
                            padding:24px;
                            color:#666;
                        ">
                            <p>No tienes vehículos registrados todavía.</p>
                            <p>Utiliza el formulario para agregar tu primer vehículo.</p>
                        </div>

                    @else

                        @for(Vehiculo vehiculo : vehiculos)

                            <div style="
                                border:1px solid #ddd;
                                border-radius:8px;
                                padding:16px;
                                margin-bottom:12px;
                            ">

                                <div style="
                                    display:flex;
                                    justify-content:space-between;
                                    align-items:center;
                                    gap:12px;
                                ">
                                    <div>
                                        <strong>Vehículo</strong>
                                        <h3 style="margin:6px 0;">
                                            ${vehiculo.getPatente()}
                                        </h3>
                                        <small style="color:#666;">
                                            Asociado a tu cuenta
                                        </small>
                                    </div>

                                    <span style="font-size:28px;">
                                        🚗
                                    </span>
                                </div>

                            </div>

                        @endfor

                    @endif

                </div>

            </div>


            <section class="info-card" style="margin-top:24px;">

                <h2>Reservar estacionamiento (HU-02)</h2>

                <p>Selecciona tu vehículo, un estacionamiento disponible y el horario de tu reserva.</p>

                @if(reservaMensaje != null && !reservaMensaje.isBlank())
                    <div role="status"
                        style="background:#e6f5ec; color:#22683e; padding:12px; border-radius:8px; margin-bottom:16px;">
                        ${reservaMensaje}
                    </div>
                @endif

                @if(reservaError != null && !reservaError.isBlank())
                    <div class="alert-error" role="alert">
                        ${reservaError}
                    </div>
                @endif

                @if(vehiculos.isEmpty())
                    <p>Primero debes registrar un vehículo para poder reservar.</p>
                @elseif(estacionamientos.isEmpty())
                    <p>No hay estacionamientos disponibles para tu tipo de usuario.</p>
                @else

                    <form method="post" action="/estacionamientos/reservar">

                        <div class="form-group">
                            <label for="vehiculoId">Mi vehículo</label>
                            <select id="vehiculoId" name="vehiculoId" required>
                                <option value="">Selecciona tu vehículo</option>
                                @for(Vehiculo vehiculo : vehiculos)
                                    <option value="${vehiculo.getId()}">
                                        ${vehiculo.getPatente()}
                                    </option>
                                @endfor
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="estacionamientoId">Estacionamiento</label>
                            <select id="estacionamientoId" name="estacionamientoId" required>
                                <option value="">Selecciona un estacionamiento</option>
                                @for(Estacionamiento estacionamiento : estacionamientos)
                                    <option value="${estacionamiento.getId()}">
                                        ${estacionamiento.getCodigo()} - ${estacionamiento.getTipo()}
                                    </option>
                                @endfor
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="fechaHoraInicio">Fecha y hora de inicio</label>
                            <input type="datetime-local"
                                id="fechaHoraInicio"
                                name="fechaHoraInicio"
                                required>
                        </div>

                        <div class="form-group">
                            <label for="fechaHoraFin">Fecha y hora de término</label>
                            <input type="datetime-local"
                                id="fechaHoraFin"
                                name="fechaHoraFin"
                                required>
                        </div>

                        <button type="submit" class="primary-btn">
                            Solicitar reserva
                        </button>

                    </form>
                @endif

            </section>


        </section>
    `
)
