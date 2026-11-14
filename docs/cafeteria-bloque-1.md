# Cafetería: bloque 1 (grupo 7)

## Ubicación y organización

El proyecto es común a todos los equipos y está organizado por capas. El grupo 7 trabaja en la rama `grupo-7`, utilizando los paquetes existentes bajo `src/main/java/cl/ucn/app/`:

| Carpeta | Responsabilidad | Ejemplos de cafetería |
| --- | --- | --- |
| `model/` | Entidades persistentes | `Categoria`, `Producto` |
| `repository/` | Consultas y escritura en PostgreSQL | `CategoriaRepository`, `ProductoRepository` |
| `service/` | Validaciones y reglas de negocio | `CategoriaService`, `ProductoService` |
| `controller/` y `routes/` | Formularios, sesión, permisos y rutas | Pendientes del bloque 3 |

Las vistas de cafetería corresponden a `src/main/resources/jte/cafeteria/` (bloque 4). Los scripts SQL del módulo ya están en `db/init/03_cafeteria_schema.sql` y `db/init/04_cafeteria_seed.sql`; Docker monta `db/init/` desde la raíz.

## Métodos disponibles

| Servicio y método | Comportamiento | Trazabilidad |
| --- | --- | --- |
| `CategoriaService.listarCategorias()` | Consulta las categorías; puede devolver una lista vacía. | RF02 |
| `CategoriaService.registrarCategoria(nombre, descripcion)` | Rechaza nombres vacíos o repetidos; la descripción es opcional. | RF02, CU09 |
| `ProductoService.listarProductos()` | Consulta productos y cantidades, incluyendo los agotados. | RF10, CU06 |
| `ProductoService.registrarProducto(nombre, precio, stock, categoriaId)` | Exige nombre, precio y stock no negativos y categoría existente. | RF01, RN05, CU01 |
| `ProductoService.actualizarStock(productoId, nuevoStock)` | Reemplaza el stock de un producto existente por una cantidad no negativa. | RF10, RN05, CU05 |

Los nombres se guardan sin espacios en los extremos y con un máximo de 100 caracteres, de acuerdo con las entidades existentes. La búsqueda de categorías duplicadas utiliza `findByNombre`, que compara sin distinguir mayúsculas y minúsculas.

El precio se recibe como `BigDecimal`, el stock como `Integer` y los identificadores como `Long`. Los métodos de registro y actualización devuelven la entidad resultante. Los errores de validación lanzan `IllegalArgumentException` con mensajes en español; el controlador del bloque 3 debe capturarlos y mostrarlos, además de controlar sesión y permisos de administrador.

Ejemplo para integrar desde el controlador:

```java
CategoriaService categorias = new CategoriaService();
ProductoService productos = new ProductoService();
Categoria categoria = categorias.registrarCategoria("Bebidas", "Frías y calientes");
Producto producto = productos.registrarProducto(
        "Café", new BigDecimal("1500.00"), 10, categoria.getId());
productos.actualizarStock(producto.getId(), 15); // El stock queda en 15.
```

El descuento y la restitución de stock por pedidos corresponden al bloque 2 y deben coordinarse con la persistencia del pedido en una misma transacción. `actualizarStock` representa el ajuste manual de CU05.

## Pruebas

Ejecutar `mvn test` desde la raíz, o la opción `test` del panel Maven de IntelliJ. El POM requiere Java 21 o superior.

Las pruebas de `CategoriaServiceTest` y `ProductoServiceTest` utilizan JUnit 5 y Mockito para comprobar registros válidos, valores cero, datos obligatorios, negativos, duplicados, referencias inexistentes, consultas vacías y reemplazo de stock. No necesitan PostgreSQL: verifican los servicios usando repositorios simulados.

Para ejecutar también las pruebas reales de `CatalogoPostgresTest`, iniciar PostgreSQL y habilitarlas explícitamente:

```powershell
docker compose up -d postgres
mvn "-Dcafeteria.integration=true" test
```

