
@param String title
@param String usuarioNombre
@param java.util.List<cl.ucn.app.model.Evento> eventos
@param java.util.Map<Long, Long> cuposDisponibles
@param java.util.Map<Long, Boolean> inscripcionesUsuario
@param String csrfToken

@template.layout.main(
    title = title,
    usuarioNombre = usuarioNombre,
    content = @`
        
        <section class="dashboard eventos-panel">   
            <h1>Gestión de Eventos Académicos</h1>

            <p>Bienvenido al módulo del Grupo 6.</p>

            <div class="card-grid">
                <div class="info-card">
                    <h3>Eventos académicos</h3>
                    <p>Administración de eventos universitarios.</p>
                </div>

                <div class="info-card">
                    <h3>Inscripciones</h3>
                    <p>Gestión de inscripciones de participantes.</p>
                </div>
            </div>
            <p>
                
                <a href="/eventos/nuevo" class="eventos-btn">  
                    + Crear evento académico
                </a>
            </p>
            <h2>Listado de eventos</h2>

            @if(eventos.isEmpty())
                <p>No hay eventos registrados actualmente.</p>
            @else
                <div class="eventos-tabla-contenedor">
                <table class="eventos-tabla">
                    <thead>
                        <tr>
                            <th>Título</th>
                            <th>Fecha</th>
                            <th>Horario</th>
                            <th>Lugar</th>
                            <th>Cupos</th>
                            <th>Disponibles</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        @for(var evento : eventos)
                            <tr>
                                <td>${evento.getTitulo()}</td>
                                <td>${evento.getFecha().toString()}</td>
                                <td>
                                    ${evento.getHoraInicio().toString()}
                                    -
                                    ${evento.getHoraFin().toString()}
                                </td>
                                <td>${evento.getLugar()}</td>
                                <td>${evento.getCupoMaximo()}</td>
                                <td>${cuposDisponibles.getOrDefault(evento.getId(), 0L)}</td>
                                <td>${evento.getEstado()}</td>
                                
                                <td>
                                    <div class="eventos-acciones">


                                                                                
                                        @if("PUBLICADO".equals(evento.getEstado()))
                                            @if(Boolean.TRUE.equals(inscripcionesUsuario.get(evento.getId())))
                                                <span>Ya inscrito</span>
                                            @else
                                                @if(cuposDisponibles.getOrDefault(evento.getId(), 0L) > 0L)
                                                    
                                                <form action="/eventos/${evento.getId()}/inscribir"
                                                    method="post">

                                                    <input type="hidden"
                                                        name="csrfToken"
                                                        value="${csrfToken}" />

                                                    <button type="submit"
                                                            class="eventos-btn">
                                                        Inscribirse
                                                    </button>

                                                </form>

                                                @else
                                                    <span>Cupos agotados</span>
                                                @endif
                                            @endif
                                        @endif



                                        @if(!"CANCELADO".equals(evento.getEstado()))

                                        @if("BORRADOR".equals(evento.getEstado()))
                                            <form action="/eventos/${evento.getId()}/publicar"
                                                method="post"
                                                onsubmit="return confirm('¿Deseas publicar este evento?');">

                                                <button type="submit" class="eventos-btn">
                                                    Publicar
                                                </button>
                                            </form>
                                        @endif


                                            <a href="/eventos/${evento.getId()}/editar"
                                            class="eventos-btn">
                                                Editar
                                            </a>

                                            <form action="/eventos/${evento.getId()}/cancelar"
                                                method="post"
                                                onsubmit="return confirm('¿Seguro que deseas cancelar este evento?');">

                                                <button type="submit"
                                                        class="eventos-btn eventos-btn-cancelar">
                                                    Cancelar
                                                </button>
                                            </form>

                                        @else
                                            <span>Evento cancelado</span>
                                        @endif

                                    </div>
                                </td>

                            </tr>
                        @endfor
                    </tbody>
                </table>
</div>
            @endif
        </section>
    `
)
