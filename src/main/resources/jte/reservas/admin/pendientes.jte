@import cl.ucn.app.model.Reserva
@import java.time.format.DateTimeFormatter
@import java.util.List

@param String title
@param String usuarioNombre
@param List<Reserva> reservas
@param String mensaje
@param String error
@param boolean esAdmin

!{var formatoFechaHora = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");}

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <style>
        .alert-success { background: #e6f6ea; color: #1d6b32; border: 1px solid #b7e0c2; padding: 10px; border-radius: 8px; margin-bottom: 16px; }
        .tabla-admin { width: 100%; border-collapse: collapse; background: white; border-radius: 12px; overflow: hidden; }
        .tabla-admin th, .tabla-admin td { padding: 10px 14px; text-align: left; border-bottom: 1px solid #e2e7ef; vertical-align: top; }
        .tabla-admin th { background: #eef2f9; }
        .acciones-admin { display: flex; flex-direction: column; gap: 8px; }
        .form-rechazar { display: flex; gap: 6px; align-items: center; }
        .form-rechazar input[type="text"] { padding: 6px 8px; border: 1px solid #c9d2df; border-radius: 6px; min-width: 180px; }
        .btn-aprobar { padding: 6px 12px; border: none; border-radius: 8px; background: #1d6b32; color: white; cursor: pointer; }
        .btn-rechazar { padding: 6px 12px; border: none; border-radius: 8px; background: #b3261e; color: white; cursor: pointer; }
    </style>

    <section class="dashboard">
        <h1>Solicitudes de reserva pendientes</h1>
        @template.reservas.nav(esAdmin = esAdmin, activo = "admin")

        @if(!mensaje.isEmpty())
            <div class="alert-success">${mensaje}</div>
        @endif
        @if(!error.isEmpty())
            <div class="alert-error">${error}</div>
        @endif

        @if(reservas.isEmpty())
            <p>No hay solicitudes pendientes.</p>
        @else
            <table class="tabla-admin">
                <thead>
                <tr>
                    <th>Solicitante</th>
                    <th>Sala</th>
                    <th>Fecha</th>
                    <th>Horario</th>
                    <th>Motivo de la reserva</th>
                    <th>Fecha de creación</th>
                    <th>Acciones</th>
                </tr>
                </thead>
                <tbody>
                @for(Reserva reserva : reservas)
                    <tr>
                        <td>
                            <div>${reserva.getUsuario().getNombre()}</div>
                            <small>${reserva.getUsuario().getCorreo()}</small>
                        </td>
                        <td>${reserva.getEspacio().getNombre()}</td>
                        <td>${reserva.getFechaReserva().toString()}</td>
                        <td>${reserva.getHoraInicio().toString()} - ${reserva.getHoraFin().toString()}</td>
                        <td>${reserva.getMotivo() != null ? reserva.getMotivo() : ""}</td>
                        <td>${reserva.getFechaCreacion() != null ? reserva.getFechaCreacion().format(formatoFechaHora) : ""}</td>
                        <td>
                            <div class="acciones-admin">
                                <form method="post" action="/reservas/admin/${reserva.getId()}/aprobar">
                                    <button type="submit" class="btn-aprobar">Aprobar</button>
                                </form>
                                <form class="form-rechazar" method="post" action="/reservas/admin/${reserva.getId()}/rechazar">
                                    <input type="text" name="motivo" maxlength="255" required placeholder="Motivo del rechazo">
                                    <button type="submit" class="btn-rechazar">Rechazar</button>
                                </form>
                            </div>
                        </td>
                    </tr>
                @endfor
                </tbody>
            </table>
        @endif
    </section>
`
)
