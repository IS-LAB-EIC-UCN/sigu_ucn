# Evidencia de concurrencia y rendimiento de eventos

Medición local del 12 de noviembre de 2026, zona America/Santiago. Requisito del informe: consultas inferiores a dos segundos con hasta 100 usuarios concurrentes.

## Método

- Aplicación Java 21 en localhost:7000, PostgreSQL 16 en Docker, Windows 11.
- Base de demostración: 6 eventos, 2 registros de inscripción y 3 usuarios.
- 100 clientes con cookies y sesiones autenticadas independientes. Se reutiliza una cuenta de prueba; no son 100 identidades distintas.
- Tres rondas con barrera de salida simultánea: 300 consultas HTTP en total.
- Rutas: listado, agenda y Mis actividades, 100 consultas de cada ruta distribuidas entre las tres rondas.
- Login y calentamiento fuera del cronómetro. El tiempo de cada consulta incluye recibir completamente su HTML.
- Se exige HTTP correcto, página autenticada y menos de dos segundos en todas las muestras. No basta con una media baja.
- Las consultas no modifican eventos o inscripciones. Las cookies y contraseñas no se guardan en el informe.

## Resultados

| Configuración | Errores / 300 | Máximo (s) | P95 (s) | Cumple esta medición |
| --- | --- | --- | --- | --- |
| Pool interno anterior | 74 | 1.428458 | 1.130650 | No |
| HikariCP, máximo 20 conexiones | 0 | 0.737048 | 0.684180 | Sí |

Medición anterior: 2026-11-12T13:50:01.800480-03:00. Medición corregida: 2026-11-12T13:51:59.939221-03:00.

El fallo inicial fue una excepción por agotamiento del pool interno, verificada en el registro del servidor. Se añadió el módulo oficial hibernate-hikaricp, con la misma versión que Hibernate, y un máximo de 20 conexiones con espera acotada a 2000 ms. No se aumentó el límite de conexiones de PostgreSQL. Referencia: [integración de HikariCP en Hibernate](https://docs.hibernate.org/orm/6.6/userguide/html_single/).

## Repetir

Con PostgreSQL y la aplicación activos:

```powershell
$env:SIGU_TEST_CORREO = 'estudiante@sigu.cl'
$env:SIGU_TEST_PASSWORD = 'clave de la cuenta de prueba'
python scripts/probar_carga.py --clientes 100 --rondas 3
```

El script devuelve error si alguna petición falla o alcanza los dos segundos. Las muestras y el resumen se guardan en target/carga_eventos.json, excluido de Git. La variable de contraseña solo se usa para crear las sesiones.

Para comprobar simultáneamente la integridad de escrituras:

```powershell
mvn test "-Deventos.integration=true"
```

La suite incluye ocho solicitudes duplicadas de un usuario, ocho usuarios compitiendo por dos cupos y cuatro eventos compitiendo por la misma sala y horario. Se valida el estado final en PostgreSQL y se limpian únicamente sus datos temporales.

## Límites de la evidencia

Esta es una prueba reproducible en una base pequeña y sobre la misma máquina cliente/servidor. No es una garantía de latencia para una base grande, una red externa ni una carga sostenida de larga duración. Esas condiciones requieren nuevas mediciones. Las 41 pruebas automatizadas pasaron tras cambiar el pool. También se verificaron en Chrome los permisos de categorías y asistencia y las confirmaciones de inscripción/cancelación.