Estas tres pruebas verifican el registro y consulta de categorías, el rechazo de duplicados sin distinguir mayúsculas, la persistencia de productos con precio decimal y categoría, y la actualización de stock. Crean categorías con nombres únicos y eliminan únicamente sus propios productos y categorías al finalizar. Sin la propiedad `cafeteria.integration`, se omiten y las pruebas unitarias siguen funcionando sin base de datos.

Verificación del 14/11/2026: compilación correcta y **38 casos aprobados** (10 de categorías, 25 de productos y 3 de integración con PostgreSQL 16), con cero fallos, cero errores y cero omitidos. Se utilizó Maven de IntelliJ con Java 25 y el objetivo Java 21 definido por el proyecto.

Mockito usa `mock-maker-subclass` en los recursos de pruebas porque los repositorios son clases ordinarias y no requieren instrumentación de clases del JDK. Esta configuración evita la incompatibilidad del motor inline incluido con Java 25; no admite simular clases o métodos finales.

## Entorno local cuando el puerto 5432 está ocupado

En el equipo usado para verificar, PostgreSQL 18 de Windows ya utiliza el puerto 5432. Para mantenerlo funcionando y usar PostgreSQL 16 de Docker en paralelo:

```powershell
$env:SIGU_DB_PORT = "55432"
$env:SIGU_DB_URL = "jdbc:postgresql://localhost:55432/sigu_ucn"
docker compose up -d postgres
mvn "-Dcafeteria.integration=true" test
```

`SIGU_DB_PORT` cambia el puerto publicado por Docker. `SIGU_DB_URL` configura la conexión Java. También se puede pasar la opción `-Dsigu.db.url=jdbc:postgresql://localhost:55432/sigu_ucn`; esta tiene prioridad sobre la variable de entorno. Si no se especifica ninguna conexión alternativa, se conserva la de `persistence.xml`, y Docker usa 5432 por defecto.

Las variables anteriores afectan solo a esa sesión de PowerShell y sus procesos hijos. Para ejecutar `cl.ucn.app.main.Main` desde IntelliJ en este equipo, agregar `SIGU_DB_URL=jdbc:postgresql://localhost:55432/sigu_ucn` en las variables de entorno de su configuración de ejecución. Al volver a levantar Docker desde otra terminal, establecer también `SIGU_DB_PORT=55432`.

La aplicación escucha en `http://localhost:7000/login`. Cuenta de demostración incluida por el proyecto: `admin@sigu.cl` / `admin123`.

### Evidencia de verificación local

| Comprobación | Resultado |
| --- | --- |
| Compilación y pruebas con PostgreSQL | 38 aprobadas |
| `GET /login` | HTTP 200 |
| Login con campos vacíos | HTTP 400 |
| Login con contraseña incorrecta | HTTP 401 |
| Login válido y consulta de `/home` con sesión | HTTP 200, panel principal |
| Datos tras las pruebas | 4 categorías y 6 productos iniciales; 0 categorías temporales |

Los reportes se generan en `target/surefire-reports/`. La pantalla de cafetería aún depende de los bloques 3 y 4; el bloque 1 se verifica a través de sus servicios y repositorios reales, no mediante formularios que todavía no existen.

## Trabajo compartido y PR

Según el README del repositorio, cada equipo trabaja en su rama asignada y solicita integración hacia `develop`. El flujo de este grupo es `grupo-7 → PR → develop`; no se trabaja directamente en `main` ni en `develop`.

Antes de empezar, actualizar `grupo-7` con `git pull --ff-only origin grupo-7`. Coordinar quién modifica cada archivo, revisar los cambios, ejecutar las pruebas y luego hacer commit y push. Si otro compañero subió cambios durante el trabajo, integrarlos y resolver cualquier conflicto antes de subir. No utilizar push forzado para reemplazar el trabajo del equipo.

Este bloque agrega servicios y pruebas; su pantalla web se incorpora en los bloques 3 y 4. La observación del ayudante sobre representar usuarios como clases con atributos y métodos debe corregirse en el diagrama del entregable 2.
