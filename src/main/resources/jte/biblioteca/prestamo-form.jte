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
    <link rel="stylesheet" href="/css/biblioteca.css">

    <p><a href="/biblioteca/libros">Volver al catálogo</a></p>

    <h1>Solicitar Préstamo</h1>

    @if(error != null)
        <div class="alert-error">${error}</div>
    @endif

    <div class="info-card">
        <form method="post" action="/biblioteca/prestamo/nuevo">
            <div class="form-group">
                <label>Lector</label>
                <select name="lectorId" style="width:100%;padding:10px 12px;border:1px solid #c9d2df;border-radius:8px;">
                    @for(Lector l : lectores)
                    <option value="${l.getId()}">${l.getNombre()} — ${l.getRut()}</option>
                    @endfor
                </select>
            </div>
            <div class="form-group">
                <label>Ejemplar disponible</label>
                <select name="ejemplarId" style="width:100%;padding:10px 12px;border:1px solid #c9d2df;border-radius:8px;">
                    @if(ejemplares != null)
                        @for(Ejemplar e : ejemplares)
                        <option value="${e.getId()}">${e.getLibro().getTitulo()} (ID: ${e.getId()})</option>
                        @endfor
                    @endif
                </select>
            </div>
            <div class="form-group">
                <label>Fecha de vencimiento</label>
                <input type="date" name="fechaVencimiento" required/>
            </div>
            <button type="submit" class="primary-btn">Confirmar préstamo</button>
        </form>
    </div>
`
)
