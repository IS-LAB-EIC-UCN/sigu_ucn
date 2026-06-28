@param String usuarioNombre
@param String error
@param String titulo
@param String autor
@param String categoria
@param String isbn

@template.layout.main(
title = "Registrar Libro - SIGU-UCN",
usuarioNombre = usuarioNombre,
content = @`
    <link rel="stylesheet" href="/css/biblioteca.css">
    <script src="/js/biblioteca.js"></script>

    <div class="bib-container">
        <p><a href="/biblioteca/libros" class="bib-action-link">&larr; Volver al catálogo</a></p>

        <h1>Registrar nuevo libro</h1>

        @if(error != null)
            <div class="bib-alert bib-alert-error">${error}</div>
        @endif

        <div class="bib-card">
            <form method="post" action="/biblioteca/libros/nuevo">
                <div class="bib-form-group">
                    <label>Título</label>
                    <input type="text" name="titulo" class="bib-form-input" placeholder="Ej: Estructuras de Datos" value="${titulo != null ? titulo : ""}" required/>
                </div>
                <div class="bib-form-group">
                    <label>Autor</label>
                    <input type="text" name="autor" class="bib-form-input" placeholder="Ej: Alfred V. Aho" value="${autor != null ? autor : ""}" required/>
                </div>
                <div class="bib-form-group">
                    <label>Categoría</label>
                    <input type="text" name="categoria" class="bib-form-input" placeholder="Ej: Programacion" value="${categoria != null ? categoria : ""}" required/>
                </div>
                <div class="bib-form-group">
                    <label>ISBN</label>
                    <input type="text" name="isbn" id="isbnInput" class="bib-form-input" placeholder="978-0-201-00023-8" value="${isbn != null ? isbn : ""}" maxlength="17" required oninput="formatIsbn(this)"/>
                </div>
                <script>
                    function formatIsbn(input) {
                        let v = input.value.replace(/[^0-9Xx]/g, '');
                        if (v.length > 3) v = v.slice(0,3) + '-' + v.slice(3);
                        if (v.length > 7) v = v.slice(0,7) + '-' + v.slice(7);
                        if (v.length > 11) v = v.slice(0,11) + '-' + v.slice(11);
                        if (v.length > 15) v = v.slice(0,15) + '-' + v.slice(15);
                        input.value = v;
                    }
                </script>
                <button type="submit" class="bib-btn bib-btn-primary">Registrar libro</button>
            </form>
        </div>
    </div>
`
)
