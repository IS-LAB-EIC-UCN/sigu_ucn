# Evidencia de instalación limpia

Fecha: 2026-11-12T16:36:45.335744-03:00. Commit verificado: ea01ecf9f3f473b933137ff752dc31202591d803.

Se ejecutó `python scripts/verificar_instalacion.py` desde un clon del commit confirmado, con PostgreSQL 16 temporal y puertos aleatorios, sin usar el volumen habitual.

- Compilación de Java y generación de JTE correctas.
- 54 pruebas, cero fallos, cero errores y ninguna omitida.
- Ocho eventos publicados del docente y tres cuentas demo con contraseñas protegidas.
- Solo DOCENTE abre el formulario de eventos; ADMIN y ESTUDIANTE reciben 403.
- ADMIN administra cuentas y roles desde /eventos/usuarios y /eventos/roles; los otros roles reciben 403.
- ESTUDIANTE recibe 403 al acceder directamente al catálogo. ADMIN ve salas y categorías sin expositores ni Mis actividades.
- Nueve recorridos HTTP correctos: eventos, inscripción, duplicados, supervisión, asistencia, cancelaciones y administración de cuentas con cambio de rol y desactivación de sesión abierta.
- Los datos adicionales del ensayo solo existieron en la base temporal. Aplicación y contenedor temporales retirados al finalizar.

Resumen versionado: [evidencias/instalacion-limpia.json](entrega/evidencias/instalacion-limpia.json). Los logs locales están en target/sigu-ucn-verificacion-7515c36edd81/maven.log y aplicacion.log. Se usó la caché Maven disponible; no se afirma una prueba sin acceso a Maven Central.

Las capturas de escritorio y móvil se documentan en [capturas](entrega/capturas.md). La exposición oral, aprobación del equipo e integración con otros grupos requieren la revisión de sus responsables.
