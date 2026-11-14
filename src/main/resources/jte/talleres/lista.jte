@import cl.ucn.app.dto.talleres.TallerResumen
@import cl.ucn.app.util.Formato
@import java.util.List

@param String title
@param String usuarioNombre
@param boolean esAdmin
@param String flashOk
@param String flashError
@param List<TallerResumen> talleres
@param List<String> categorias
@param String categoriaSeleccionada
@param String fechaSeleccionada

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <section class="tl-page">
        <div class="tl-header">
            <h1>Talleres disponibles</h1>
            <div class="tl-actions">
                <%-- TODO [Tarea 3]: botón "Mis inscripciones" -> /mis-inscripciones --%>
                <%-- TODO [Tarea 1]: si esAdmin, botón "+ Nuevo taller" -> /talleres/nuevo --%>
            </div>
        </div>

        @template.talleres.flash(flashOk = flashOk, flashError = flashError)

        <form class="tl-filtros" method="get" action="/talleres">
            <label>Categoría
                <select name="categoria">
                    <option value="">Todas</option>
                    @for(String c : categorias)
                        <option value="${c}" selected="${c.equals(categoriaSeleccionada)}">${c}</option>
                    @endfor
                </select>
            </label>
            <label>Fecha
                <input type="date" name="fecha" value="${fechaSeleccionada}">
            </label>
            <button class="tl-btn" type="submit">Filtrar</button>
            <a class="tl-link" href="/talleres">Limpiar</a>
        </form>

        @if(talleres.isEmpty())
            <p class="tl-vacio">No hay talleres disponibles con esos filtros.</p>
        @else
            <table class="tl-tabla">
                <thead>
                <tr>
                    <th>Taller</th>
                    <th>Categoría</th>
                    <th>Fecha</th>
                    <th>Horario</th>
                    <th>Lugar</th>
                    <th>Cupos</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                @for(TallerResumen t : talleres)
                    <tr>
                        <td><strong>${t.nombre()}</strong></td>
                        <td><span class="tl-tag">${t.categoria()}</span></td>
                        <td>${Formato.fecha(t.fecha())}</td>
                        <td>${Formato.hora(t.horaInicio())} – ${Formato.hora(t.horaFin())}</td>
                        <td>${Formato.texto(t.lugar())}</td>
                        <td>
                            @if(t.estaLleno())
                                <span class="tl-badge tl-badge-lleno">Lleno · ${Formato.texto(t.enEspera())} en espera</span>
                            @else
                                <span class="tl-badge tl-badge-ok">${Formato.texto(t.cuposDisponibles())} de ${Formato.texto(t.cupoMaximo())}</span>
                            @endif
                        </td>
                        <td><a class="tl-link" href="/talleres/${Formato.texto(t.id())}">Ver detalle</a></td>
                    </tr>
                @endfor
                </tbody>
            </table>
        @endif
    </section>
`
)
