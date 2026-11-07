@import cl.ucn.app.model.Libro
@import cl.ucn.app.model.Ejemplar
@import cl.ucn.app.model.Lector
@import cl.ucn.app.model.Prestamo
@import cl.ucn.app.model.Multa
@import java.util.List

@param String title
@param String usuarioNombre
@param String usuarioRol
@param boolean esBibliotecario
@param List<Libro> libros
@param String busqueda
@param Lector lector = null
@param List<Prestamo> prestamosActivos = null
@param List<Multa> multasPendientes = null

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <section class="dashboard">
        <h1>Sistema de Gestión de Biblioteca (Grupo 2)</h1>
        <p>Rol activo: <strong>${usuarioRol}</strong></p>

        @if(!esBibliotecario && lector != null)
            <div class="info-card" style="margin-bottom: 20px; border-left: 5px solid #1f3b73;">
                <h3>Mi Estado de Lector</h3>
                <p><strong>Nombre:</strong> ${lector.getNombre()} | <strong>RUT:</strong> ${lector.getRut()} | <strong>Tipo:</strong> ${lector.getTipo().name()} (Plazo: ${lector.getTipo().getDiasPlazo()} días, Límite: ${lector.getTipo().getMaxPrestamos()} préstamos)</p>
                <p><strong>Estado:</strong> 
                    @if(lector.estaHabilitado())
                        <span style="color: green; font-weight: bold;">HABILITADO</span>
                    @else
                        <span style="color: red; font-weight: bold;">BLOQUEADO</span> (Debe regularizar multas pendientes)
                    @endif
                </p>
                
                @if(multasPendientes != null && !multasPendientes.isEmpty())
                    <h4 style="color: #8f1d1d; margin-top: 10px;">Multas Pendientes</h4>
                    <ul>
                        @for(Multa m : multasPendientes)
                            <li>Multa #${m.getId()} - Días de atraso: ${m.getDiasAtraso()} - Monto: $${m.getMonto()} (Generada: ${m.getFechaGeneracion().toString()})</li>
                        @endfor
                    </ul>
                @endif

                @if(prestamosActivos != null && !prestamosActivos.isEmpty())
                    <h4 style="margin-top: 10px;">Mis Préstamos Activos</h4>
                    <ul>
                        @for(Prestamo p : prestamosActivos)
                            <li>
                                <strong>${p.getEjemplar().getLibro().getTitulo()}</strong> (${p.getEjemplar().getCodigoInventario()}) - 
                                Fecha límite: ${p.getFechaLimite().toString()}
                            </li>
                        @endfor
                    </ul>
                @endif
            </div>
        @endif

        @if(esBibliotecario)
            <div class="card-grid" style="margin-bottom: 25px;">
                <div class="info-card">
                    <h3>Mesón: Registrar Devolución (CU-02)</h3>
                    <form method="POST" action="/biblioteca/devoluciones">
                        <div class="form-group">
                            <label>Código de Inventario:</label>
                            <input type="text" name="codigoInventario" placeholder="Ej: EJ-CC-001" required />
                        </div>
                        <div class="form-group">
                            <label>
                                <input type="checkbox" name="danado" value="true" /> Ejemplar dañado (enviar a reparación)
                            </label>
                        </div>
                        <button type="submit" class="primary-btn">Registrar Devolución</button>
                    </form>
                </div>

                <div class="info-card">
                    <h3>Mesón: Cobrar Multa (CU-03)</h3>
                    <form method="POST" action="/biblioteca/multas/pago">
                        <div class="form-group">
                            <label>ID de la Multa:</label>
                            <input type="number" name="multaId" placeholder="Ej: 1" required />
                        </div>
                        <button type="submit" class="primary-btn">Registrar Pago y Desbloquear</button>
                    </form>
                </div>
            </div>
        @endif

        <div class="info-card">
            <h3>Catálogo de Libros y Búsqueda (CU-04 / RF-03)</h3>
            <form method="GET" action="/biblioteca" style="margin-bottom: 20px; display: flex; gap: 10px;">
                <input type="text" name="q" value="${busqueda}" placeholder="Buscar por título, autor o categoría..." style="flex: 1; padding: 8px 12px; border: 1px solid #c9d2df; border-radius: 8px;" />
                <button type="submit" class="primary-btn" style="width: auto;">Buscar</button>
            </form>

            <table style="width: 100%; border-collapse: collapse; text-align: left;">
                <thead>
                    <tr style="border-bottom: 2px solid #ddd; background: #fafafa;">
                        <th style="padding: 10px;">ISBN</th>
                        <th style="padding: 10px;">Título</th>
                        <th style="padding: 10px;">Categoría</th>
                        <th style="padding: 10px;">Ejemplares Disponibles</th>
                        <th style="padding: 10px;">Acción</th>
                    </tr>
                </thead>
                <tbody>
                    @if(libros.isEmpty())
                        <tr>
                            <td colspan="5" style="padding: 14px; text-align: center; color: #777;">No se encontraron libros en el catálogo.</td>
                        </tr>
                    @else
                        @for(Libro libro : libros)
                            <tr style="border-bottom: 1px solid #eee;">
                                <td style="padding: 10px;">${libro.getIsbn()}</td>
                                <td style="padding: 10px;"><strong>${libro.getTitulo()}</strong> (${libro.getAnioPublicacion()})</td>
                                <td style="padding: 10px;">${libro.getCategoria().getNombre()}</td>
                                <td style="padding: 10px;">${libro.contarDisponibles()} disponibles</td>
                                <td style="padding: 10px;">
                                    @if(!esBibliotecario && libro.contarDisponibles() > 0 && lector != null && lector.estaHabilitado())
                                        @for(Ejemplar ej : libro.getEjemplares())
                                            @if(ej.estaDisponible())
                                                <form method="POST" action="/biblioteca/prestamos" style="display:inline;">
                                                    <input type="hidden" name="ejemplarId" value="${ej.getId()}" />
                                                    <button type="submit" class="primary-btn" style="padding: 4px 8px; font-size: 0.85rem; width: auto;">Solicitar (${ej.getCodigoInventario()})</button>
                                                </form>
                                            @endif
                                        @endfor
                                    @elseif(!esBibliotecario && lector != null && !lector.estaHabilitado())
                                        <span style="color: red; font-size: 0.85rem;">Bloqueado por multas</span>
                                    @else
                                        <span style="color: #888; font-size: 0.85rem;">-</span>
                                    @endif
                                </td>
                            </tr>
                        @endfor
                    @endif
                </tbody>
            </table>
        </div>
    </section>
`
)
