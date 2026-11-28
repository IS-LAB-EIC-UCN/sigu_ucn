@import java.util.List
@import cl.ucn.app.service.ReservaEstacionamientoService.ResumenReserva

@param String usuarioNombre
@param List<ResumenReserva> reservas
@param String mensajeCancelacion
@param String errorCancelacion

@template.layout.main(
    title = "Administrar reservas de estacionamiento",
    usuarioNombre = usuarioNombre,
    content = @`
        <section class="dashboard">
            <h1>Administrar reservas de estacionamiento</h1>
            <p><a href="/estacionamientos/admin">Volver al catálogo de espacios</a></p>

            <section class="info-card" style="overflow-x:auto;">
                @template.estacionamientos.reservas-listado(
                    reservas = reservas,
                    mostrarPropietario = true,
                    rutaCancelacion = "/estacionamientos/admin/reservas",
                    mensajeCancelacion = mensajeCancelacion,
                    errorCancelacion = errorCancelacion
                )
            </section>
        </section>
    `
)
