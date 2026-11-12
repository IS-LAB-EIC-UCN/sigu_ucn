# SIGU-UCN · Módulo 6: eventos académicos

Módulo del sistema compartido del Taller de Ingeniería de Software. Java 21, Maven, Javalin, JTE, JPA/Hibernate, PostgreSQL 16 y Docker. Rama de trabajo: grupo-6.

## Requisitos

- JDK 21 o superior compatible con el proyecto.
- Maven 3.9 o superior en PATH.
- Docker con Docker Compose.

## Inicio desde un clon nuevo

```powershell
git clone --branch grupo-6 --single-branch https://github.com/IS-LAB-EIC-UCN/sigu_ucn.git
cd sigu_ucn
docker compose up -d
mvn clean compile
mvn exec:java "-Dexec.mainClass=cl.ucn.app.main.Main"
```

Esperar a que PostgreSQL finalice la inicialización antes de iniciar Java. Abrir http://localhost:7000. La aplicación usa el puerto 7000 y PostgreSQL el 5433.

| Usuario demo | Contraseña | Función |
| --- | --- | --- |
| admin@sigu.cl | admin123 | Salas, categorías y supervisión de eventos. |
| docente@sigu.cl | docente123 | Organiza sus eventos y registra su asistencia. |
| estudiante@sigu.cl | estudiante123 | Consulta, se inscribe y cancela antes del inicio. |

Las contraseñas de las semillas están protegidas con PBKDF2. Los usuarios antiguos se migran al iniciar sin cambiar su clave de acceso.

## Base existente: aplicar migraciones sin borrar datos

Los scripts de db/init se ejecutan automáticamente solo cuando se crea un volumen nuevo. Para una base anterior, aplicar 05 y después 07 a 10 en orden:

```powershell
$OutputEncoding = [System.Text.UTF8Encoding]::new()
$archivos = @('05_eventos_catalogos.sql','07_expositores_institucion.sql','08_evento_expositores.sql','09_asistencia.sql','10_categorias.sql')
foreach ($archivo in $archivos) {
    Get-Content -Encoding utf8 (Join-Path 'db/init' $archivo) | docker compose exec -T postgres psql -U postgres -d sigu_ucn -v ON_ERROR_STOP=1
    if ($LASTEXITCODE -ne 0) { throw "Falló la migración $archivo" }
}
```

06_eventos_demo.sql es opcional para cargar los cuatro eventos de ejemplo. Puede repetirse sin duplicarlos. Detener Docker con `docker compose down` conserva el volumen; no es necesario eliminarlo para actualizar el módulo.

## Configuración alternativa

La URL, usuario y contraseña de PostgreSQL se pueden definir con las variables DB_URL, DB_USER y DB_PASSWORD. Las propiedades Java db.url, db.user y db.password tienen prioridad. Si no se definen, se usan los valores locales de persistence.xml. El puerto web se puede cambiar con -Dserver.port=7001.

## Pruebas

```powershell
mvn test
mvn test "-Deventos.integration=true"
```

La primera orden ejecuta las pruebas unitarias. La segunda incluye PostgreSQL y necesita los scripts iniciales y usuarios demo. Los informes quedan en target/surefire-reports.

Verificación independiente de instalación (Python 3 y Docker):

```powershell
python scripts/verificar_instalacion.py
```

El script clona el commit local confirmado, crea un PostgreSQL temporal en un puerto aleatorio, aplica todas las semillas, ejecuta las pruebas y comprueba el acceso de los tres roles. Retira solo su contenedor temporal y deja logs e informe en target. No utiliza el volumen habitual.

Para reproducir la prueba de 100 sesiones, ver [rendimiento](docs/rendimiento-eventos.md).

## Administración y permisos

El módulo 6 reutiliza el inicio de sesión y los tres roles del sistema base. ADMIN gestiona únicamente salas, categorías y supervisión de eventos dentro de este módulo. La creación, edición y asignación global de usuarios/roles queda a cargo del sistema base y de su equipo responsable; no se incluye un panel paralelo.

El organizador es el único que modifica sus eventos y registra su asistencia oficial. ADMIN puede consultar eventos ajenos publicados/finalizados e inscritos; esa supervisión no concede edición, cancelación ni acceso a borradores ajenos. Los tres nombres de rol se mantienen porque son compartidos por los demás módulos.

## Documentación

- [Guía del módulo y trazabilidad](docs/modulo-6.md).
- [Evidencia de instalación limpia](docs/instalacion-limpia.md).
- [Evidencia de rendimiento](docs/rendimiento-eventos.md).
- [Material de entrega y demostración](docs/entrega/README.md).

## Trabajo con los otros grupos

Se reutilizan usuarios, roles y espacios; no se crean tablas paralelas. Las contraseñas ahora son hashes: otros módulos deben usar el verificador común y no compararlas como texto. Los cambios de disponibilidad/nombre/capacidad de los espacios afectan al catálogo compartido. La coordinación con reservas y la aprobación de la integración deben realizarse en el PR hacia develop.
