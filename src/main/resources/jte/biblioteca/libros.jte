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
@param double kpiTotalMultas = 0.0
@param double lectorMultasPendientes = 0.0
@param List<PrestamoLibro> entregasPendientes = null
@param List<PrestamoLibro> devolucionesPendientes = null
@param boolean asignado = false

@template.layout.main(
title = "Catálogo de Libros - SIGU-UCN",
usuarioNombre = usuarioNombre,
content = @`
    <link rel="stylesheet" href="/css/biblioteca.css">
    <script src="/js/biblioteca.js"></script>

    <div class="bib-container">
        <h1>Catálogo de Libros</h1>

        @if(asignado)
            <div class="bib-alert bib-alert-success" style="margin-top:15px;margin-bottom:20px;">
                <strong>Asignación completada.</strong> El libro fue asignado correctamente al lector y el préstamo quedó registrado como activo.
            </div>
        @endif

        @if(lectorMultasPendientes > 0)
            <div class="bib-alert bib-alert-error" style="margin-top: 15px; margin-bottom: 20px;">
                <strong>¡Atención!</strong> Tienes multas pendientes de pago por un total de $${(int)lectorMultasPendientes}. Por favor, acércate a la biblioteca para regularizar tu situación.
            </div>
        @endif

        @if(usuarioRol != null && usuarioRol.equals("ADMIN"))
            <div class="bib-kpi-grid">
                <div class="bib-kpi-card">
                    <span class="kpi-title">Títulos registrados</span>
                    <span class="kpi-value">${kpiTotalLibros}</span>
                </div>
                <div class="bib-kpi-card">
                    <span class="kpi-title">Ejemplares totales</span>
                    <span class="kpi-value">${kpiTotalEjemplares}</span>
                </div>
                <div class="bib-kpi-card">
                    <span class="kpi-title">Préstamos activos</span>
                    <span class="kpi-value">${kpiPrestamosActivos}</span>
                    @if(kpiPrestamosAtrasados > 0)
                        <span class="kpi-sub" style="color:var(--bib-danger);font-weight:600;">${kpiPrestamosAtrasados} atrasados</span>
                    @else
                        <span class="kpi-sub" style="color:var(--bib-success);">Al día</span>
                    @endif
                </div>
                <div class="bib-kpi-card">
                    <span class="kpi-title">Multas por recaudar</span>
                    <span class="kpi-value">$${(int)kpiTotalMultas}</span>
                </div>
            </div>

            @if((entregasPendientes != null && !entregasPendientes.isEmpty()) || (devolucionesPendientes != null && !devolucionesPendientes.isEmpty()))
                <div class="bib-card" style="margin-top: 25px; margin-bottom: 25px; border-color: var(--bib-accent);">
                    <h2 style="margin-top:0; color:var(--bib-accent); font-size:1.4em;">Pendientes de Aprobación</h2>
                    
                    @if(entregasPendientes != null && !entregasPendientes.isEmpty())
                        <h3 style="font-size:1.1em; margin-bottom:10px;">Entregas Pendientes (Retiro en Biblioteca)</h3>
                        <table class="bib-table" style="margin-bottom:20px;">
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
                                        <td><strong>${p.getLector().getNombre()}</strong><br/><small style="color:#555;">RUT: ${p.getLector().getRut()}</small></td>
                                        <td><strong>${p.getEjemplar().getLibro().getTitulo()}</strong></td>
                                        <td>${p.getFechaInicio().toString()}</td>
                                        <td>
                                            <form method="post" action="/biblioteca/prestamo/confirmar" style="display:inline;">
                                                <input type="hidden" name="prestamoId" value="${p.getId()}"/>
                                                <button type="submit" class="bib-btn bib-btn-accent" style="padding:6px 12px; font-size:0.85em;">Confirmar Entrega</button>
                                            </form>
                                        </td>
                                    </tr>
                                @endfor
                            </tbody>
                        </table>
                    @endif

                    @if(devolucionesPendientes != null && !devolucionesPendientes.isEmpty())
                        <h3 style="font-size:1.1em; margin-bottom:10px;">Devoluciones Pendientes (Recepción Física)</h3>
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
                                        <td><strong>${p.getLector().getNombre()}</strong><br/><small style="color:#555;">RUT: ${p.getLector().getRut()}</small></td>
                                        <td><strong>${p.getEjemplar().getLibro().getTitulo()}</strong></td>
                                        <td>${p.getFechaVencimiento().toString()}</td>
                                        <td>
                                            <form method="post" action="/biblioteca/prestamo/finalizar" style="display:inline;">
                                                <input type="hidden" name="prestamoId" value="${p.getId()}"/>
                                                <button type="submit" class="bib-btn bib-btn-primary" style="padding:6px 12px; font-size:0.85em;">Confirmar Devolución</button>
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

        <div class="bib-toolbar" style="margin-bottom:20px;">
            <form method="get" action="/biblioteca/libros" class="bib-search-form" style="display:flex;gap:10px;flex-wrap:wrap;align-items:center;width:100%;">
                <input type="text" name="search" class="bib-form-input" placeholder="Buscar por título o autor..." value="${search}" style="flex:1;min-width:200px;margin:0;"/>
                
                <select name="categoria" class="bib-form-input" style="width:200px;margin:0;">
                    <option value="">Todas las categorías</option>
                    <option value="CIENCIAS" selected="${"CIENCIAS".equals(categoria)}">Ciencias</option>
                    <option value="ECONOMIA" selected="${"ECONOMIA".equals(categoria)}">Economía</option>
                    <option value="HISTORIA" selected="${"HISTORIA".equals(categoria)}">Historia</option>
                    <option value="PROGRAMACION" selected="${"PROGRAMACION".equals(categoria)}">Programación</option>
                    <option value="HUMANIDADES" selected="${"HUMANIDADES".equals(categoria)}">Humanidades</option>
                    <option value="OTRO" selected="${"OTRO".equals(categoria)}">Otro</option>
                </select>

                <label style="display:flex;align-items:center;gap:6px;font-weight:600;cursor:pointer;user-select:none;">
                    <input type="checkbox" name="soloDisponibles" value="true" checked="${soloDisponibles}" style="width:18px;height:18px;margin:0;accent-color:var(--bib-primary);"/>
                    Solo disponibles
                </label>

                <button type="submit" class="bib-btn bib-btn-primary" style="margin:0;">Filtrar</button>
                <a href="/biblioteca/libros" class="bib-btn bib-btn-secondary" style="margin:0;text-decoration:none;display:inline-flex;align-items:center;justify-content:center;">Limpiar</a>
            </form>
        </div>

        <div class="bib-actions-bar" style="display:flex;gap:10px;flex-wrap:wrap;margin-bottom:25px;padding-bottom:15px;border-bottom:1px solid #eee;">
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
                <table class="bib-table">
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
                            <td style="font-weight:600;color:var(--bib-primary);">${libro.getTitulo()}</td>
                            <td>${libro.getAutor()}</td>
                            <td>${libro.getCategoria()}</td>
                            <td>${libro.getIsbn()}</td>
                            <td>${totalEjemplares.getOrDefault(libro.getId(), 0)}</td>
                            <td>
                                @if(disponibles.getOrDefault(libro.getId(), 0) > 0)
                                    <span class="bib-badge bib-badge-disponible">${disponibles.getOrDefault(libro.getId(), 0)} disp.</span>
                                @else
                                    <span class="bib-badge bib-badge-bloqueado">Agotado</span>
                                @endif
                            </td>
                            <td>
                                <a href="/biblioteca/libros/editar?id=${libro.getId()}" class="bib-action-link">Editar</a>
                                @if(disponibles.getOrDefault(libro.getId(), 0) > 0)
                                    <a href="/biblioteca/prestamo/nuevo?libroId=${libro.getId()}" class="bib-action-link" style="margin-left:12px;">Asignar</a>
                                @endif
                                <a href="/biblioteca/libros/eliminar?id=${libro.getId()}" class="bib-action-link-danger bib-confirm-delete" data-message="¿Está seguro de que desea eliminar el libro '${libro.getTitulo()}' y todos sus ejemplares/préstamos?" style="margin-left:12px;">Eliminar</a>
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
                                <div style="margin-top:10px;margin-bottom:12px;">
                                    @if(disponibles.getOrDefault(libro.getId(), 0) > 0)
                                        <span class="bib-badge bib-badge-disponible">${disponibles.getOrDefault(libro.getId(), 0)} disponibles</span>
                                    @else
                                        <span class="bib-badge bib-badge-bloqueado">Agotado</span>
                                    @endif
                                </div>
                                <div class="bib-book-actions" style="display:flex;width:100%;">
                                    @if("ADMIN".equals(usuarioRol))
                                        @if(disponibles.getOrDefault(libro.getId(), 0) > 0)
                                            <a href="/biblioteca/prestamo/nuevo?libroId=${libro.getId()}" class="bib-btn" style="background:#4a6c8c;color:#fff;text-decoration:none;text-align:center;display:inline-flex;align-items:center;justify-content:center;flex:1;">Asignar a lector</a>
                                        @else
                                            <span style="font-size:0.8em;color:#888;text-align:center;width:100%;">Sin ejemplares disponibles</span>
                                        @endif
                                    @else
                                        @if(disponibles.getOrDefault(libro.getId(), 0) > 0)
                                            @if(lectorMultasPendientes > 0)
                                                <span class="bib-badge bib-badge-bloqueado" style="text-align:center;width:100%;padding:8px;font-size:0.9em;display:block;">Bloqueado por multas</span>
                                            @else
                                                <a href="/biblioteca/prestamo/nuevo?libroId=${libro.getId()}" class="bib-btn bib-btn-accent" style="text-decoration:none;text-align:center;display:inline-flex;align-items:center;justify-content:center;flex:1;">Solicitar</a>
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
