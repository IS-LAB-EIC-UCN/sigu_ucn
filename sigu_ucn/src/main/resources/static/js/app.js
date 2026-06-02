$(document).ready(function () {
    $("#loginForm").on("submit", function () {
        const correo = $("#correo").val().trim();
        const password = $("#password").val().trim();

        if (correo === "" || password === "") {
            alert("Debe ingresar correo y contraseña.");
            return false;
        }
    });
});