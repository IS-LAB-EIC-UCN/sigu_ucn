
@param String title
@param String usuarioNombre
@param String csrfToken

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
                            <label for="descripcion"> Descripción </label>
                            <textarea
                                id="descripcion" name="descripcion" rows="5" required
                                style="width:100%;
                                       padding:10px;
                                       border:1px solid #c9d2df;
                                       border-radius:8px;"
                                placeholder="Describa el problema"
                            ></textarea>
                        </div>

                        <button type="submit" class="primary-btn" style="background:#1f3b73;color:white;">
                            Crear ticket
                        </button>

                        <p id="mensajeTicket" role="status" aria-live="polite"></p>
                    </form>
                </div>

                <div class="info-card">
                    <h3>Tickets registrados</h3>
                    <p>
                        Aquí se mostrarán los tickets
                        registrados por el usuario.
                    </p>

                    <p>
                        El listado se implementará
                        en una siguiente etapa.
                    </p>
                </div>
            </div>
        </section>

        <script src="/js/soporte/tickets.js" defer></script>
    `
)