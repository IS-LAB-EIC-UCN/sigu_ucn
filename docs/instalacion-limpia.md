# Evidencia de instalación limpia · alcance de eventos

Fecha: 2026-11-12T14:47:10.709096-03:00. Commit verificado: e4c8a3d0e66cbf602a93f68c45786dd0de171788.

Se ejecutó `python scripts/verificar_instalacion.py` desde la raíz del proyecto. Se creó un clon sin archivos generados del directorio original y un PostgreSQL 16 temporal, con puertos aleatorios y sin utilizar el volumen postgres_data habitual.

Resultado:

- Compilación y generación de las vistas JTE correctas.
- 42 pruebas de eventos y autenticación ejecutadas sin fallos ni errores.
- Las semillas crearon cuatro eventos y las tres cuentas demo existentes del proyecto base.
- Las contraseñas permanecen protegidas y los tres roles cargaron la agenda por HTTP.
- El estudiante recibió 403 al intentar crear un evento; los roles organizadores cargaron el formulario.
- La ruta del panel global de usuarios respondió 404 para los tres roles: esa administración no pertenece al módulo 6.
- La aplicación temporal y el contenedor se retiraron al finalizar. La base y el servidor habituales se conservaron.

Los logs se encuentran en target/sigu-ucn-verificacion-60c0411cc652/maven.log y aplicacion.log. El resumen es target/instalacion-limpia.json. Son archivos locales excluidos de Git. Se usó la caché Maven disponible; no se afirma una prueba sin conexión a Maven Central.

La reducción de 50 a 42 pruebas corresponde al retiro de las ocho pruebas del panel global, su servicio y la actualización de sesiones vinculada a esa funcionalidad. Las pruebas de eventos permanecen y pasan.
