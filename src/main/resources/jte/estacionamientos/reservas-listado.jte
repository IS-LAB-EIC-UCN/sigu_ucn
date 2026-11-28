@import java.time.format.DateTimeFormatter
@import java.util.List
@import cl.ucn.app.service.ReservaEstacionamientoService.ResumenReserva

@param List<ResumenReserva> reservas
@param boolean mostrarPropietario
@param String rutaCancelacion
@param String mensajeCancelacion
@param String errorCancelacion

!{var formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");}

@if(!mensajeCancelacion.isBlank())
    <div role="status" style="background:#e6f5ec; color:#22683e; padding:12px; border-radius:8px; margin-bottom:16px;">
        ${mensajeCancelacion}
    </div>
@endif
@if(!errorCancelacion.isBlank())
    <div class="alert-error" role="alert">${errorCancelacion}</div>
@endif

@if(reservas.isEmpty())
    <p>No hay reservas para mostrar.</p>
@else
    <table style="width:100%; text-align:left; border-spacing:8px;">
        <thead>
            <tr>
                <th scope="col">Estacionamiento</th>
                <th scope="col">Patente</th>
                @if(mostrarPropietario)
                    <th scope="col">Propietario</th>
                @endif
                <th scope="col">Inicio</th>
                <th scope="col">Término</th>
                <th scope="col">Estado</th>
                <th scope="col">Acción</th>
            </tr>
        </thead>
        <tbody>
            @for(ResumenReserva reserva : reservas)
                <tr>
                    <th scope="row">${reserva.codigoEstacionamiento()}</th>
                    <td>${reserva.patente()}</td>
                    @if(mostrarPropietario)
                        <td>${reserva.nombrePropietario()} (${reserva.correoPropietario()})</td>
                    @endif
                    <td>${reserva.inicio().format(formatoFecha)}</td>
                    <td>${reserva.fin().format(formatoFecha)}</td>
                    <td>${reserva.estado()}</td>
                    <td>
                        @if(reserva.puedeCancelar())
                            !{String accion = rutaCancelacion + "/" + reserva.id() + "/cancelar";}
                            <details>
                                <summary>Cancelar reserva</summary>
                                <p>¿Confirmas la cancelación de esta reserva?</p>
                                <form method="post" action="${accion}">
                                    <button type="submit" class="primary-btn">Confirmar cancelación</button>
                                </form>
                            </details>
                        @endif
                    </td>
                </tr>
            @endfor
        </tbody>
    </table>
@endif
