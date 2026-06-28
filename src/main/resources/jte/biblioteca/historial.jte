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
            <table class="bib-table">
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
                            <td style="font-weight:600;">${p.getLector().getNombre()} <br/><small style="color:#555;">RUT: ${p.getLector().getRut()}</small></td>
                        @endif
                        <td style="font-weight:600;color:var(--bib-primary);">${p.getLibroTitulo()}</td>
                        <td>${p.getFechaInicio().toString()}</td>
                        <td>${p.getFechaVencimiento().toString()}</td>
                        <td>${p.getFechaDevolucion() != null ? p.getFechaDevolucion().toString() : "No devuelto"}</td>
                        <td>
                            @if("ACTIVO".equals(p.getEstado()))
                                <span class="bib-badge bib-badge-activo">Activo</span>
                            @else
                                <span class="bib-badge bib-badge-finalizado">Devuelto</span>
                            @endif
                        </td>
                        @if(p.getMontoMulta() != null)
                            <td>
                                <strong style="color:var(--bib-danger);">$${p.getMontoMulta().toString()}</strong> <br/><small style="color:#555;">(${p.getDiasAtraso()} días de atraso)</small>
                                @if(esAdmin && !p.getMulta().getPagada())
                                    <form method="post" action="/biblioteca/multa/pagar" style="display:block;margin-top:4px;">
                                        <input type="hidden" name="multaId" value="${p.getMulta().getId()}"/>
                                        <button type="submit" class="bib-btn bib-btn-accent" style="font-size:0.8em;padding:4px 8px;margin-top:4px;">Registrar pago</button>
                                    </form>
                                @elseif(p.getMulta().getPagada())
                                    <br/><span class="bib-badge bib-badge-finalizado" style="font-size:0.75em;padding:2px 6px;margin-top:4px;">Pagada</span>
                                @endif
                            </td>
                        @else
                            <td>-</td>
                        @endif
                        <td>
                            @if("ACTIVO".equals(p.getEstado()))
                                @if(esAdmin)
                                    <form method="post" action="/biblioteca/devolucion" style="display:inline;">
                                        <input type="hidden" name="prestamoId" value="${p.getId()}"/>
                                        <button type="submit" class="bib-btn bib-btn-accent" style="padding:6px 12px;font-size:0.9em;">Registrar Devolución</button>
                                    </form>
                                @else
                                    <form method="post" action="/biblioteca/prestamo/devolver" style="display:inline;">
                                        <input type="hidden" name="prestamoId" value="${p.getId()}"/>
                                        <button type="submit" class="bib-btn bib-btn-accent" style="padding:6px 12px;font-size:0.9em;">Devolver</button>
                                    </form>
                                @endif
                            @else
                                <span style="color:#999;">-</span>
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
