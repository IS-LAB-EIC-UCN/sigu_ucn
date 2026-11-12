# Evidencia de instalación limpia

Fecha: 2026-11-12T14:10:56.911639-03:00. Commit verificado: b7af1b36aa83b0830cc3fad383ba04d880cd3bc2.

Se ejecutó `python scripts/verificar_instalacion.py` desde la raíz del proyecto. El script creó un clon local sin archivos generados ni dependencias del directorio target original. Se utilizó un PostgreSQL 16 nuevo en Docker, sin el volumen postgres_data habitual y con puertos aleatorios ligados a localhost.

Resultado:

- Compilación y generación JTE correctas.
- 50 pruebas ejecutadas: cero fallos y cero errores.
- Las semillas crearon 4 eventos y 3 cuentas demo.
- Las tres contraseñas quedaron protegidas; el acceso de ADMIN, DOCENTE y ESTUDIANTE se verificó por HTTP.
- ADMIN accedió a usuarios; los otros roles recibieron 403 en esa administración.
- Las tres cuentas cargaron la agenda de eventos.
- La aplicación temporal y su contenedor fueron retirados. El volumen y el servidor habituales se conservaron.

Los logs originales se encuentran en target/sigu-ucn-verificacion-ed51c3b50475/maven.log y aplicacion.log. El resumen está en target/instalacion-limpia.json. Esos archivos son locales y no se versionan. Las dependencias Maven ya estaban disponibles en la caché del usuario; no se afirma haber probado un entorno sin acceso a Maven Central.

Esta prueba usa un clon local del commit confirmado. Para repetir desde GitHub se puede clonar grupo-6 y ejecutar las mismas instrucciones del README.
