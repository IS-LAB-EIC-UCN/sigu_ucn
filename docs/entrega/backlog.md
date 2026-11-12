# Backlog y estado de entrega · 12/11/2026

Estado técnico local, respaldado por commits y pruebas. No representa una actualización del tablero remoto, al que falta permiso Projects. Los responsables concretos deben acordarlos los integrantes; no se atribuyen tareas personales sin confirmación.

| ID | Trabajo | Estado técnico | Evidencia |
| --- | --- | --- | --- |
| HU-01 | Sesión, roles y contraseñas protegidas | Implementado y probado | AuthService, PasswordHasher, PermisosEventos y pruebas. |
| HU-02 | Programar/publicar eventos | Implementado y probado | EventoService, repositorio y formulario. |
| HU-03 | Agenda y filtros | Implementado y probado | Listado, categoría y fecha. |
| HU-04 | Detalle y expositores | Implementado y probado | Ficha y relación muchos a muchos. |
| HU-05 | Inscripción y confirmación | Implementado y probado | Persistencia y mensajes explícitos. |
| HU-06 | Cancelación y cupos | Implementado y probado | Plazo hasta inicio, pruebas temporales y concurrentes. |
| HU-07 | Salas y capacidad | Implementado y probado | Catálogo común, edición segura y disponibilidad. |
| HU-08 | Catálogo de expositores | Implementado y probado | Institución, edición y selección múltiple. |
| HU-09 | Inscritos y asistencia oficial | Implementado y probado | Lista imprimible y autorización del responsable. |
| HU-10 | Conflictos de sala | Implementado y probado | Bloqueo transaccional y escrituras simultáneas. |
| ALC-01 | Administración global de usuarios y roles | Fuera del alcance del Grupo 6; retirada | Se reutiliza la autenticación base, sin panel paralelo. |
| QA-01 | Instalación limpia | Verificado | Ver evidencia actualizada en docs/instalacion-limpia.md. |
| QA-02 | Carga de 100 sesiones | Verificado en condiciones locales | 300 consultas, sin errores; evidencia de rendimiento. |
| DOC-01 | Informe y diagramas | Preparado para revisión del equipo | PDF, Markdown, seis SVG y fuentes Mermaid. |
| DOC-02 | Demostración y defensa | Guion y presentación preparados; ensayo pendiente del equipo | demostracion.md. |
| INT-01 | PR hacia develop | Borrador publicado; revisión pendiente | [PR #1](https://github.com/IS-LAB-EIC-UCN/sigu_ucn/pull/1). |
| INT-02 | Coordinar usuarios/hash y espacios con otros equipos | Pendiente de revisión humana | Notas de compatibilidad del PR. |
| INT-03 | Actualizar GitHub Project | Pendiente de permiso externo | La conexión actual carece de read:project/project. |
| INT-04 | Aprobación y merge | Pendiente del responsable del repositorio | No se fusiona automáticamente. |

La implementación técnica y sus verificaciones están completadas en esta versión; la aprobación, coordinación, tablero y exposición se distinguen expresamente de los cambios de código.
