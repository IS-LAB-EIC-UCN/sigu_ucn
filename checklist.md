# Checklist de Casos de Uso - Módulo de Gestión de Eventos Académicos (Grupo 6)

Este checklist sirve para hacer seguimiento del progreso de los casos de uso del módulo de gestión de eventos académicos del sistema SIGU-UCN.

## Estado de Casos de Uso

- [x] **“Registrar un Evento”**: El administrador registra un evento en el sistema.
- [x] **“Registrar Expositor”**: El administrador registra a un expositor para un evento dado en el sistema.
- [x] **“Definir Cupos”**: El administrador define los cupos disponibles de un evento dado (aforo máximo validado contra capacidad física).
- [/] **“Emitir Agenda de Eventos”**: Ante la petición de un administrador, el sistema entrega una agenda de eventos en las fechas próximas.
  - [x] Vista general de eventos (/eventos).
  - [ ] Filtrar y ordenar cronológicamente por fechas próximas (futuras y activas).
- [/] **“Administrar ubicaciones”**: El administrador designa la ubicación de un evento dado.
  - [x] Selección de espacio al crear un evento.
  - [ ] CRUD administrativo para agregar, modificar o eliminar salas/espacios físicos.
- [ ] **“Cancelar un Evento”**: El administrador cancela un evento junto con todas las inscripciones y expositores relacionados (cascada/lógico).
- [ ] **“Inscribirse como Asistente”**: El usuario se inscribe como asistente en un evento.
- [ ] **“Cancelar Inscripción”**: Un administrador o usuario cancela la inscripción de un asistente a un evento dado.
- [ ] **“Publicar Cupos”**: El sistema publica los cupos disponibles de un evento dado (cálculo en tiempo real: total - inscritos).
- [ ] **“Emitir lista de Asistentes”**: Ante la petición del administrador, el sistema entrega una lista de asistentes de un evento dado.
- [ ] **“Filtrar por temática y/o Filtro”**: El usuario busca eventos utilizando un filtro de temática y/o fecha.

---

## Estado de Reglas de Negocio (RN) de Soporte

- [x] **RN-03: Exclusividad de Ubicación y Horario** (Evitar colisiones de salas y agendas de expositores).
- [x] **RN-05: Coherencia de Aforo vs Capacidad Instalada** (Aforo del evento <= capacidad de la sala).
- [ ] **RN-01: Control de Cupo Máximo** (No permitir inscripciones si el cupo está lleno).
- [ ] **RN-02: Restricción de Inscripción Duplicada** (Un usuario no puede registrarse dos veces en el mismo evento activo).
- [ ] **RN-04: Bloqueo de Acciones sobre Eventos Cancelados** (No se pueden inscribir asistentes si el evento está cancelado).
- [ ] **RN-06: No Retroactividad Temporal** (Impedir registrar eventos en fechas y horas pasadas).
