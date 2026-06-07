@import java.util.List
@import cl.ucn.app.model.biblioteca.Lector
@import cl.ucn.app.model.biblioteca.PrestamoLibro
@param String usuarioNombre
@param List<Lector> lectores
@param List<PrestamoLibro> historial
@param String lectorNombre

@template.layout.main(
title = "Historial de Préstamos - SIGU-UCN",
usuarioNombre = usuarioNombre,
content = @`
    <link rel="stylesheet" href="/css/biblioteca.css">

    <p><a href="/biblioteca/libros">Volver al catálogo</a></p>

    <h1>Historial de Préstamos</h1>

    <div class="info-card">
        <form method="get" action="/biblioteca/historial">
            <div class="form-group">
                <label>Lector</label>
                <div style="display:flex;gap:8px;">
                    <select name="lectorId" style="flex:1;">
                        @for(Lector l : lectores)
                        <option value="${l.getId()}">${l.getNombre()} (${l.getRut()})</option>
                        @endfor
                    </select>
                    <button type="submit" class="primary-btn" style="width:auto;">Ver historial</button>
                </div>
            </div>
        </form>
    </div>

    @if(historial != null && !historial.isEmpty())
        <h2>Historial de ${lectorNombre}</h2>
        <table class="data-table">
            <thead>
                <tr>
                    <th>Libro</th>
                    <th>Fecha inicio</th>
                    <th>Fecha vencimiento</th>
                    <th>Fecha devolución</th>
                    <th>Estado</th>
                </tr>
            </thead>
            <tbody>
                @for(PrestamoLibro p : historial)
                <tr>
                    <td>${p.getEjemplar().getLibro().getTitulo()}</td>
                    <td>${p.getFechaInicio().toString()}</td>
                    <td>${p.getFechaVencimiento().toString()}</td>
                    <td>${p.getFechaDevolucion() != null ? p.getFechaDevolucion().toString() : "No devuelto"}</td>
                    <td>${p.getEstado()}</td>
                </tr>
                @endfor
            </tbody>
        </table>
    @elseif(lectorNombre != null && !lectorNombre.isEmpty())
        <p>Este lector no tiene préstamos registrados.</p>
    @endif
`
)
