@param String title
@param String usuarioNombre
@param String usuarioRol
@param java.util.List<cl.ucn.app.model.Espacio> espacios

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`
    <section class="dashboard">

        <h1>Estacionamientos disponibles</h1>

        <p>Listado de espacios de estacionamiento registrados en el sistema.</p>

        @if(usuarioRol.equals("ADMIN"))
            <p>
                <a href="/estacionamientos/nuevo">
                    Nuevo estacionamiento
                </a>
            </p>
        @endif

        <table class="table">

            <thead>
            <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Tipo</th>
                <th>Capacidad</th>
                <th>Estado</th>

                @if(usuarioRol.equals("ADMIN"))
                    <th>Acciones</th>
                @endif

            </tr>
            </thead>

            <tbody>

            @for(var espacio : espacios)

                <tr>
                    <td>${espacio.getId()}</td>
                    <td>${espacio.getNombre()}</td>
                    <td>${espacio.getTipo()}</td>
                    <td>${espacio.getCapacidad()}</td>

                    <td>
                        @if(espacio.getDisponible())
                            Disponible
                        @else
                            Ocupado
                        @endif
                    </td>

                    @if(usuarioRol.equals("ADMIN"))
                        <td>
                            <a href="/estacionamientos/editar/${espacio.getId()}">
                                Editar
                            </a>
                            |
                            <a href="/estacionamientos/eliminar/${espacio.getId()}">
                                Eliminar
                            </a>
                        </td>
                    @endif

                </tr>

            @endfor

            </tbody>

        </table>

    </section>
`
)