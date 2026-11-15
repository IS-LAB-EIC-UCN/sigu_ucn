
document.addEventListener("DOMContentLoaded", () => {

    const formulario = document.getElementById("formTicket");
    const mensaje = document.getElementById("mensajeTicket");

    const tickets = document.getElementById("ticketPendiente");
    const tecnicos = document.getElementById("tecnicoAsignacion");
    const btnAsignar = document.getElementById("btnAsignarTicket");
    const mensajeAsignacion =
        document.getElementById("mensajeAsignacion");

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

    cargarDatosAsignacion();
});
