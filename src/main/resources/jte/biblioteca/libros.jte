@import java.util.List
@import cl.ucn.app.model.biblioteca.Libro
@param List<Libro> libros

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Catálogo de Libros</title>
</head>
<body>
    <h1>Catálogo de Libros</h1>
    <a href="/biblioteca/buscar">Buscar libro</a> |
    <a href="/biblioteca/prestamo/nuevo">Solicitar préstamo</a>

    <table border="1">
        <thead>
            <tr>
                <th>Título</th>
                <th>Autor</th>
                <th>Categoría</th>
                <th>ISBN</th>
            </tr>
        </thead>
        <tbody>
            @for(Libro libro : libros)
            <tr>
                <td>${libro.getTitulo()}</td>
                <td>${libro.getAutor()}</td>
                <td>${libro.getCategoria()}</td>
                <td>${libro.getIsbn()}</td>
            </tr>
            @endfor
        </tbody>
    </table>
</body>
</html>