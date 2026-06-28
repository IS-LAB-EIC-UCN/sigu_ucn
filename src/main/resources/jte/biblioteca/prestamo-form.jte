@import cl.ucn.app.controller.biblioteca.LibroDisponible
@import cl.ucn.app.model.biblioteca.Lector
@import java.util.List
@param String usuarioNombre
@param String usuarioRol = null
@param List<LibroDisponible> librosDisponibles
@param String error
@param String libroId
@param String fechaVencimiento
@param List<Lector> lectores = null

@template.layout.main(
title = "Solicitar Préstamo - SIGU-UCN",
usuarioNombre = usuarioNombre,
content = @`
    <link rel="stylesheet" href="/css/biblioteca.css">
    <script src="/js/biblioteca.js"></script>

    <div class="bib-container">
        <p><a href="/biblioteca/libros" class="bib-action-link">&larr; Volver al catálogo</a></p>

        @if(usuarioRol != null && usuarioRol.equals("ADMIN"))
            <h1>Asignar Libro a Lector</h1>
            <div class="bib-alert" style="background:#e8f0fe;border-left:4px solid #4a6c8c;color:#2d4a6e;margin-bottom:20px;padding:12px 16px;border-radius:4px;">
                Esta asignación es manual y queda directamente en estado <strong>Activo</strong>. Úsela cuando el lector esté presente en la biblioteca y retire el libro en ese momento.
            </div>
        @else
            <h1>Solicitar Préstamo</h1>
        @endif

        @if(error != null)
            <div class="bib-alert bib-alert-error">${error}</div>
        @endif

        <div class="bib-card">
            <form method="post" action="/biblioteca/prestamo/nuevo">
                @if(usuarioRol != null && usuarioRol.equals("ADMIN"))
                    <div class="bib-form-group">
                        <label>Lector</label>
                        <select name="lectorId" class="bib-form-select" required>
                            <option value="">Seleccione un lector</option>
                            @if(lectores != null)
                                @for(Lector lec : lectores)
                                    <option value="${lec.getId()}">${lec.getNombre()} (RUT: ${lec.getRut()})</option>
                                @endfor
                            @endif
                        </select>
                    </div>
                @endif
                <div class="bib-form-group">
                    <label>Libro a solicitar</label>
                    <select name="libroId" class="bib-form-select" required>
                        @if(librosDisponibles != null && !librosDisponibles.isEmpty())
                            <option value="">Seleccione un libro</option>
                            @for(LibroDisponible ld : librosDisponibles)
                            @if(libroId != null && !libroId.isEmpty() && ld.getLibro().getId().equals(Long.parseLong(libroId)))
                                <option value="${ld.getLibro().getId()}" selected>${ld.getLabel()}</option>
                            @else
                                <option value="${ld.getLibro().getId()}">${ld.getLabel()}</option>
                            @endif
                            @endfor
                        @else
                            <option value="">No hay libros disponibles</option>
                        @endif
                    </select>
                </div>
                <div class="bib-form-group">
                    <label>Fecha de vencimiento</label>
                    <input type="date" name="fechaVencimiento" class="bib-form-input" value="${fechaVencimiento != null ? fechaVencimiento : ""}" required/>
                </div>
                @if(usuarioRol != null && usuarioRol.equals("ADMIN"))
                    <button type="submit" class="bib-btn bib-btn-primary">Confirmar asignación</button>
                @else
                    <button type="submit" class="bib-btn bib-btn-primary">Confirmar préstamo</button>
                @endif
            </form>
        </div>
    </div>
`
)
