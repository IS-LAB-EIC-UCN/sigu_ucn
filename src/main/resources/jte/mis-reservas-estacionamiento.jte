@param String title
@param String usuarioNombre
@param java.util.List<cl.ucn.app.model.Reserva> reservas

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`

    <section class="dashboard">

        <h1>Mis Reservas de Estacionamiento</h1>

        <p>
            <a href="/reservas/estacionamientos">Nueva reserva</a> |
            <a href="/estacionamientos/historial">Ver historial</a>
        </p>

        @if(reservas.isEmpty())

            <p>No tienes reservas registradas.</p>

        @else

            <table class="table">

                <thead>
                <tr>
                    <th>ID</th>
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

                            @if(reserva.getEstado().equals("PENDIENTE") || reserva.getEstado().equals("APROBADA"))

                                <a href="/mis-reservas/estacionamientos/cancelar/${reserva.getId()}">
                                    Cancelar
                                </a>

                            @elseif(reserva.getEstado().equals("ACTIVA"))

                                En uso

                            @elseif(reserva.getEstado().equals("FINALIZADA"))

                                Finalizada

                            @else

                                Sin acción

                            @endif

                        </td>

                    </tr>

                @endfor

                </tbody>

            </table>

        @endif

    </section>

`
)