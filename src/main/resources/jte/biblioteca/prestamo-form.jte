@import cl.ucn.app.controller.biblioteca.LibroDisponible
@import cl.ucn.app.model.biblioteca.Lector
@import java.util.List
@param String usuarioNombre
@param List<Lector> lectores
@param List<LibroDisponible> librosDisponibles
@param String error
@param String lectorId
@param String libroId
@param String fechaVencimiento

@template.layout.main(
title = "Solicitar Prestamo - SIGU-UCN",
usuarioNombre = usuarioNombre,
content = @`
    <link rel="stylesheet" href="/css/biblioteca.css">

    <p><a href="/biblioteca/libros">Volver al catalogo</a></p>

    <h1>Solicitar Prestamo</h1>

    @if(error != null)
        <div class="alert-error">${error}</div>
    @endif

    <div class="info-card">
        <form method="post" action="/biblioteca/prestamo/nuevo">
            <div class="form-group">
                <label>Lector</label>
                <select name="lectorId">
                    @for(Lector l : lectores)
                    @if(lectorId != null && !lectorId.isEmpty() && lectorId.equals(String.valueOf(l.getId())))
                        <option value="${l.getId()}" selected>${l.getNombre()} - ${l.getRut()}</option>
                    @else
                        <option value="${l.getId()}">${l.getNombre()} - ${l.getRut()}</option>
                    @endif
                    @endfor
                </select>
            </div>
            <div class="form-group">
                <label>Libro a solicitar</label>
                <select name="libroId">
                    @if(librosDisponibles != null)
                        @for(LibroDisponible ld : librosDisponibles)
                        @if(libroId != null && !libroId.isEmpty() && ld.getLibro().getId().equals(Long.parseLong(libroId)))
                            <option value="${ld.getLibro().getId()}" selected>${ld.getLabel()}</option>
                        @else
                            <option value="${ld.getLibro().getId()}">${ld.getLabel()}</option>
                        @endif
                        @endfor
                    @endif
                </select>
            </div>
            <div class="form-group">
                <label>Fecha de vencimiento</label>
                <input type="date" name="fechaVencimiento" value="${fechaVencimiento != null ? fechaVencimiento : ""}" required/>
            </div>
            <button type="submit" class="primary-btn">Confirmar prestamo</button>
        </form>
    </div>
`
)
