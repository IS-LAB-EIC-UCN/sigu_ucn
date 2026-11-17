
@param String title
@param String usuarioNombre
@param String csrfToken
@param boolean esAdmin
@param boolean esTecnico

@template.layout.main(
    title = title,
    usuarioNombre = usuarioNombre,
    content = @`
        <section class="dashboard">
            <h1>Gestión de tickets de soporte</h1>
            <p>
                Registre y consulte sus solicitudes
                de soporte técnico universitario.
            </p>

            <div class="card-grid">
                <div class="info-card">
                    <h3>Crear nuevo ticket</h3>

                    <form id="formTicket" method="post" action="/api/soporte/tickets">
                        <input type="hidden" name="csrfToken" value="${csrfToken}">

                        <div class="form-group">
                            <label for="titulo">Título</label>
                            <input type="text" id="titulo" name="titulo" maxlength="150" placeholder="Resumen del problema" required>
                        </div>

                        <div class="form-group">
                            <label for="descripcion">Descripción</label>
                            <textarea id="descripcion" name="descripcion" rows="5" required
                                      style="width:100%;
                                             padding:10px;
                                             border:1px solid #c9d2df;
                                             border-radius:8px;"
                                      placeholder="Describa el problema"
                            ></textarea>
                        </div>

                        <button type="submit" class="primary-btn" style="background:#1f3b73;color:white;"> Crear ticket</button>
                        <p id="mensajeTicket" role="status" aria-live="polite"></p>
                    </form>
                </div>

                <div class="info-card">
                    <h3>Tickets registrados</h3>
                    <p>
                        La consulta general de tickets
                        se incorporará en HU10.
                    </p>
                </div>
            </div>

            @if(esAdmin)
                <div class="info-card" style="margin-top:24px;">
                    <h2>Asignar tickets a técnicos (HU02)</h2>
                    <p>
                        Solo se muestran tickets creados
                        pendientes de asignación.
                    </p>

                    <div class="form-group">
                        <label for="ticketPendiente">Ticket pendiente</label>

                        <select id="ticketPendiente" style="width:100%;padding:10px;">
                            <option value="">Cargando tickets...</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="tecnicoAsignacion"> Técnico de soporte </label>

                        <select id="tecnicoAsignacion" style="width:100%;padding:10px;">
                            <option value=""> Cargando técnicos...</option>
                        </select>
                    </div>

                    <button id="btnAsignarTicket"
                            type="button"
                            class="primary-btn"
                            style="background:#1f3b73;color:white;">
                        Asignar ticket
                    </button>

                    <p id="mensajeAsignacion" role="status" aria-live="polite"></p>
                </div>
            @endif

            @if(esTecnico)
                <div class="info-card" style="margin-top:24px;">
                    <h2>Iniciar resolución (HU03)</h2>
                    <p>
                        Seleccione un ticket asignado a usted para indicar que está trabajando en él.
                    </p>

                    <div class="form-group">
                        <label for="ticketResolucion">Ticket asignado</label>
                        <select id="ticketResolucion" style="width:100%;padding:10px;">
                            <option value="">Cargando tickets...</option>
                        </select>
                    </div>

                    <button id="btnIniciarResolucion"
                            type="button"
                            class="primary-btn"
                            style="background:#1f3b73;color:white;">
                        Iniciar resolución
                    </button>

                    <p id="mensajeResolucion" role="status" aria-live="polite"></p>
                </div>

                <div class="info-card" style="margin-top:24px;">
                    <h2>Cerrar ticket (HU05)</h2>
                    <p>
                        Registre cómo se resolvió el problema para cerrar un ticket en resolución.
                    </p>

                    <div class="form-group">
                        <label for="ticketCierre">Ticket en resolución</label>
                        <select id="ticketCierre" style="width:100%;padding:10px;">
                            <option value="">Cargando tickets...</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="resolucionCierre">Resolución</label>
                        <textarea id="resolucionCierre" rows="4"
                                  style="width:100%;
                                         padding:10px;
                                         border:1px solid #c9d2df;
                                         border-radius:8px;"
                                  placeholder="Describa cómo se resolvió el problema"
                        ></textarea>
                    </div>

                    <button id="btnCerrarTicket"
                            type="button"
                            class="primary-btn"
                            style="background:#1f3b73;color:white;">
                        Cerrar ticket
                    </button>

                    <p id="mensajeCierre" role="status" aria-live="polite"></p>
                </div>
            @endif

        </section>

        <script src="/js/soporte/tickets.js" defer></script>
    `
)