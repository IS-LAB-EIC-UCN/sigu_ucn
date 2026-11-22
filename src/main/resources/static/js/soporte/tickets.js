
document.addEventListener("DOMContentLoaded", () => {

    const formulario = document.getElementById("formTicket");
    const mensaje = document.getElementById("mensajeTicket");

    const tickets = document.getElementById("ticketPendiente");
    const tecnicos = document.getElementById("tecnicoAsignacion");
    const btnAsignar = document.getElementById("btnAsignarTicket");
    const mensajeAsignacion =
        document.getElementById("mensajeAsignacion");
    const ticketsResolucion = document.getElementById("ticketResolucion");
    const btnIniciarResolucion =
        document.getElementById("btnIniciarResolucion");
    const mensajeResolucion = document.getElementById("mensajeResolucion");
    const ticketsCierre = document.getElementById("ticketCierre");
    const resolucionCierre = document.getElementById("resolucionCierre");
    const btnCerrarTicket = document.getElementById("btnCerrarTicket");
    const mensajeCierre = document.getElementById("mensajeCierre");

    const csrf = formulario ?.querySelector('[name="csrfToken"]')?.value;

    async function peticion(url, opciones = {}) {
        const respuesta = await fetch(url, {
            credentials: "same-origin", ...opciones
        });

        const resultado = await respuesta.json();

        if (!respuesta.ok) {
            throw new Error(resultado.mensaje || "Error de solicitud.");
        }
        return resultado;
    }

    function informar(elemento, texto, error = false) {
        elemento.textContent = texto;
        elemento.style.color = error ? "#b42318" : "#20744e";
    }

    function completarSelector(
        select, elementos, textoVacio, etiqueta) {
        select.replaceChildren();

        if (!elementos.length) {
            select.add(new Option(textoVacio, ""));
            return;
        }

        select.add(new Option("Seleccione una opción", ""));

        elementos.forEach(elemento => {
            select.add(new Option(etiqueta(elemento), String(elemento.id)
            ));
        });
    }

    async function cargarDatosAsignacion() {
        if (!tickets || !tecnicos) return;

        try {
            const [pendientes, disponibles] = await Promise.all([
                peticion("/api/soporte/tickets/pendientes"),
                peticion("/api/soporte/tecnicos")
            ]);

            completarSelector(tickets, pendientes.datos, "Sin tickets pendientes", ticket => `#${ticket.id} — ${ticket.titulo}`);
            completarSelector(tecnicos, disponibles.datos, "Sin técnicos disponibles", tecnico => `${tecnico.nombre} (#${tecnico.id})`);

        } catch (error) {
            informar(mensajeAsignacion, error.message, true);
        }
    }

    async function cargarTicketsAsignados() {
        if (!ticketsResolucion) return;

        try {
            const resultado = await peticion("/api/soporte/tickets/asignados");
            completarSelector(
                ticketsResolucion,
                resultado.datos,
                "Sin tickets asignados",
                ticket => `#${ticket.id} — ${ticket.titulo}`
            );
        } catch (error) {
            informar(mensajeResolucion, error.message, true);
        }
    }

    async function cargarTicketsEnResolucion() {
        if (!ticketsCierre) return;

        try {
            const resultado = await peticion("/api/soporte/tickets/en-resolucion");
            completarSelector(
                ticketsCierre,
                resultado.datos,
                "Sin tickets en resolución",
                ticket => `#${ticket.id} — ${ticket.titulo}`
            );
        } catch (error) {
            informar(mensajeCierre, error.message, true);
        }
    }

    formulario?.addEventListener("submit", async (evento) => {
        evento.preventDefault();

        const boton = formulario.querySelector('[type="submit"]');
        boton.disabled = true;

        try {
            const datos = await peticion(formulario.action, {method: "POST", body: new FormData(formulario)});
            informar(mensaje, datos.mensaje);
            formulario.reset();
            await cargarDatosAsignacion();

        } catch (error) {
            informar(mensaje, error.message, true);
        } finally {
            boton.disabled = false;
        }
    });

    btnAsignar?.addEventListener("click", async () => {

        if (!tickets.value || !tecnicos.value) {
            informar(mensajeAsignacion, "Seleccione un ticket y un técnico.", true);
            return;
        }

        btnAsignar.disabled = true;

        try {
            const resultado = await peticion(
                `/api/soporte/tickets/${tickets.value}/asignacion`,
                {
                    method: "PUT",
                    headers: {"Content-Type": "application/json", "X-CSRF-Token": csrf},
                    body: JSON.stringify({tecnicoId: Number(tecnicos.value)
                    })
                }
            );

            informar(mensajeAsignacion, resultado.mensaje);
            await cargarDatosAsignacion();

        } catch (error) {
            informar(mensajeAsignacion, error.message, true);
        } finally {
            btnAsignar.disabled = false;
        }
    });

    btnIniciarResolucion?.addEventListener("click", async () => {
        if (!ticketsResolucion.value) {
            informar(mensajeResolucion, "Seleccione un ticket asignado.", true);
            return;
        }

        btnIniciarResolucion.disabled = true;

        try {
            const resultado = await peticion(
                `/api/soporte/tickets/${ticketsResolucion.value}/resolucion`,
                {
                    method: "PUT",
                    headers: {"X-CSRF-Token": csrf}
                }
            );

            informar(mensajeResolucion, resultado.mensaje);
            await Promise.all([cargarTicketsAsignados(), cargarTicketsEnResolucion()]);
        } catch (error) {
            informar(mensajeResolucion, error.message, true);
        } finally {
            btnIniciarResolucion.disabled = false;
        }
    });

    btnCerrarTicket?.addEventListener("click", async () => {
        if (!ticketsCierre.value || !resolucionCierre.value.trim()) {
            informar(mensajeCierre, "Seleccione un ticket y escriba la resolución.", true);
            return;
        }

        btnCerrarTicket.disabled = true;

        try {
            const resultado = await peticion(
                `/api/soporte/tickets/${ticketsCierre.value}/cierre`,
                {
                    method: "PUT",
                    headers: {"Content-Type": "application/json", "X-CSRF-Token": csrf},
                    body: JSON.stringify({resolucion: resolucionCierre.value})
                }
            );

            informar(mensajeCierre, resultado.mensaje);
            resolucionCierre.value = "";
            await cargarTicketsEnResolucion();
        } catch (error) {
            informar(mensajeCierre, error.message, true);
        } finally {
            btnCerrarTicket.disabled = false;
        }
    });

    cargarDatosAsignacion();
    cargarTicketsAsignados();
    cargarTicketsEnResolucion();
});
