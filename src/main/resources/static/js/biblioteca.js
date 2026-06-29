document.addEventListener("DOMContentLoaded", () => {
    const liveSearch = document.getElementById("bib-live-search");
    if (liveSearch) {
        liveSearch.focus();
        liveSearch.addEventListener("input", (e) => {
            const term = e.target.value.toLowerCase().trim();
            
            let tableRows;
            if (document.querySelector(".bib-table-catalog")) {
                tableRows = document.querySelectorAll(".bib-table-catalog tbody tr");
            } else if (document.getElementById("bib-live-search-table")) {
                tableRows = document.querySelectorAll("#bib-live-search-table tbody tr");
            } else {
                // Si no hay tabla específica, buscar en todas
                tableRows = document.querySelectorAll(".bib-table tbody tr");
            }
            
            tableRows.forEach(row => {
                const text = row.textContent.toLowerCase();
                row.style.display = text.includes(term) ? "" : "none";
            });
            
            const cards = document.querySelectorAll(".bib-book-card");
            cards.forEach(card => {
                const text = card.textContent.toLowerCase();
                card.style.display = text.includes(term) ? "" : "none";
            });
        });
    } else {
        const searchInput = document.getElementById("bib-search-input");
        if (searchInput) {
            searchInput.focus();
        }
    }

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
