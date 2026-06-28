document.addEventListener("DOMContentLoaded", () => {
    // Autofocus en el campo de busqueda si existe
    const searchInput = document.getElementById("bib-search-input");
    if (searchInput) {
        searchInput.focus();
    }

    // Filtrar tabla en vivo
    const liveSearch = document.getElementById("bib-live-search");
    if (liveSearch) {
        liveSearch.addEventListener("input", (e) => {
            const term = e.target.value.toLowerCase().trim();
            const rows = document.querySelectorAll(".bib-table tbody tr");
            rows.forEach(row => {
                const text = row.textContent.toLowerCase();
                if (text.includes(term)) {
                    row.style.display = "";
                } else {
                    row.style.display = "none";
                }
            });
        });
    }

    // Confirmacion personalizada para eliminaciones
    const deleteButtons = document.querySelectorAll(".bib-confirm-delete");
    deleteButtons.forEach(btn => {
        btn.addEventListener("click", (e) => {
            const message = btn.getAttribute("data-message") || "¿Está seguro de que desea eliminar este elemento?";
            if (!confirm(message)) {
                e.preventDefault();
            }
        });
    });
});
