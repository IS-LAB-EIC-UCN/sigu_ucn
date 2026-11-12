# Eventos académicos · SIGU-UCN
Grupo 6 · Taller de Ingeniería de Software · UCN
Versión técnica del 12 de noviembre de 2026
Material de apoyo para revisión y ensayo del equipo
---
# Problema y alcance
- Centralizar eventos, fechas, ubicaciones y expositores.
- Evitar inscripciones duplicadas, sobrecupos y conflictos de sala.
- Apoyar la consulta de inscritos y el registro de asistencia.
- Reutilizar usuarios, roles y espacios del sistema común.
---
# Actores y permisos
- ESTUDIANTE: consulta, filtros, detalle e inscripción propia.
- DOCENTE: organiza sus eventos y registra asistencia de sus participantes.
- ADMIN: gestiona salas, categorías y supervisa eventos.
- Supervisar un evento ajeno permite consultar, no modificarlo.
---
# Demostración del flujo principal
- El participante busca un evento y revisa su detalle.
- Confirma la inscripción; el servidor valida plazo, cupos y duplicados.
- PostgreSQL confirma el registro antes del mensaje de éxito.
- Cancelar antes del inicio libera el cupo y conserva el historial.
---
# Modelo y arquitectura
[CLASES]
---
# Integridad ante solicitudes simultáneas
- Ocho solicitudes del mismo usuario: una inscripción.
- Ocho usuarios compiten por dos cupos: exactamente dos aceptados.
- Cuatro eventos compiten por una sala y horario: uno aceptado.
- Validación y escritura permanecen en la misma transacción.
---
# Seguridad del módulo de eventos
- Contraseñas protegidas con PBKDF2 y sal aleatoria.
- Permisos y CSRF comprobados en el servidor.
- La gestión global de usuarios y roles pertenece al proyecto base.
- Solo el organizador registra la asistencia oficial de su evento.
---
# Verificación reproducible
- 42 pruebas de eventos y autenticación correctas.
- Los tres roles acceden a sus funciones y reciben los rechazos esperados.
- Chrome y HTTP: eventos, permisos y pantallas móviles.
- 100 sesiones y 300 consultas: cero errores, máximo local 0.619 s.
- La medición usa una base pequeña en localhost; no garantiza otros entornos.
---
# Integración y cierre
- Commits específicos publicados en grupo-6.
- PR borrador número 1 hacia develop, sin merge automático.
- Informe, fuentes editables, diagramas, backlog y guion disponibles.
- Revisión del equipo, coordinación de tablas compartidas y ensayo pendientes.
- Actualización de GitHub Projects pendiente de autorización adicional.
