@import gg.jte.Content
@import java.util.List
@import java.util.Map
@import cl.ucn.app.model.Espacio
@import cl.ucn.app.model.Reserva
@import cl.ucn.app.model.RegistroEstacionamiento
@import cl.ucn.app.model.Vehiculo

@param String title
@param String usuarioNombre
@param String usuarioRol
@param List<Espacio> espacios
@param Map<Long, List<Integer>> puestosOcupados
@param List<Reserva> misReservas
@param List<Reserva> reservasPendientes
@param List<RegistroEstacionamiento> registros
@param String usuarioFiltro
@param String patenteFiltro
@param String fechaFiltro

@template.layout.main(
title = title,
usuarioNombre = usuarioNombre,
content = @`

<%-- ════════════════════════════════════════════════════════
     MÓDULO ESTACIONAMIENTOS — Panel raíz con grid de cards
     Grupo 9 · SIGU-UCN
     ════════════════════════════════════════════════════════ --%>

<section class="dashboard ep-shell">

    <%-- ──────────────────────────────────────────────────
         CABECERA DEL MÓDULO (siempre visible)
         ────────────────────────────────────────────────── --%>
    <div class="ep-header">
        <div class="ep-header__text">
            <h1 class="ep-header__title">Gestión de Estacionamientos</h1>
            <p class="ep-header__sub">
                @if(usuarioRol.equals("ADMIN"))
                    Administración del módulo — acceso completo.
                @else
                    Panel de usuario — gestione sus reservas e historial.
                @endif
            </p>
        </div>
        <span class="ep-role-badge ep-role-badge--${usuarioRol.toLowerCase()}">${usuarioRol}</span>
    </div>

    <%-- ──────────────────────────────────────────────────
         CAPA A: GRID DE CARDS (menú raíz)
         Se oculta al seleccionar una función.
         ────────────────────────────────────────────────── --%>
    <div id="ep-menu" class="ep-menu">

        <p class="ep-menu__hint">Seleccione una función para continuar.</p>
        <div class="card-grid">

            <%-- Card: Disponibilidad — visible para TODOS --%>
            <button class="info-card ep-card ep-card-btn" data-view="view-mapa" type="button">
                <h3>Disponibilidad</h3>
                <p>Consulte los espacios y puestos disponibles.</p>
            </button>

            @if(usuarioRol.equals("ADMIN"))

                <%-- Card: Registrar usuario/vehículo — solo Admin --%>
                <button class="info-card ep-card ep-card-btn" data-view="view-registrar" type="button">
                    <h3>Registrar Usuario / Vehículo</h3>
                    <p>Cree cuentas de Docente o Estudiante con vehículo.</p>
                </button>

                <%-- Card: Reservas pendientes — solo Admin --%>
                <button class="info-card ep-card ep-card-btn" data-view="view-pendientes" type="button">
                    <h3>
                        Reservas Pendientes
                        @if(!reservasPendientes.isEmpty())
                            <span class="ep-card__badge">${reservasPendientes.size()} pendiente(s)</span>
                        @endif
                    </h3>
                    <p>Apruebe o rechace solicitudes de reserva.</p>
                </button>

                <%-- Card: Historial global — solo Admin --%>
                <button class="info-card ep-card ep-card-btn" data-view="view-historial" type="button">
                    <h3>Historial Global</h3>
                    <p>Revise todos los registros de ingreso y salida.</p>
                </button>

            @else

                <%-- Card: Mis reservas — Docente / Estudiante --%>
                <button class="info-card ep-card ep-card-btn" data-view="view-mis-reservas" type="button">
                    <h3>
                        Mis Reservas
                        @if(!misReservas.isEmpty())
                            <span class="ep-card__badge">${misReservas.size()} reserva(s)</span>
                        @endif
                    </h3>
                    <p>Consulte, cree o cancele sus reservas.</p>
                </button>

                <%-- Card: Mi historial — Docente / Estudiante --%>
                <button class="info-card ep-card ep-card-btn" data-view="view-historial" type="button">
                    <h3>Mi Historial</h3>
                    <p>Revise sus registros de ingreso y salida.</p>
                </button>

            @endif

        </div>
                
                
        
        
    </div><%-- /ep-menu --%>


    <%-- ══════════════════════════════════════════════════════════
         CAPA B: VISTAS FUNCIONALES
         Ocultas por defecto. El JS activa una a la vez.
         Cada una tiene su propio botón "Volver al menú".
         ══════════════════════════════════════════════════════════ --%>

    <%-- ────────────────────────────────────────────────────
         VISTA 1 — DISPONIBILIDAD DE ESPACIOS
         ──────────────────────────────────────────────────── --%>
    <div id="view-mapa" class="ep-view" hidden>

        <div class="ep-view__toolbar">
            <button class="ep-back-btn" data-back type="button">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                     stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                    <polyline points="15 18 9 12 15 6"/>
                </svg>
                Volver al menú
            </button>
            <h2 class="ep-view__title">Disponibilidad de Espacios</h2>
            @if(usuarioRol.equals("ADMIN"))
                <a href="/estacionamientos/nuevo" class="btn-action btn-action--primary btn-action--sm">
                    Nuevo espacio
                </a>
            @endif
        </div>

        @if(espacios.isEmpty())
            <div class="ep-empty">No hay espacios de estacionamiento registrados.</div>
        @else
            <div class="card-grid">
                @for(var espacio : espacios)
                    <div class="info-card espacio-card">

                        <div class="espacio-card__head">
                            <h3>${espacio.getNombre()}</h3>
                            <span class="espacio-tipo-badge">${espacio.getTipo()}</span>
                        </div>

                        <p class="espacio-capacidad">
                            Capacidad: <strong>${espacio.getCapacidad()} vehículos</strong>
                        </p>

                        <div class="parking-grid">
                            @for(int i = 1; i <= espacio.getCapacidad() && i <= 20; i++)
                                @if(
                                    puestosOcupados.containsKey(espacio.getId())
                                    && puestosOcupados.get(espacio.getId()).contains(i)
                                )
                                    <span class="parking-slot ocupado" title="Puesto ${i} — ocupado">${i}</span>
                                @else
                                    <a class="parking-slot disponible"
                                       href="/reservas/estacionamientos?espacioId=${espacio.getId()}&puesto=${i}"
                                       title="Puesto ${i} — disponible. Clic para reservar.">${i}</a>
                                @endif
                            @endfor
                        </div>

                        @if(espacio.getCapacidad() > 20)
                            <p class="small-text">Mostrando 20 de ${espacio.getCapacidad()} puestos.</p>
                        @endif

                        @if(usuarioRol.equals("ADMIN"))
                            <div class="espacio-card__actions">
                                <a href="/estacionamientos/editar/${espacio.getId()}"
                                   class="btn-action btn-action--secondary btn-action--sm">Editar</a>
                                <a href="/estacionamientos/eliminar/${espacio.getId()}"
                                   class="btn-action btn-action--danger btn-action--sm"
                                   data-confirm="¿Eliminar este espacio? La acción no se puede deshacer.">
                                   Eliminar</a>
                            </div>
                        @else
                            <p class="small-text">Haga clic en un puesto disponible para reservar.</p>
                        @endif

                    </div>
                @endfor
            </div>
        @endif

    </div><%-- /view-mapa --%>


    <%-- ────────────────────────────────────────────────────
         VISTA 2-A — REGISTRAR USUARIO Y VEHÍCULO (Admin)
         ──────────────────────────────────────────────────── --%>
    @if(usuarioRol.equals("ADMIN"))

    <div id="view-registrar" class="ep-view" hidden>

        <div class="ep-view__toolbar">
            <button class="ep-back-btn" data-back type="button">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                     stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                    <polyline points="15 18 9 12 15 6"/>
                </svg>
                Volver al menú
            </button>
            <h2 class="ep-view__title">Registrar Usuario y Vehículo</h2>
        </div>

        <p class="ep-view__desc">
            Complete el formulario para crear una cuenta de Docente o Estudiante
            con su vehículo asociado. Los datos solo se guardarán al hacer clic en
            <strong>Registrar</strong>.
        </p>

        <%-- action POST real — sin cambios en backend --%>
        <form method="post"
              action="/estacionamientos/usuarios-vehiculos/nuevo"
              class="parking-form"
              id="formRegistrarUsuario"
              autocomplete="off">

            <fieldset class="form-fieldset">
                <legend>Datos del usuario</legend>

                <div class="form-group">
                    <label for="reg-nombre">Nombre completo</label>
                    <input type="text" id="reg-nombre" name="nombre"
                           placeholder="Ej: Juan Pérez" required>
                </div>
                <div class="form-group">
                    <label for="reg-correo">Correo institucional</label>
                    <input type="email" id="reg-correo" name="correo"
                           placeholder="correo@ucn.cl" required>
                </div>
                <div class="form-group">
                    <label for="reg-password">Contraseña</label>
                    <input type="password" id="reg-password" name="password"
                           placeholder="Mínimo 6 caracteres" required>
                </div>
                <div class="form-group">
                    <label for="reg-rol">Rol</label>
                    <select id="reg-rol" name="rol" required>
                        <option value="DOCENTE">Docente</option>
                        <option value="ESTUDIANTE">Estudiante</option>
                    </select>
                </div>
            </fieldset>

            <fieldset class="form-fieldset">
                <legend>Datos del vehículo</legend>

                <div class="form-group">
                    <label for="reg-patente">Patente</label>
                    <input type="text" id="reg-patente" name="patente"
                           maxlength="10" placeholder="Ej: ABCD12" required>
                </div>
                <div class="form-group">
                    <label for="reg-marca">Marca</label>
                    <input type="text" id="reg-marca" name="marca"
                           placeholder="Ej: Toyota" required>
                </div>
                <div class="form-group">
                    <label for="reg-modelo">Modelo</label>
                    <input type="text" id="reg-modelo" name="modelo"
                           placeholder="Ej: Corolla" required>
                </div>
            </fieldset>

            <div class="form-actions">
                <button type="submit" class="btn-action btn-action--primary">
                    Registrar usuario y vehículo
                </button>
                <button type="reset" class="btn-action btn-action--secondary">
                    Limpiar formulario
                </button>
            </div>

        </form>

    </div><%-- /view-registrar --%>

    @endif


    <%-- ────────────────────────────────────────────────────
         VISTA 2-B — MIS RESERVAS (Docente / Estudiante)
         ──────────────────────────────────────────────────── --%>
    @if(!usuarioRol.equals("ADMIN"))

    <div id="view-mis-reservas" class="ep-view" hidden>

        <div class="ep-view__toolbar">
            <button class="ep-back-btn" data-back type="button">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                     stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                    <polyline points="15 18 9 12 15 6"/>
                </svg>
                Volver al menú
            </button>
            <h2 class="ep-view__title">Mis Reservas</h2>
            <a href="/estacionamientos/panel#view-mapa"
               class="btn-action btn-action--primary btn-action--sm">
                Nueva reserva
            </a>
        </div>

        @if(misReservas.isEmpty())
            <div class="ep-empty">
                <p>No tienes reservas registradas.</p>
                <p>Ingresa a <strong>Disponibilidad</strong> y selecciona un puesto libre.</p>
            </div>
        @else
            <div class="table-responsive">
                <table class="table">
                    <thead>
                    <tr>
                        <th>ID</th><th>Espacio</th><th>Puesto</th><th>Vehículo</th>
                        <th>Fecha</th><th>Inicio</th><th>Fin</th><th>Estado</th><th>Acciones</th>
                    </tr>
                    </thead>
                    <tbody>
                    @for(var reserva : misReservas)
                        <tr>
                            <td>${reserva.getId()}</td>
                            <td>${reserva.getEspacio().getNombre()}</td>
                            <td>${reserva.getPuestoNumero()}</td>
                            <td>
                                @if(reserva.getVehiculo() != null)
                                    ${reserva.getVehiculo().getPatente()}
                                @else —
                                @endif
                            </td>
                            <td>${reserva.getFechaReserva().toString()}</td>
                            <td>${reserva.getHoraInicio().toString()}</td>
                            <td>${reserva.getHoraFin().toString()}</td>
                            <td>
                                <span class="badge badge-${reserva.getEstado().toLowerCase()}">
                                    ${reserva.getEstado()}
                                </span>
                            </td>
                            <td>
                                @if(reserva.getEstado().equals("PENDIENTE") || reserva.getEstado().equals("APROBADA"))
                                    <a href="/mis-reservas/estacionamientos/cancelar/${reserva.getId()}"
                                       class="btn-action btn-action--danger btn-action--sm"
                                       data-confirm="¿Cancelar esta reserva?">Cancelar</a>
                                @elseif(reserva.getEstado().equals("ACTIVA"))
                                    <span class="status-text status-text--active">En uso</span>
                                @elseif(reserva.getEstado().equals("FINALIZADA"))
                                    <span class="status-text">Finalizada</span>
                                @else
                                    <span class="status-text">—</span>
                                @endif
                            </td>
                        </tr>
                    @endfor
                    </tbody>
                </table>
            </div>
        @endif

    </div><%-- /view-mis-reservas --%>

    @endif


    <%-- ────────────────────────────────────────────────────
         VISTA 3 — RESERVAS PENDIENTES (Admin)
         ──────────────────────────────────────────────────── --%>
    @if(usuarioRol.equals("ADMIN"))

    <div id="view-pendientes" class="ep-view" hidden>

        <div class="ep-view__toolbar">
            <button class="ep-back-btn" data-back type="button">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                     stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                    <polyline points="15 18 9 12 15 6"/>
                </svg>
                Volver al menú
            </button>
            <h2 class="ep-view__title">Reservas Pendientes</h2>
        </div>

        @if(reservasPendientes.isEmpty())
            <div class="ep-empty">No hay reservas pendientes ni aprobadas en este momento.</div>
        @else
            <div class="table-responsive">
                <table class="table">
                    <thead>
                    <tr>
                        <th>ID</th><th>Usuario</th><th>Espacio</th><th>Puesto</th>
                        <th>Vehículo</th><th>Fecha</th><th>Inicio</th><th>Fin</th>
                        <th>Estado</th><th>Acciones</th>
                    </tr>
                    </thead>
                    <tbody>
                    @for(var reserva : reservasPendientes)
                        <tr>
                            <td>${reserva.getId()}</td>
                            <td>${reserva.getUsuario().getNombre()}</td>
                            <td>${reserva.getEspacio().getNombre()}</td>
                            <td>${reserva.getPuestoNumero()}</td>
                            <td>
                                @if(reserva.getVehiculo() != null)
                                    ${reserva.getVehiculo().getPatente()}
                                @else —
                                @endif
                            </td>
                            <td>${reserva.getFechaReserva().toString()}</td>
                            <td>${reserva.getHoraInicio().toString()}</td>
                            <td>${reserva.getHoraFin().toString()}</td>
                            <td>
                                <span class="badge badge-${reserva.getEstado().toLowerCase()}">
                                    ${reserva.getEstado()}
                                </span>
                            </td>
                            <td class="actions-cell">
                                @if(reserva.getEstado().equals("PENDIENTE"))
                                    <form method="post"
                                          action="/estacionamientos/reservas-pendientes/aprobar/${reserva.getId()}"
                                          style="display:inline;">
                                        <button type="submit"
                                                class="btn-action btn-action--success btn-action--sm">
                                            Aprobar
                                        </button>
                                    </form>
                                    <form method="post"
                                          action="/estacionamientos/reservas-pendientes/rechazar/${reserva.getId()}"
                                          style="display:inline;">
                                        <button type="submit"
                                                class="btn-action btn-action--danger btn-action--sm">
                                            Rechazar
                                        </button>
                                    </form>
                                @elseif(reserva.getEstado().equals("APROBADA"))
                                    <form method="post"
                                          action="/mis-reservas/estacionamientos/ingreso/${reserva.getId()}"
                                          style="display:inline;">
                                        <button type="submit"
                                                class="btn-action btn-action--primary btn-action--sm">
                                            Registrar ingreso
                                        </button>
                                    </form>
                                @elseif(reserva.getEstado().equals("ACTIVA"))
                                    <span class="status-text status-text--active">En uso</span>
                                @else
                                    <span class="status-text">—</span>
                                @endif
                            </td>
                        </tr>
                    @endfor
                    </tbody>
                </table>
            </div>
        @endif

    </div><%-- /view-pendientes --%>

    @endif


    <%-- ────────────────────────────────────────────────────
         VISTA 4 — HISTORIAL
         Admin: global con filtros | Usuario: personal
         ──────────────────────────────────────────────────── --%>
    <div id="view-historial" class="ep-view" hidden>

        <div class="ep-view__toolbar">
            <button class="ep-back-btn" data-back type="button">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                     stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                    <polyline points="15 18 9 12 15 6"/>
                </svg>
                Volver al menú
            </button>
            <h2 class="ep-view__title">
                @if(usuarioRol.equals("ADMIN")) Historial Global @else Mi Historial @endif
            </h2>
        </div>

        <%-- Filtros — action GET al endpoint existente sin cambios --%>
        <form method="get"
              action="/estacionamientos/historial/filtrar"
              class="filter-form"
              id="formFiltrarHistorial">

            @if(usuarioRol.equals("ADMIN"))
                <div>
                    <label for="hist-usuario">Usuario</label>
                    <input type="text" id="hist-usuario" name="usuario"
                           value="${usuarioFiltro != null ? usuarioFiltro : ""}">
                </div>
            @endif

            <div>
                <label for="hist-patente">Patente</label>
                <input type="text" id="hist-patente" name="patente"
                       value="${patenteFiltro != null ? patenteFiltro : ""}">
            </div>

            <div>
                <label for="hist-fecha">Fecha</label>
                <input type="date" id="hist-fecha" name="fecha"
                       value="${fechaFiltro != null ? fechaFiltro : ""}">
            </div>

            <button type="submit" class="btn-action btn-action--primary">Filtrar</button>
            <a href="/estacionamientos/panel" class="btn-action btn-action--secondary">Limpiar</a>

        </form>

        @if(registros.isEmpty())
            <div class="ep-empty">No hay registros que coincidan con los filtros aplicados.</div>
        @else
            <div class="table-responsive">
                <table class="table">
                    <thead>
                    <tr>
                        <th>ID</th><th>Reserva</th><th>Usuario</th><th>Espacio</th>
                        <th>Vehículo</th><th>Ingreso</th><th>Salida</th>
                        <th>Tiempo (min)</th><th>Estado</th>
                        @if(usuarioRol.equals("ADMIN"))<th>Acción</th>@endif
                    </tr>
                    </thead>
                    <tbody>
                    @for(var registro : registros)
                        <tr>
                            <td>${registro.getId()}</td>
                            <td>${registro.getReserva().getId()}</td>
                            <td>${registro.getReserva().getUsuario().getNombre()}</td>
                            <td>${registro.getReserva().getEspacio().getNombre()}</td>
                            <td>
                                @if(registro.getReserva().getVehiculo() != null)
                                    ${registro.getReserva().getVehiculo().getPatente()}
                                @else —
                                @endif
                            </td>
                            <td>${registro.getFechaHoraIngreso().toString()}</td>
                            <td>
                                @if(registro.getFechaHoraSalida() != null)
                                    ${registro.getFechaHoraSalida().toString()}
                                @else
                                    <span class="status-text status-text--active">Pendiente</span>
                                @endif
                            </td>
                            <td>
                                @if(registro.getTiempoUsoMinutos() != null)
                                    ${registro.getTiempoUsoMinutos()} min
                                @else
                                    <span class="status-text status-text--active">En uso</span>
                                @endif
                            </td>
                            <td>
                                <span class="badge badge-${registro.getEstado().toLowerCase()}">
                                    ${registro.getEstado()}
                                </span>
                            </td>
                            @if(usuarioRol.equals("ADMIN"))
                                <td>
                                    @if(registro.getEstado().equals("EN_USO"))
                                        <form method="post"
                                              action="/estacionamientos/salida/${registro.getId()}"
                                              style="display:inline;">
                                            <button type="submit"
                                                    class="btn-action btn-action--warning btn-action--sm">
                                                Registrar salida
                                            </button>
                                        </form>
                                    @else
                                        <span class="status-text">—</span>
                                    @endif
                                </td>
                            @endif
                        </tr>
                    @endfor
                    </tbody>
                </table>
            </div>
        @endif

    </div><%-- /view-historial --%>

</section><%-- /ep-shell --%>

<%-- Script de gestión de estado — se carga solo en esta vista --%>
<script src="/js/parking-panel.js"></script>

`
)
