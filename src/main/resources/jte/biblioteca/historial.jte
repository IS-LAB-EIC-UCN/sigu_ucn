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
    <h1>Historial de Préstamos</h1>

    <form method="get" action="/biblioteca/historial">
        <select name="lectorId">
            @for(Lector l : lectores)
            <option value="${l.getId()}">${l.getNombre()}</option>
            @endfor
        </select>
        <button type="submit">Ver historial</button>
    </form>

    @if(historial != null && !historial.isEmpty())
        <h2>Historial de ${lectorNombre}</h2>
        <table border="1">
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
    @endif
`
)