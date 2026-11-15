@import cl.ucn.app.model.Espacio
@import cl.ucn.app.model.Reserva
@import java.util.List
@import java.util.Set

@param String title
@param String usuarioNombre
@param List<Reserva> reservas
@param Set<Long> idsCancelables
@param List<Espacio> salas
@param List<String> estados
@param String salaSeleccionada
@param String fechaSeleccionada
@param String estadoSeleccionado
@param String mensaje
@param String error

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <style>
        .alert-success { background: #e6f6ea; color: #1d6b32; border: 1px solid #b7e0c2; padding: 10px; border-radius: 8px; margin-bottom: 16px; }
        .filtros { display: flex; flex-wrap: wrap; gap: 16px; align-items: flex-end; background: white; padding: 16px; border-radius: 12px; margin: 16px 0; }
        .filtros .form-group { margin-bottom: 0; }
        .filtros select, .filtros input { padding: 10px 12px; border: 1px solid #c9d2df; border-radius: 8px; }
        .tabla-reservas { width: 100%; border-collapse: collapse; background: white; border-radius: 12px; overflow: hidden; }
        .tabla-reservas th, .tabla-reservas td { padding: 10px 14px; text-align: left; border-bottom: 1px solid #e2e7ef; }
        .tabla-reservas th { background: #eef2f9; }
        .badge { padding: 3px 10px; border-radius: 12px; font-size: 0.85rem; font-weight: bold; }
        .estado-pendiente { background: #fff3cd; color: #7a5b00; }
        .estado-aprobada { background: #d4edda; color: #1d6b32; }
        .estado-rechazada { background: #f8d7da; color: #8f1d1d; }
        .estado-cancelada { background: #e2e3e5; color: #444; }
        .btn-filtrar { padding: 10px 16px; border: none; border-radius: 8px; background: #1f3b73; color: white; cursor: pointer; }
        .btn-cancelar { padding: 6px 12px; border: none; border-radius: 8px; background: #b3261e; color: white; cursor: pointer; }
    </style>

    <section class="dashboard">
        <h1>Mis reservas</h1>
        <p><a href="/reservas">Ver salas disponibles</a></p>

        @if(!mensaje.isEmpty())
            <div class="alert-success">${mensaje}</div>
        @endif
        @if(!error.isEmpty())
            <div class="alert-error">${error}</div>
        @endif

        <form class="filtros" method="get" action="/reservas/mis-reservas">
            <div class="form-group">
                <label for="sala">Sala</label>
                <select id="sala" name="sala">
                    <option value="">Todas</option>
                    @for(Espacio sala : salas)
                        <option value="${sala.getId()}" selected="${String.valueOf(sala.getId()).equals(salaSeleccionada)}">${sala.getNombre()}</option>
                    @endfor
                </select>
            </div>

            <div class="form-group">
                <label for="fecha">Fecha</label>
                <input type="date" id="fecha" name="fecha" value="${fechaSeleccionada}">
            </div>

            <div class="form-group">
                <label for="estado">Estado</label>
                <select id="estado" name="estado">
                    <option value="">Todos</option>
                    @for(String estado : estados)
                        <option value="${estado}" selected="${estado.equals(estadoSeleccionado)}">${estado}</option>
                    @endfor
                </select>
            </div>

            <button type="submit" class="btn-filtrar">Filtrar</button>
            <a href="/reservas/mis-reservas">Limpiar</a>
        </form>

        @if(reservas.isEmpty())
            <p>No se encontraron reservas con los filtros indicados.</p>
        @else
            <table class="tabla-reservas">
                <thead>
                <tr>
                    <th>Sala</th>
                    <th>Fecha</th>
                    <th>Inicio</th>
                    <th>Término</th>
                    <th>Estado</th>
                    <th>Acción</th>
                </tr>
                </thead>
                <tbody>
                @for(Reserva reserva : reservas)
                    <tr>
                        <td>${reserva.getEspacio().getNombre()}</td>
                        <td>${reserva.getFechaReserva().toString()}</td>
                        <td>${reserva.getHoraInicio().toString()}</td>
                        <td>${reserva.getHoraFin().toString()}</td>
                        <td><span class="badge estado-${reserva.getEstado().toLowerCase()}">${reserva.getEstado()}</span></td>
                        <td>
                            @if(idsCancelables.contains(reserva.getId()))
                                <form method="post" action="/reservas/${reserva.getId()}/cancelar" onsubmit="return confirm('¿Desea cancelar esta reserva?');">
                                    <button type="submit" class="btn-cancelar">Cancelar</button>
                                </form>
                            @else
                                —
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