@import java.util.List
@import cl.ucn.app.controller.biblioteca.PrestamoConMulta
@param String usuarioNombre
@param String usuarioRol
@param boolean esAdmin
@param List<PrestamoConMulta> historial

@template.layout.main(
title = (esAdmin ? "Préstamos - SIGU-UCN" : "Mis Préstamos - SIGU-UCN"),
usuarioNombre = usuarioNombre,
content = @`
    <link rel="stylesheet" href="/css/biblioteca.css">
    <script src="/js/biblioteca.js"></script>

    <div class="bib-container">
        <p><a href="/biblioteca/libros" class="bib-action-link">&larr; Volver al catálogo</a></p>

        @if(esAdmin)
            <h1>Préstamos</h1>
        @else
            <h1>Mis Préstamos</h1>
        @endif

        @if(historial != null && !historial.isEmpty())
            <input type="text" id="bib-live-search" class="bib-form-input bib-live-search" placeholder="🔍 Buscar en préstamos..."/>
            <table class="bib-table" id="bib-live-search-table">
                <thead>
                    <tr>
                        @if(esAdmin)
                            <th>Lector</th>
                        @endif
                        <th>Libro</th>
                        <th>Fecha inicio</th>
                        <th>Fecha vencimiento</th>
                        <th>Fecha devolución</th>
                        <th>Estado</th>
                        <th>Multa</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    @for(PrestamoConMulta p : historial)
                    <tr>
                        @if(esAdmin)
                            <td class="bib-cell-lector"><strong>${p.getLectorNombre()}</strong><br/><small class="bib-cell-sub">RUT: ${p.getLectorRut()}</small></td>
                        @endif
                        <td class="bib-cell-libro">${p.getLibroTitulo()}</td>
                        <td>${p.getFechaInicio() != null ? p.getFechaInicio().toString() : "N/A"}</td>
                        <td>${p.getFechaVencimiento() != null ? p.getFechaVencimiento().toString() : "N/A"}</td>
                        <td>${p.getFechaDevolucion() != null ? p.getFechaDevolucion().toString() : "No devuelto"}</td>
                        <td>
                            @if("SOLICITADO".equals(p.getEstado()))
                                <span class="bib-badge bib-badge-pendiente">Solicitado</span>
                            @elseif("ACTIVO".equals(p.getEstado()))
                                <span class="bib-badge bib-badge-activo">En préstamo</span>
                            @elseif("PENDIENTE_DEVOLUCION".equals(p.getEstado()))
                                <span class="bib-badge bib-badge-pendiente">En devolución</span>
                            @else
                                <span class="bib-badge bib-badge-finalizado">Devuelto</span>
                            @endif
                        </td>
                        @if(p.getMontoMulta() != null)
                            <td>
                                <strong class="bib-monto-multa">$${p.getMontoMulta().toString()}</strong> <br/><small class="bib-cell-sub">(${p.getDiasAtraso()} días de atraso)</small>
                                @if(esAdmin && p.getMulta() != null && !p.getMulta().getPagada())
                                    <form method="post" action="/biblioteca/multa/pagar" class="bib-form-inline">
                                        <input type="hidden" name="multaId" value="${p.getMulta().getId()}"/>
                                        <input type="hidden" name="returnTo" value="/biblioteca/historial"/>
                                        <button type="submit" class="bib-btn bib-btn-accent bib-btn-sm">Registrar pago</button>
                                    </form>
                                @elseif(p.getMulta() != null && p.getMulta().getPagada())
                                    <br/><span class="bib-badge bib-badge-finalizado bib-badge-sm">Pagada</span>
                                @endif
                            </td>
                        @else
                            <td>-</td>
                        @endif
                        <td>
                            @if(esAdmin)
                                @if("SOLICITADO".equals(p.getEstado()))
                                    <form method="post" action="/biblioteca/prestamo/confirmar" class="bib-form-inline">
                                        <input type="hidden" name="prestamoId" value="${p.getId()}"/>
                                        <input type="hidden" name="returnTo" value="/biblioteca/historial"/>
                                        <button type="submit" class="bib-btn bib-btn-accent bib-btn-sm">Confirmar Entrega</button>
                                    </form>
                                @elseif("ACTIVO".equals(p.getEstado()) || "PENDIENTE_DEVOLUCION".equals(p.getEstado()))
                                    <form method="post" action="/biblioteca/devolucion" class="bib-form-inline">
                                        <input type="hidden" name="prestamoId" value="${p.getId()}"/>
                                        <input type="hidden" name="returnTo" value="/biblioteca/historial"/>
                                        <button type="submit" class="bib-btn bib-btn-accent bib-btn-sm">Registrar Devolución</button>
                                    </form>
                                @else
                                    <span class="bib-cell-muted">-</span>
                                @endif
                            @else
                                @if("ACTIVO".equals(p.getEstado()))
                                    <form method="post" action="/biblioteca/prestamo/devolver" class="bib-form-inline">
                                        <input type="hidden" name="prestamoId" value="${p.getId()}"/>
                                        <input type="hidden" name="returnTo" value="/biblioteca/historial"/>
                                        <button type="submit" class="bib-btn bib-btn-accent bib-btn-sm">Devolver libro</button>
                                    </form>
                                @else
                                    <span class="bib-cell-muted">-</span>
                                @endif
                            @endif
                        </td>
                    </tr>
                    @endfor
                </tbody>
            </table>
        @else
            <p>No ${esAdmin ? "hay préstamos registrados" : "tienes préstamos registrados"}.</p>
        @endif
    </div>
`
)
