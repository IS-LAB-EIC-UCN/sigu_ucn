@import cl.ucn.app.model.BloqueHorario
@import cl.ucn.app.model.Espacio
@import cl.ucn.app.service.Disponibilidad
@import java.time.LocalDate
@import java.util.List

@param String title
@param String usuarioNombre
@param List<Espacio> salas
@param Long salaId
@param LocalDate fecha
@param Disponibilidad disponibilidad
@param String error

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <style>
        .filtros { display: flex; flex-wrap: wrap; gap: 16px; align-items: flex-end; background: white; padding: 16px; border-radius: 12px; margin: 16px 0; }
        .filtros .form-group { margin-bottom: 0; }
        .filtros select, .filtros input { padding: 10px 12px; border: 1px solid #c9d2df; border-radius: 8px; }
        .btn-consultar { padding: 10px 16px; border: none; border-radius: 8px; background: #1f3b73; color: white; cursor: pointer; }
        .tabla-disponibilidad { width: 100%; border-collapse: collapse; background: white; border-radius: 12px; overflow: hidden; margin-bottom: 24px; }
        .tabla-disponibilidad th, .tabla-disponibilidad td { padding: 10px 14px; text-align: left; border-bottom: 1px solid #e2e7ef; }
        .tabla-disponibilidad th { background: #eef2f9; }
        .badge { padding: 3px 10px; border-radius: 12px; font-size: 0.85rem; font-weight: bold; }
        .estado-pendiente { background: #fff3cd; color: #7a5b00; }
        .estado-aprobada { background: #d4edda; color: #1d6b32; }
        .estado-libre { background: #e6f6ea; color: #1d6b32; }
    </style>

    <section class="dashboard">
        <h1>Disponibilidad de sala</h1>
        <p>
            <a href="/reservas">Ver salas disponibles</a> |
            <a href="/reservas/mis-reservas">Mis reservas</a>
        </p>

        @if(error != null && !error.isEmpty())
            <div class="alert-error">${error}</div>
        @endif

        <form class="filtros" method="get" action="/reservas/disponibilidad">
            <div class="form-group">
                <label for="sala">Sala</label>
                <select id="sala" name="sala">
                    <option value="">Seleccione una sala</option>
                    @for(Espacio sala : salas)
                        <option value="${sala.getId()}" selected="${sala.getId().equals(salaId)}">${sala.getNombre()}</option>
                    @endfor
                </select>
            </div>

            <div class="form-group">
                <label for="fecha">Fecha</label>
                <input type="date" id="fecha" name="fecha" value="${fecha != null ? fecha.toString() : ""}">
            </div>

            <button type="submit" class="btn-consultar">Consultar</button>
        </form>

        @if(disponibilidad != null)
            <h2>${disponibilidad.espacio().getNombre()} — ${disponibilidad.fecha().toString()}</h2>

            <h3>Bloques ocupados</h3>
            @if(disponibilidad.ocupados().isEmpty())
                <p>La sala está libre durante todo el día.</p>
            @else
                <table class="tabla-disponibilidad">
                    <thead>
                    <tr>
                        <th>Inicio</th>
                        <th>Término</th>
                        <th>Estado</th>
                    </tr>
                    </thead>
                    <tbody>
                    @for(BloqueHorario bloque : disponibilidad.ocupados())
                        <tr>
                            <td>${bloque.inicio().toString()}</td>
                            <td>${bloque.fin().toString()}</td>
                            <td><span class="badge estado-${bloque.estado().name().toLowerCase()}">${bloque.estado()}</span></td>
                        </tr>
                    @endfor
                    </tbody>
                </table>
            @endif

            <h3>Bloques libres</h3>
            @if(disponibilidad.libres().isEmpty())
                <p>No hay bloques libres disponibles para esta fecha.</p>
            @else
                <table class="tabla-disponibilidad">
                    <thead>
                    <tr>
                        <th>Inicio</th>
                        <th>Término</th>
                    </tr>
                    </thead>
                    <tbody>
                    @for(BloqueHorario bloque : disponibilidad.libres())
                        <tr>
                            <td>${bloque.inicio().toString()}</td>
                            <td>${bloque.fin().toString()}</td>
                        </tr>
                    @endfor
                    </tbody>
                </table>
            @endif
        @endif
    </section>
`
)
