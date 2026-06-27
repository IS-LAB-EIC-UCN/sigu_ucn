@param String title
@param String usuarioNombre
@param String puestoNumero
@param java.util.List<cl.ucn.app.model.Espacio> espacios
@param java.util.List<cl.ucn.app.model.Vehiculo> vehiculos

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <section class="dashboard">

        <h1>Confirmar Reserva de Estacionamiento</h1>

        <p>Confirme el estacionamiento seleccionado, el puesto, el horario y el vehículo que utilizará.</p>

        @if(espacios.isEmpty())
            <p>No hay espacios disponibles para reservar.</p>
            <p>
                <a href="/estacionamientos">Volver al mapa de estacionamientos</a>
            </p>
        @else
            @if(vehiculos.isEmpty())
                <p>No tienes vehículos registrados. Debes registrar un vehículo antes de reservar.</p>
                <p>
                    <a href="/estacionamientos">Volver al mapa de estacionamientos</a>
                </p>
            @else
                <form method="post" action="/reservas/estacionamientos" class="form">

                    <div class="form-group">
                        <label for="espacioId">Estacionamiento seleccionado</label>
                        <select id="espacioId" name="espacioId" required>
                            @for(var espacio : espacios)
                                <option value="${espacio.getId()}">
                                    ${espacio.getNombre()} - ${espacio.getTipo()}
                                </option>
                            @endfor
                        </select>
                    </div>

                    <div class="form-group">
                        <label>Puesto seleccionado</label>

                        @if(puestoNumero != null && !puestoNumero.isBlank())
                            <p><strong>Puesto N° ${puestoNumero}</strong></p>
                            <input type="hidden" name="puestoNumero" value="${puestoNumero}">
                        @else
                            <p>No se seleccionó un puesto. Vuelva al mapa de estacionamientos.</p>
                        @endif
                    </div>

                    <div class="form-group">
                        <label for="fechaReserva">Fecha de reserva</label>
                        <input
                                type="date"
                                id="fechaReserva"
                                name="fechaReserva"
                                required>
                    </div>

                    <div class="form-group">
                        <label for="horaInicio">Hora de inicio</label>
                        <input
                                type="time"
                                id="horaInicio"
                                name="horaInicio"
                                required>
                    </div>

                    <div class="form-group">
                        <label for="horaFin">Hora de término</label>
                        <input
                                type="time"
                                id="horaFin"
                                name="horaFin"
                                required>
                    </div>

                    <div class="form-group">
                        <label for="vehiculoId">Vehículo</label>
                        <select id="vehiculoId" name="vehiculoId" required>
                            @for(var vehiculo : vehiculos)
                                <option value="${vehiculo.getId()}">
                                    ${vehiculo.getPatente()} - ${vehiculo.getMarca()} ${vehiculo.getModelo()}
                                </option>
                            @endfor
                        </select>
                    </div>

                    @if(puestoNumero != null && !puestoNumero.isBlank())
                        <button type="submit">Confirmar reserva</button>
                    @endif

                </form>

                <p>
                    <a href="/estacionamientos">Cambiar estacionamiento o puesto</a>
                </p>
            @endif
        @endif

        <p>
            <a href="/mis-reservas/estacionamientos">Ver mis reservas</a>
        </p>

    </section>
`
)