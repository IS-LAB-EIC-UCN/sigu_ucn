@param String title
@param String usuarioNombre
@param String usuarioRol
@param java.util.List<cl.ucn.app.model.Espacio> espacios
@param java.util.Map<Long, java.util.List<Integer>> puestosOcupados

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <section class="dashboard">

        <h1>Estacionamientos disponibles</h1>

        <p>Vista general de los espacios de estacionamiento registrados en el sistema.</p>

        @if(usuarioRol.equals("ADMIN"))
            <p>
                <a href="/estacionamientos/nuevo">
                    Nuevo estacionamiento
                </a>
            </p>
        @endif

        <div class="card-grid">

            @for(var espacio : espacios)

                <div class="info-card">

                    <h3>${espacio.getNombre()}</h3>

                    <p>
                        <strong>Tipo:</strong> ${espacio.getTipo()}
                    </p>

                    <p>
                        <strong>Capacidad:</strong> ${espacio.getCapacidad()} vehículos
                    </p>

                    <div class="parking-grid">

                        @for(int i = 1; i <= espacio.getCapacidad() && i <= 20; i++)

                            @if(
                                puestosOcupados.containsKey(espacio.getId())
                                && puestosOcupados.get(espacio.getId()).contains(i)
                            )

                                <span class="parking-slot ocupado"
                                      title="Puesto ${i} ocupado">
                                    🚗
                                </span>

                            @else

                                <a class="parking-slot disponible"
                                   href="/reservas/estacionamientos?espacioId=${espacio.getId()}&puesto=${i}"
                                   title="Seleccionar puesto ${i} en ${espacio.getNombre()}">
                                    🅿️
                                </a>

                            @endif

                        @endfor

                    </div>

                    @if(espacio.getCapacidad() > 20)
                        <p class="small-text">
                            Se muestran 20 de ${espacio.getCapacidad()} cupos.
                        </p>
                    @endif

                    @if(usuarioRol.equals("ADMIN"))
                        <p>
                            <a href="/estacionamientos/editar/${espacio.getId()}">
                                Editar
                            </a>
                            |
                            <a href="/estacionamientos/eliminar/${espacio.getId()}">
                                Eliminar
                            </a>
                        </p>
                    @else
                        <p class="small-text">
                            Seleccione un puesto libre (🅿️) para reservar.
                        </p>
                    @endif

                </div>

            @endfor

        </div>

    </section>
`
)