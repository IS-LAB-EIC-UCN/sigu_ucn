@import java.util.List
@import cl.ucn.app.model.biblioteca.Lector
@import cl.ucn.app.model.biblioteca.Ejemplar
@param String usuarioNombre
@param List<Lector> lectores
@param List<Ejemplar> ejemplares
@param String error

@template.layout.main(
title = "Solicitar Préstamo - SIGU-UCN",
usuarioNombre = usuarioNombre,
content = @`
    <h1>Solicitar Préstamo</h1>

    @if(error != null)
        <p style="color:red">${error}</p>
    @endif

    <form method="post" action="/biblioteca/prestamo/nuevo">
        <label>Lector:</label>
        <select name="lectorId">
            @for(Lector l : lectores)
            <option value="${l.getId()}">${l.getNombre()} — ${l.getRut()}</option>
            @endfor
        </select>
        <br/>
        <label>Ejemplar:</label>
        <select name="ejemplarId">
            @for(Ejemplar e : ejemplares)
            <option value="${e.getId()}">
                ${e.getLibro().getTitulo()} (ID: ${e.getId()})
            </option>
            @endfor
        </select>
        <br/>
        <label>Fecha vencimiento:</label>
        <input type="date" name="fechaVencimiento" required/>
        <br/>
        <button type="submit">Confirmar préstamo</button>
    </form>
`
)