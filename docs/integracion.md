# Integración del módulo 6 hacia develop

Rama de origen: grupo-6. Base propuesta: develop. PR de revisión: [#1](https://github.com/IS-LAB-EIC-UCN/sigu_ucn/pull/1), creado como borrador. No se fusiona automáticamente.

## Resultado funcional

RF-01 a RF-12 del informe del equipo: agenda, filtros, detalle, eventos propios, categorías, salas, varios expositores, cupos, inscripciones, cancelación dentro de plazo, asistencia y listas imprimibles. El alcance se limita a eventos y reutiliza la autenticación y los roles existentes del sistema base; la gestión de cuentas del diagrama se integra en /eventos/usuarios sobre las tablas comunes. ADMIN supervisa eventos ajenos publicados/finalizados sin modificarlos.

## Cambios compartidos que revisar antes de aceptar

| Área | Cambio | Revisión requerida |
| --- | --- | --- |
| Usuarios y roles | Se reutilizan las tablas y los tres roles; ADMIN crea/edita cuentas, asigna roles y activa/desactiva desde Eventos. Los cambios se aplican a sesiones abiertas en rutas /eventos. | Confirmar que los otros módulos usan esos roles y el identificador de sesión común. |
| Contraseñas | PBKDF2 en la columna existente; semillas protegidas y migración de cuentas antiguas al arrancar. | Los módulos deben autenticar con AuthService/PasswordHasher; no comparar texto plano ni volver a hashear un hash existente. |
| Espacios | Se usa el catálogo común; nombre, capacidad y disponibilidad se administran desde el panel. | Coordinar con el grupo de reservas el significado de disponible y los cambios de capacidad. No se implementan reservas generales. |
| Arranque | PostgreSQL local 5433; web 7000; db.url/user/password y DB_URL/USER/PASSWORD permiten configuración externa. | Ajustar valores del entorno de integración sin modificar tablas ni borrar el volumen. |
| Conexiones | HikariCP alineado con la versión de Hibernate, máximo 20 conexiones. | Conservar un único proveedor y validar recursos del servidor compartido. |

## Migración

Base nueva: Docker ejecuta db/init en orden. Base existente: aplicar 05 y 07 a 10, sin eliminar el volumen. 06 es opcional para datos demo. Usuarios y roles no requieren modificaciones estructurales. Conservar los scripts históricos para que una instalación desde cero funcione.

## Verificación realizada

- Suite actual de eventos/autenticación: 54 pruebas correctas. La evidencia de instalación limpia se actualiza en docs/instalacion-limpia.md.
- Acceso de los tres roles y permisos de eventos comprobados por HTTP; /eventos/usuarios y /eventos/roles son exclusivos de ADMIN; las antiguas rutas /admin siguen sin exponerse.
- Chrome y HTTP: agenda, detalle, expositores, inscritos y permisos; escritorio y móvil.
- Pruebas concurrentes de cupos, duplicados y conflictos de sala.
- 100 sesiones, 300 consultas, cero errores; máximo local 0.751096 s, P95 0.702972 s. Se documentan las limitaciones de entorno y datos.
- Registros temporales retirados; volumen habitual conservado.

## Antes de fusionar

- [ ] El equipo revisa el informe, integrantes, diagramas y guion de defensa.
- [ ] El responsable de integración revisa autenticación y roles comunes.
- [ ] El equipo de reservas confirma el uso coordinado de espacios.
- [ ] Se repiten pruebas sobre develop cuando se integren los demás módulos.
- [ ] Se actualiza el tablero GitHub Projects con los estados acordados.
- [ ] El responsable del repositorio aprueba y realiza el merge.

La conexión disponible permite repositorio/PR, pero carece de read:project/project para el tablero. El backlog local está en docs/entrega/backlog.md y no se presenta como un tablero remoto actualizado.
