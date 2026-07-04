@import java.util.List
@import cl.ucn.app.model.Ticket
@import cl.ucn.app.model.CategoriaTicket
@param List<Ticket> tickets
@param List<CategoriaTicket> categorias
@param String usuarioRol
@param String usuarioNombre

@template.layout.main(
    title = "Soporte - Lista de Tickets",
    usuarioNombre = usuarioNombre,
    content = @`
        <section class="dashboard">
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:20px;">
                <h1>Tickets de Soporte</h1>
                <span style="background:#e8f0fe; color:#1a56db; padding:4px 12px; border-radius:20px; font-size:0.8rem; font-weight:600;">${usuarioRol}</span>
            </div>

            <div style="margin-bottom:16px; display:flex; gap:10px;">
                <a href="/soporte/nuevo" class="btn-primary">+ Nuevo ticket</a>
            </div>

            <form method="get" action="/soporte" style="display:flex; gap:10px; margin-bottom:20px; flex-wrap:wrap;">
                <select name="estado" style="padding:8px 12px; border:1px solid #d0d7e3; border-radius:6px;">
                    <option value="">Todos los estados</option>
                    <option value="ABIERTO">Abierto</option>
                    <option value="EN_PROCESO">En proceso</option>
                    <option value="CERRADO">Cerrado</option>
                </select>
                <select name="prioridad" style="padding:8px 12px; border:1px solid #d0d7e3; border-radius:6px;">
                    <option value="">Todas las prioridades</option>
                    <option value="BAJA">Baja</option>
                    <option value="MEDIA">Media</option>
                    <option value="ALTA">Alta</option>
                </select>
                <button type="submit" class="btn-primary">Filtrar</button>
            </form>

            <div class="card-grid" style="display:block;">
                @if(tickets.isEmpty())
                    <p style="color:#a0aec0; text-align:center; padding:40px;">No hay tickets que coincidan con el filtro.</p>
                @else
                    <table style="width:100%; border-collapse:collapse; background:white; border-radius:10px; overflow:hidden; box-shadow:0 1px 4px rgba(0,0,0,0.07);">
                        <thead style="background:#f0f4f8;">
                            <tr>
                                <th style="padding:12px 16px; text-align:left; font-size:0.82rem; color:#5a6a85; text-transform:uppercase;">#</th>
                                <th style="padding:12px 16px; text-align:left; font-size:0.82rem; color:#5a6a85; text-transform:uppercase;">Título</th>
                                <th style="padding:12px 16px; text-align:left; font-size:0.82rem; color:#5a6a85; text-transform:uppercase;">Categoría</th>
                                <th style="padding:12px 16px; text-align:left; font-size:0.82rem; color:#5a6a85; text-transform:uppercase;">Prioridad</th>
                                <th style="padding:12px 16px; text-align:left; font-size:0.82rem; color:#5a6a85; text-transform:uppercase;">Estado</th>
                                <th style="padding:12px 16px; text-align:left; font-size:0.82rem; color:#5a6a85; text-transform:uppercase;">Técnico</th>
                            </tr>
                        </thead>
                        <tbody>
                            @for(Ticket t : tickets)
                                <tr style="border-bottom:1px solid #f0f4f8;">
                                    <td style="padding:13px 16px;">${t.getId()}</td>
                                    <td style="padding:13px 16px;"><a href="/soporte/${t.getId()}" style="color:#1a56db; font-weight:500;">${t.getTitulo()}</a></td>
                                    <td style="padding:13px 16px;">${t.getCategoria() != null ? t.getCategoria().getNombreCategoria() : "-"}</td>
                                    <td style="padding:13px 16px;">${t.getPrioridad()}</td>
                                    <td style="padding:13px 16px;">
                                        @if(t.getEstado().equals("ABIERTO"))
                                            <span style="background:#d1fae5; color:#065f46; padding:4px 10px; border-radius:20px; font-size:0.78rem; font-weight:700;">ABIERTO</span>
                                        @elseif(t.getEstado().equals("CERRADO"))
                                            <span style="background:#fee2e2; color:#991b1b; padding:4px 10px; border-radius:20px; font-size:0.78rem; font-weight:700;">CERRADO</span>
                                        @else
                                            <span style="background:#fef3c7; color:#92400e; padding:4px 10px; border-radius:20px; font-size:0.78rem; font-weight:700;">EN PROCESO</span>
                                        @endif
                                    </td>
                                    <td style="padding:13px 16px;">${t.getTecnico() != null ? t.getTecnico().getNombre() : "Sin asignar"}</td>
                                </tr>
                            @endfor
                        </tbody>
                    </table>
                @endif
            </div>
        </section>
    `
)