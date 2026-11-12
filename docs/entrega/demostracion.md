# Guion de demostración · Grupo 6

Duración sugerida: 10 a 12 minutos. Ejecutar la aplicación y PostgreSQL previamente. Usar datos ficticios nuevos; no modificar la cuenta administradora del sistema para demostrar bloqueos.

## 1. Contexto y actores (1 minuto)

Explicar el problema, el alcance del módulo y la relación con usuarios/espacios compartidos. Mostrar ADMIN, DOCENTE y ESTUDIANTE. Aclarar que supervisar no permite alterar eventos ajenos.

## 2. Catálogos del módulo de eventos (2 minutos)

Entrar como ADMIN y mostrar Salas y categorías, Usuarios y roles, y supervisión de eventos. Editar una ubicación de prueba y comprobar la protección de capacidad. Mostrar la creación/edición de una cuenta de prueba, asignación de roles y desactivación sin eliminar historial. Explicar que se reutilizan las tablas del proyecto base.

## 3. Organización y conflictos (3 minutos)

Entrar como DOCENTE. Registrar o editar un expositor, seleccionar varios, elegir categoría y sala y crear un evento futuro. Mostrar el borrador y publicarlo. Intentar otro evento con sala y horario coincidentes: debe informar el conflicto y conservar los datos del formulario. Explicar por qué un horario consecutivo sí se permite.

## 4. Participación y asistencia (2 minutos)

Entrar como ESTUDIANTE. Buscar/filtrar, revisar el detalle e inscribirse. Mostrar la confirmación y el cupo actualizado. Intentar duplicado y mostrar el rechazo. El organizador abre inscritos y marca asistencia; ADMIN ajeno únicamente consulta. Cancelar una inscripción dentro de plazo y comprobar la liberación del cupo. El límite exacto se demuestra con la prueba automatizada de reloj fijo.

## 5. Pruebas, instalación y rendimiento (2 minutos)

Mostrar los informes de las 54 pruebas del clon limpio, los tres casos simultáneos y la evidencia de 100 sesiones. Explicar la corrección del pool básico y la limitación de que se midió en localhost con una base pequeña. Mostrar que el script de instalación utiliza otro contenedor y no elimina la base de trabajo.

## 6. Cierre técnico (1 minuto)

Explicar la arquitectura por capas y un ejemplo concreto de cada principio SOLID sin recitar definiciones. Mostrar los commits progresivos, la propuesta hacia develop y los puntos de coordinación de usuarios/contraseñas/espacios. Cada integrante debe revisar su explicación y practicar la defensa.

## Evidencias y preparación

- Confirmar que los tres accesos demo funcionan.
- Mantener una copia de respaldo de la base habitual antes de una demostración manual.
- Preparar un título y correo de prueba únicos.
- Tener abiertos los diagramas, los informes de prueba y el PR.
- Las capturas de cuentas/eventos temporales son evidencia de prueba, no datos institucionales reales.
- El equipo debe confirmar integrantes, formato académico, responsables y fecha de entrega.

## Ensayo automatizado realizado

El clon limpio completó nueve recorridos HTTP mediante scripts/verificar_flujos.py, incluida una cuenta temporal con cambio de rol y desactivación mientras su sesión estaba abierta. Evidencia: evidencias/instalacion-limpia.json. Esto verifica el recorrido técnico; cada integrante debe ensayar su explicación oral.
