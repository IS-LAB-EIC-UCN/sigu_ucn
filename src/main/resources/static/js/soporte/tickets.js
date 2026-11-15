
document.addEventListener("DOMContentLoaded", () => {
    const formulario = document.getElementById("formTicket");
    const mensaje = document.getElementById("mensajeTicket");
    const boton = formulario.querySelector("button[type='submit']");

    formulario.addEventListener("submit", async (evento) => {
        evento.preventDefault();

        boton.disabled = true;
        mensaje.textContent = "Guardando ticket...";

        try {
            const respuesta = await fetch(formulario.action, {
                method: "POST",
                body: new URLSearchParams(
                    new FormData(formulario)
                ),
                headers: {
                    "Accept": "application/json"
                },
                credentials: "same-origin"
            });

            const resultado = await respuesta.json();
            mensaje.textContent = resultado.mensaje;
            mensaje.style.color = respuesta.ok ? "#20744e" : "#b42318";

            if (respuesta.ok) {formulario.reset();}

        } catch (error) {
            mensaje.textContent = "No fue posible completar la solicitud.";
            mensaje.style.color = "#b42318";

            console.error("Error al enviar ticket:", error);

        } finally {
            boton.disabled = false;
        }
    });
});
