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
            <div class="bib-alert bib-alert-info">
                Esta asignación es manual y el libro queda registrado directamente como entregado (en préstamo). Úsela cuando el lector esté presente en la biblioteca y retire el libro en ese momento.
            </div>
        @else
            <h1>Solicitar Préstamo</h1>
        @endif

        @if(error != null)
            <div class="bib-alert bib-alert-error">${error}</div>
        @endif

        <div class="bib-card">
            @if(usuarioRol != null && usuarioRol.equals("ADMIN") && (lectores == null || lectores.isEmpty()))
                <div class="bib-alert bib-alert-error" style="margin-bottom: 20px;">
                    <strong>No hay lectores registrados:</strong> Debes registrar al menos un lector en el sistema antes de poder realizar una asignación.
                </div>
                <p><a href="/biblioteca/lectores" class="bib-btn bib-btn-primary">Registrar lector</a></p>
            @elseif(librosDisponibles == null || librosDisponibles.isEmpty())
                <div class="bib-alert bib-alert-error" style="margin-bottom: 20px;">
                    <strong>No hay libros disponibles:</strong> No existen ejemplares disponibles en la biblioteca actualmente para realizar un préstamo.
                </div>
                <p><a href="/biblioteca/libros" class="bib-btn bib-btn-secondary" style="text-decoration:none;">Volver al catálogo</a></p>
            @else
                <form method="post" action="/biblioteca/prestamo/nuevo">
                    @if(usuarioRol != null && usuarioRol.equals("ADMIN"))
                        <div class="bib-form-group">
                            <label>Lector</label>
                            <select name="lectorId" class="bib-form-select" required>
                                <option value="">Seleccione un lector</option>
                                @for(Lector lec : lectores)
                                    <option value="${lec.getId()}">${lec.getNombre()} (RUT: ${lec.getRut()})</option>
                                @endfor
                            </select>
                        </div>
                    @endif
                    <div class="bib-form-group">
                        <label>Libro a solicitar</label>
                        <select name="libroId" class="bib-form-select" required>
                            <option value="">Seleccione un libro</option>
                            @for(LibroDisponible ld : librosDisponibles)
                                @if(libroId != null && !libroId.isEmpty() && ld.getLibro().getId().equals(Long.parseLong(libroId)))
                                    <option value="${ld.getLibro().getId()}" selected>${ld.getLabel()}</option>
                                @else
                                    <option value="${ld.getLibro().getId()}">${ld.getLabel()}</option>
                                @endif
                            @endfor
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
            @endif
        </div>
    </div>
`
)
