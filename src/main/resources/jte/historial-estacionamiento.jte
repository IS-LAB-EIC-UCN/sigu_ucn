@param String title
@param String usuarioNombre
@param String usuarioRol
@param java.util.List<cl.ucn.app.model.RegistroEstacionamiento> registros
@param String usuarioFiltro
@param String patenteFiltro
@param String fechaFiltro

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`

    <section class="dashboard">

        <h1>Historial de Estacionamientos</h1>

        <p>
            <a href="/mis-reservas/estacionamientos">Mis reservas</a>
            @if(usuarioRol.equals("ADMIN"))
                | <a href="/estacionamientos/reservas-pendientes">Reservas pendientes</a>
            @endif
        </p>

        <form method="get" action="/estacionamientos/historial/filtrar" class="filter-form">

            <div>
                <label>Usuario</label>
                <input type="text" name="usuario" value="${usuarioFiltro != null ? usuarioFiltro : ""}">
            </div>

            <div>
                <label>Patente</label>
                <input type="text" name="patente" value="${patenteFiltro != null ? patenteFiltro : ""}">
            </div>

            <div>
                <label>Fecha</label>
                <input type="date" name="fecha" value="${fechaFiltro != null ? fechaFiltro : ""}">
            </div>

            <button type="submit">Filtrar</button>

            <a href="/estacionamientos/historial">Limpiar</a>

        </form>

        @if(registros.isEmpty())

            <p>No existen registros.</p>

        @else

            <table class="table">

                <thead>
                <tr>
                    <th>ID</th>
                    <th>Reserva</th>
                    <th>Usuario</th>
                    <th>Espacio</th>
                    <th>Vehículo</th>
                    <th>Ingreso</th>
                    <th>Salida</th>
                    <th>Tiempo</th>
                    <th>Estado</th>
                    <th>Acción</th>
                </tr>
                </thead>

                <tbody>

                @for(var registro : registros)

                    <tr>
                        <td>${registro.getId()}</td>
                        <td>${registro.getReserva().getId()}</td>
                        <td>${registro.getReserva().getUsuario().getNombre()}</td>
                        <td>${registro.getReserva().getEspacio().getNombre()}</td>

                        <td>
                            @if(registro.getReserva().getVehiculo() != null)
                                ${registro.getReserva().getVehiculo().getPatente()}
                            @else
                                Sin vehículo
                            @endif
                        </td>

                        <td>${registro.getFechaHoraIngreso().toString()}</td>

                        <td>
                            @if(registro.getFechaHoraSalida() != null)
                                ${registro.getFechaHoraSalida().toString()}
                            @else
                                Pendiente
                            @endif
                        </td>

                        <td>
                            @if(registro.getTiempoUsoMinutos() != null)
                                ${registro.getTiempoUsoMinutos()} minutos
                            @else
                                En uso
                            @endif
                        </td>

                        <td>${registro.getEstado()}</td>

                        <td>
                            @if(registro.getEstado().equals("EN_USO"))
                                <form method="post"
                                      action="/estacionamientos/salida/${registro.getId()}"
                                      style="display:inline;">
                                    <button type="submit">
                                        Registrar salida
                                    </button>
                                </form>
                            @else
                                Finalizado
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