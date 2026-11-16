@import cl.ucn.app.model.Espacio
@import java.util.List

@param String title
@param String usuarioNombre
@param List<Espacio> salas
@param String hoy
@param String error
@param String salaConError
@param String fecha
@param String horaInicio
@param String horaFin
@param String motivo

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <style>
        .tabla-salas { width: 100%; border-collapse: collapse; background: white; border-radius: 12px; overflow: hidden; }
        .tabla-salas th, .tabla-salas td { padding: 10px 14px; text-align: left; border-bottom: 1px solid #e2e7ef; vertical-align: top; }
        .tabla-salas th { background: #eef2f9; }
        .fila-error { background: #fff8f8; }
        .form-reserva { display: flex; flex-wrap: wrap; gap: 8px 12px; align-items: flex-end; margin-top: 8px; }
        .form-reserva label { display: flex; flex-direction: column; font-size: 0.85rem; font-weight: bold; gap: 4px; }
        .form-reserva input { padding: 6px 8px; border: 1px solid #c9d2df; border-radius: 6px; font-weight: normal; }
        .form-reserva input[name="motivo"] { min-width: 180px; }
        .btn-reservar { padding: 7px 14px; border: none; border-radius: 8px; background: #1f3b73; color: white; cursor: pointer; }
    </style>

    <section class="dashboard">
        <h1>Salas disponibles</h1>
        <p><a href="/reservas/mis-reservas">Mis reservas</a></p>

        @if(!error.isEmpty())
            <div class="alert-error">${error}</div>
        @endif

        <table class="tabla-salas">
            <thead>
            <tr>
                <th>Nombre</th>
                <th>Tipo</th>
                <th>Capacidad</th>
                <th>Ubicación</th>
                <th>Acciones</th>
            </tr>
            </thead>
            <tbody>
            @for(Espacio sala : salas)
                !{boolean conError = String.valueOf(sala.getId()).equals(salaConError);}
                <tr class="${conError ? "fila-error" : ""}">
                    <td>${sala.getNombre()}</td>
                    <td>${sala.getTipo()}</td>
                    <td>${sala.getCapacidad()}</td>
                    <td>${sala.getUbicacion() != null ? sala.getUbicacion() : "-"}</td>
                    <td>
                        <a href="/reservas/disponibilidad?sala=${sala.getId()}">Ver disponibilidad</a>
                        <form action="/reservas/crear" method="post" class="form-reserva">
                            <input type="hidden" name="espacioId" value="${sala.getId()}">
                            <label>Fecha
                                <input type="date" name="fecha" min="${hoy}" value="${conError ? fecha : ""}" required>
                            </label>
                            <label>Inicio
                                <input type="time" name="horaInicio" value="${conError ? horaInicio : ""}" required>
                            </label>
                            <label>Término
                                <input type="time" name="horaFin" value="${conError ? horaFin : ""}" required>
                            </label>
                            <label>Motivo
                                <input type="text" name="motivo" maxlength="255" value="${conError ? motivo : ""}" required>
                            </label>
                            <button type="submit" class="btn-reservar">Reservar</button>
                        </form>
                    </td>
                </tr>
            @endfor
            </tbody>
        </table>
    </section>
`
)
