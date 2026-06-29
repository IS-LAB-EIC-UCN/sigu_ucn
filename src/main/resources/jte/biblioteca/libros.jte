@import java.util.List
@import java.util.Map
@import cl.ucn.app.model.biblioteca.Libro
@import cl.ucn.app.model.biblioteca.PrestamoLibro
@param String usuarioNombre
@param String usuarioRol
@param List<Libro> libros
@param Map<Long, Integer> totalEjemplares
@param Map<Long, Integer> disponibles
@param String search = ""
@param String categoria = ""
@param boolean soloDisponibles = false
@param int kpiTotalLibros = 0
@param int kpiTotalEjemplares = 0
@param int kpiPrestamosActivos = 0
@param int kpiPrestamosAtrasados = 0
@param java.math.BigDecimal kpiTotalMultas = null
@param java.math.BigDecimal lectorMultasPendientes = null
@param List<PrestamoLibro> entregasPendientes = null
@param List<PrestamoLibro> devolucionesPendientes = null
@param boolean asignado = false

@template.layout.main(
title = "Catálogo de Libros - SIGU-UCN",
usuarioNombre = usuarioNombre,
content = @`    <link rel="stylesheet" href="/css/biblioteca.css">
    <script src="/js/biblioteca.js"></script>
 
    <div class="bib-container">
        <h1>Catálogo de Libros</h1>
 
        @if(asignado)
            <div class="bib-alert bib-alert-success bib-mb-3">
                <strong>Asignación completada.</strong> El libro fue asignado correctamente al lector y el préstamo quedó registrado como activo.
            </div>
        @endif
 
        @if(lectorMultasPendientes != null && lectorMultasPendientes.signum() > 0)
            <div class="bib-alert bib-alert-error bib-mb-3">
                <strong>¡Atención!</strong> Tienes multas pendientes de pago por un total de $${lectorMultasPendientes.intValue()}. Por favor, acércate a la biblioteca para regularizar tu situación.
            </div>
        @endif
 
        @if(usuarioRol != null && usuarioRol.equals("ADMIN"))
            <div class="bib-kpi-grid">
                <div class="bib-kpi-card">
                    <span class="kpi-title">Libros</span>
                    <span class="kpi-value">${kpiTotalLibros}</span>
                </div>
                <div class="bib-kpi-card">
                    <span class="kpi-title">Ejemplares</span>
                    <span class="kpi-value">${kpiTotalEjemplares}</span>
                </div>
                <div class="bib-kpi-card">
                    <span class="kpi-title">Préstamos activos</span>
                    <span class="kpi-value">${kpiPrestamosActivos}</span>
                    @if(kpiPrestamosAtrasados > 0)
                        <span class="kpi-sub kpi-sub-danger">${kpiPrestamosAtrasados} atrasados</span>
                    @endif
                </div>
                <div class="bib-kpi-card">
                    <span class="kpi-title">Multas por recaudar</span>
                    <span class="kpi-value">$${kpiTotalMultas != null ? kpiTotalMultas.intValue() : 0}</span>
                </div>
            </div>
 
            @if((entregasPendientes != null && !entregasPendientes.isEmpty()) || (devolucionesPendientes != null && !devolucionesPendientes.isEmpty()))
                <div class="bib-card bib-pending-card">
                    <h2 class="bib-pending-title">Pendientes de Aprobación</h2>
                    
                    @if(entregasPendientes != null && !entregasPendientes.isEmpty())
                        <h3 class="bib-pending-subtitle">Entregas Pendientes</h3>
                        <table class="bib-table bib-mb-3">
                            <thead>
                                <tr>
                                    <th>Lector</th>
                                    <th>Libro</th>
                                    <th>Fecha de Reserva</th>
                                    <th>Acción</th>
                                </tr>
                            </thead>
                            <tbody>
                                @for(PrestamoLibro p : entregasPendientes)
                                    <tr>
                                        <td><strong>${p.getLector() != null ? p.getLector().getNombre() : "N/A"}</strong><br/><small class="bib-cell-sub">RUT: ${p.getLector() != null ? p.getLector().getRut() : "N/A"}</small></td>
                                        <td><strong>${p.getEjemplar() != null && p.getEjemplar().getLibro() != null ? p.getEjemplar().getLibro().getTitulo() : "N/A"}</strong></td>
                                        <td>${p.getFechaInicio() != null ? p.getFechaInicio().toString() : "N/A"}</td>
                                        <td>
                                            <form method="post" action="/biblioteca/prestamo/confirmar" class="bib-form-inline">
                                                <input type="hidden" name="prestamoId" value="${p.getId()}"/>
                                                <input type="hidden" name="returnTo" value="/biblioteca/libros"/>
                                                <button type="submit" class="bib-btn bib-btn-accent bib-btn-sm-action">Confirmar Entrega</button>
                                            </form>
                                        </td>
                                    </tr>
                                @endfor
                            </tbody>
                        </table>
                    @endif
 
                    @if(devolucionesPendientes != null && !devolucionesPendientes.isEmpty())
                        <h3 class="bib-pending-subtitle">Devoluciones Pendientes</h3>
                        <table class="bib-table">
                            <thead>
                                <tr>
                                    <th>Lector</th>
                                    <th>Libro</th>
                                    <th>Fecha de Vencimiento</th>
                                    <th>Acción</th>
                                </tr>
                            </thead>
                            <tbody>
                                @for(PrestamoLibro p : devolucionesPendientes)
                                    <tr>
                                        <td><strong>${p.getLector() != null ? p.getLector().getNombre() : "N/A"}</strong><br/><small class="bib-cell-sub">RUT: ${p.getLector() != null ? p.getLector().getRut() : "N/A"}</small></td>
                                        <td><strong>${p.getEjemplar() != null && p.getEjemplar().getLibro() != null ? p.getEjemplar().getLibro().getTitulo() : "N/A"}</strong></td>
                                        <td>${p.getFechaVencimiento() != null ? p.getFechaVencimiento().toString() : "N/A"}</td>
                                        <td>
                                            <form method="post" action="/biblioteca/prestamo/finalizar" class="bib-form-inline">
                                                <input type="hidden" name="prestamoId" value="${p.getId()}"/>
                                                <input type="hidden" name="returnTo" value="/biblioteca/libros"/>
                                                <button type="submit" class="bib-btn bib-btn-primary bib-btn-sm-action">Confirmar Devolución</button>
                                            </form>
                                        </td>
                                    </tr>
                                @endfor
                            </tbody>
                        </table>
                    @endif
                </div>
            @endif
        @endif
 
        <div class="bib-toolbar bib-mb-3">
            <form method="get" action="/biblioteca/libros" class="bib-search-form">
                <input type="text" id="bib-live-search" name="search" class="bib-form-input bib-form-input-search" placeholder="Buscar por título o autor..." value="${search}"/>
                
                <select name="categoria" class="bib-form-input bib-w-200">
                    <option value="">Todas las categorías</option>
                    <option value="CIENCIAS" selected="${"CIENCIAS".equals(categoria)}">Ciencias</option>
                    <option value="ECONOMIA" selected="${"ECONOMIA".equals(categoria)}">Economía</option>
                    <option value="HISTORIA" selected="${"HISTORIA".equals(categoria)}">Historia</option>
                    <option value="PROGRAMACION" selected="${"PROGRAMACION".equals(categoria)}">Programación</option>
                    <option value="HUMANIDADES" selected="${"HUMANIDADES".equals(categoria)}">Humanidades</option>
                    <option value="OTRO" selected="${"OTRO".equals(categoria)}">Otro</option>
                </select>
 
                <label class="bib-checkbox-label">
                    <input type="checkbox" name="soloDisponibles" value="true" checked="${soloDisponibles}" class="bib-checkbox-input"/>
                    Solo disponibles
                </label>
 
                <button type="submit" class="bib-btn bib-btn-primary">Filtrar</button>
                <a href="/biblioteca/libros" class="bib-btn bib-btn-secondary">Limpiar</a>
            </form>
        </div>
 
        <div class="bib-actions-bar">
            @if(usuarioRol != null && usuarioRol.equals("ADMIN"))
                <a href="/biblioteca/libros/nuevo" class="bib-btn bib-btn-primary">Registrar libro</a>
                <a href="/biblioteca/ejemplares/nuevo" class="bib-btn bib-btn-primary">Registrar ejemplar</a>
                <a href="/biblioteca/historial" class="bib-btn bib-btn-primary">Préstamos</a>
                <a href="/biblioteca/lectores" class="bib-btn bib-btn-primary">Lectores</a>
            @endif
            @if(usuarioRol != null && (usuarioRol.equals("DOCENTE") || usuarioRol.equals("ESTUDIANTE")))
                <a href="/biblioteca/historial" class="bib-btn bib-btn-primary">Mis préstamos</a>
                <a href="/biblioteca/mis-datos" class="bib-btn bib-btn-primary">Mis datos</a>
            @endif
        </div>
 
        @if(libros != null && !libros.isEmpty())
            @if(usuarioRol != null && usuarioRol.equals("ADMIN"))
                <table class="bib-table bib-table-catalog">
                    <thead>
                        <tr>
                            <th>Título</th>
                            <th>Autor</th>
                            <th>Categoría</th>
                            <th>ISBN</th>
                            <th>Ejemplares</th>
                            <th>Disponibles</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        @for(Libro libro : libros)
                        <tr>
                            <td class="bib-table-cell-bold">${libro.getTitulo()}</td>
                            <td>${libro.getAutor()}</td>
                            <td>${libro.getCategoria()}</td>
                            <td>${libro.getIsbn()}</td>
                            <td>${totalEjemplares.getOrDefault(libro.getId(), 0)}</td>
                            <td>
                                @if(disponibles.getOrDefault(libro.getId(), 0) > 0)
                                    <span class="bib-badge bib-badge-disponible">${disponibles.getOrDefault(libro.getId(), 0)}</span>
                                @else
                                    <span class="bib-badge bib-badge-bloqueado">Agotado</span>
                                @endif
                            </td>
                            <td>
                                <a href="/biblioteca/libros/editar?id=${libro.getId()}" class="bib-action-link">Editar</a>
                                @if(disponibles.getOrDefault(libro.getId(), 0) > 0)
                                    <a href="/biblioteca/prestamo/nuevo?libroId=${libro.getId()}" class="bib-action-link bib-ml-12">Prestar</a>
                                @endif
                            </td>
                        </tr>
                        @endfor
                    </tbody>
                </table>
            @else
                <div class="bib-book-grid">
                    @for(Libro libro : libros)
                        <div class="bib-book-card ${disponibles.getOrDefault(libro.getId(), 0) == 0 ? "bib-book-card-exhausted" : ""}">
                            <div class="bib-book-cover category-${libro.getCategoria().toLowerCase()}">
                                <span class="cover-category">${libro.getCategoria()}</span>
                                <h3 class="cover-title">${libro.getTitulo()}</h3>
                                <span class="cover-author">${libro.getAutor()}</span>
                            </div>
                            <div class="bib-book-info">
                                <h4 class="book-card-title">${libro.getTitulo()}</h4>
                                <span class="book-card-author">Por ${libro.getAutor()}</span>
                                <div class="bib-book-meta-spacing">
                                    @if(disponibles.getOrDefault(libro.getId(), 0) > 0)
                                        <span class="bib-badge bib-badge-disponible">${disponibles.getOrDefault(libro.getId(), 0)} disponibles</span>
                                    @else
                                        <span class="bib-badge bib-badge-bloqueado">Agotado</span>
                                    @endif
                                </div>
                                <div class="bib-book-actions bib-book-actions-flex">
                                    @if("ADMIN".equals(usuarioRol))
                                        @if(disponibles.getOrDefault(libro.getId(), 0) > 0)
                                            <a href="/biblioteca/prestamo/nuevo?libroId=${libro.getId()}" class="bib-btn bib-btn-assign">Asignar a lector</a>
                                        @else
                                            <span class="bib-book-exhausted-label">Sin ejemplares disponibles</span>
                                        @endif
                                    @else
                                        @if(disponibles.getOrDefault(libro.getId(), 0) > 0)
                                            @if(lectorMultasPendientes != null && lectorMultasPendientes.signum() > 0)
                                                <span class="bib-badge bib-badge-bloqueado bib-badge-blocked-label">Bloqueado por multas</span>
                                            @else
                                                <a href="/biblioteca/prestamo/nuevo?libroId=${libro.getId()}" class="bib-btn bib-btn-accent bib-btn-request">Solicitar</a>
                                            @endif
                                        @endif
                                    @endif
                                </div>
                            </div>
                        </div>
                    @endfor
                </div>
            @endif
        @else
            <p>No se encontraron libros que coincidan con la búsqueda.</p>
        @endif
    </div>
`
)
