# Evidencia de concurrencia y rendimiento · módulo de eventos

Versión limitada al módulo 6, sin administración global de usuarios/roles ni su filtro de actualización de sesiones.

## Método

- Medición: 2026-11-12T14:49:45.177840-03:00, entorno Windows-11-10.0.26300-SP0.
- Java 21, Javalin, JTE, Hibernate/HikariCP y PostgreSQL 16 local en Docker.
- Base de demostración: seis eventos, dos registros de inscripción y tres usuarios.
- 100 sesiones autenticadas independientes, con una cuenta de prueba y cookies distintas.
- Tres rondas con barrera de salida simultánea: 300 consultas en total, distribuidas entre listado, agenda y Mis actividades.
- Login y calentamiento fuera del cronómetro. Cada medición incluye la recepción completa del HTML.
- La prueba no modifica eventos ni inscripciones y exige una respuesta autenticada correcta y menor a dos segundos en todas las muestras.

## Resultado actual

| Peticiones | Errores | Máximo (s) | P95 (s) | Resultado |
| --- | --- | --- | --- | --- |
| 300 | 0 | 0.619212 | 0.567522 | Correcto en estas condiciones |

Se conserva HikariCP con máximo 20 conexiones y espera de hasta 2000 ms. El pool interno anterior produjo errores por agotamiento bajo carga, motivo de la corrección. Referencia: [integración HikariCP de Hibernate](https://docs.hibernate.org/orm/6.6/userguide/html_single/).

## Repetir

```powershell
$env:SIGU_TEST_CORREO = 'estudiante@sigu.cl'
$env:SIGU_TEST_PASSWORD = 'clave de la cuenta de prueba'
python scripts/probar_carga.py --clientes 100 --rondas 3
```

La salida detallada queda en target/carga_eventos.json y no se versiona. La última medición de esta corrección se conservó en target/carga_eventos_solo_eventos.json. El script devuelve error si alguna respuesta falla o alcanza dos segundos.

La suite actual de 42 pruebas conserva las verificaciones de duplicados, usuarios que compiten por cupos y eventos que compiten por la misma sala. Las ocho pruebas retiradas pertenecían exclusivamente al panel global de usuarios y roles que quedó fuera de alcance.

Esta prueba local utiliza una base pequeña y una identidad de prueba repetida en sesiones distintas. No garantiza la latencia con una base grande, otra red o una carga sostenida; esos escenarios requieren mediciones específicas.
