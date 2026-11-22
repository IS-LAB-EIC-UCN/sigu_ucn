document.addEventListener("DOMContentLoaded", () => {
    const form = document.querySelector(".login-form");
    if (form) form.addEventListener("submit", () => {
        const correo = form.querySelector('[name="correo"]');
        correo.value = correo.value.trim().toLowerCase();
    });
});

document.addEventListener("DOMContentLoaded", () => {
    const current = new URL(window.location.href);
    document.querySelectorAll(".eventos-nav a").forEach(link => {
        const target = new URL(link.href);
        if (target.pathname === current.pathname && target.searchParams.get("vista") === current.searchParams.get("vista")) {
            link.classList.add("activo"); link.setAttribute("aria-current", "page");
        }
    });
    document.querySelectorAll("[data-imprimir]").forEach(button => button.addEventListener("click", () => window.print()));
});
