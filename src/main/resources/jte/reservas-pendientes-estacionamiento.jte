@param String title
@param String usuarioNombre
@param String usuarioRol
@param java.util.List<cl.ucn.app.model.Reserva> reservas

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`

    <section class="dashboard">

        <h1>Reservas Pendientes de Estacionamiento</h1>

        <p>
            <a href="/estacionamientos">Volver a estacionamientos</a>
        </p>

        @if(reservas.isEmpty())

            <p>No existen reservas pendientes.</p>

        @else

            <table class="table">

                <thead>
                <tr>
                    <th>ID</th>
                    <th>Usuario</th>
                    <th>Espacio</th>
                    <th>Puesto</th>
                    <th>Vehículo</th>
                    <th>Fecha</th>
                    <th>Hora inicio</th>
                    <th>Hora fin</th>
                    <th>Estado</th>
                    <th>Acciones</th>
                </tr>
                </thead>

                <tbody>

                @for(var reserva : reservas)

                    <tr>

                        <td>${reserva.getId()}</td>

                        <td>${reserva.getUsuario().getNombre()}</td>

                        <td>${reserva.getEspacio().getNombre()}</td>

                        <td>${reserva.getPuestoNumero()}</td>

                        <td>
                            @if(reserva.getVehiculo() != null)
                                ${reserva.getVehiculo().getPatente()}
                            @else
                                Sin vehículo
                            @endif
                        </td>

                        <td>${reserva.getFechaReserva().toString()}</td>
                        <td>${reserva.getHoraInicio().toString()}</td>
                        <td>${reserva.getHoraFin().toString()}</td>

                        <td>${reserva.getEstado()}</td>

                        <td>
                            <form method="post"
                                  action="/estacionamientos/reservas-pendientes/aprobar/${reserva.getId()}"
                                  style="display:inline;">
                                <button type="submit">Aprobar</button>
                            </form>

                            <form method="post"
                                  action="/estacionamientos/reservas-pendientes/rechazar/${reserva.getId()}"
                                  style="display:inline;">
                                <button type="submit">Rechazar</button>
                            </form>
                        </td>

                    </tr>

                @endfor

                </tbody>

            </table>

        @endif

    </section>

`
)