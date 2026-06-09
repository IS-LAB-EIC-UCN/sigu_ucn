@param String title
@param String usuarioNombre
@param java.util.List<cl.ucn.app.model.Espacio> espacios
@param java.util.List<cl.ucn.app.model.Vehiculo> vehiculos

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <section class="dashboard">

        <h1>Reservar Estacionamiento</h1>

        <p>Seleccione un espacio disponible y el vehículo que utilizará.</p>

        @if(espacios.isEmpty())
            <p>No hay espacios disponibles para reservar.</p>
        @else
            @if(vehiculos.isEmpty())
                <p>No tienes vehículos registrados. Debes registrar un vehículo antes de reservar.</p>
            @else
                <form method="post" action="/reservas/estacionamientos" class="form">

                    <div class="form-group">
                        <label for="espacioId">Espacio disponible</label>
                        <select id="espacioId" name="espacioId" required>
                            @for(var espacio : espacios)
                                <option value="${espacio.getId()}">
                                    ${espacio.getNombre()} - ${espacio.getTipo()}
                                </option>
                            @endfor
                        </select>
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

                    <button type="submit">Reservar</button>

                </form>
            @endif
        @endif

        <p>
            <a href="/mis-reservas/estacionamientos">Ver mis reservas</a>
        </p>

    </section>
`
)