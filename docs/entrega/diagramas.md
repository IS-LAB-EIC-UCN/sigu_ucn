# Diagramas editables del módulo 6

Estas fuentes documentan el diseño actual. Las versiones SVG y PDF se regeneran mediante scripts/generar_informe.py. Las relaciones compartidas deben revisarse al integrar con otros módulos.

## Casos de uso por actor

```mermaid
flowchart LR
    A[Administrador] --> S[Administrar salas y categorías]
    A --> U[Gestionar usuarios y asignar roles existentes]
    A --> V[Supervisar eventos ajenos publicados]
    O[Organizador: DOCENTE responsable] --> E[Crear, editar, publicar y cancelar su evento]
    O --> X[Gestionar expositores]
    O --> R[Consultar inscritos y registrar asistencia propia]
    P[Participante: ESTUDIANTE] --> C[Consultar agenda, filtros y detalle]
    P --> I[Inscribirse]
    P --> N[Cancelar inscripción antes del inicio]
```

## Clases y relaciones

```mermaid
classDiagram
    Rol "1" <-- "0..*" Usuario
    Usuario "1" <-- "0..*" Evento : organiza
    Categoria "1" <-- "0..*" Evento : clasifica
    Espacio "1" <-- "0..*" Evento : programa
    Usuario "1" <-- "0..*" Inscripcion : participa
    Evento "1" <-- "0..*" Inscripcion
    Evento "0..*" -- "1..*" Expositor : evento_expositores
    Usuario : nombre, correo, passwordHash, activo
    Rol : nombre
    Evento : titulo, fecha, horaInicio, horaFin, cupoMaximo, estado
    Categoria : nombre
    Espacio : nombre, tipo, capacidad, disponible
    Expositor : nombre, correo, institucion, especialidad
    Inscripcion : fechaInscripcion, estado, asistencia
```

## Secuencia: inscribirse

```mermaid
sequenceDiagram
    actor P as Participante
    participant C as Controlador
    participant R as Repositorio
    participant DB as PostgreSQL
    P->>C: POST inscripción + CSRF
    C->>R: inscribir(eventoId, usuarioId)
    R->>DB: BEGIN y bloqueo de evento
    DB-->>R: Estado actual
    R->>DB: Validar plazo, cupos y registro anterior
    alt Condiciones cumplidas
        R->>DB: INSERT o reactivar inscripción
        R->>DB: COMMIT
        R-->>C: Operación confirmada
        C-->>P: Redirección y confirmación explícita
    else Estado, plazo, duplicado o cupos inválidos
        R->>DB: ROLLBACK
        R-->>C: Motivo de rechazo
        C-->>P: Mostrar error sin confirmar inscripción
    end
```

## Secuencia: programar evento

```mermaid
sequenceDiagram
    actor O as Organizador
    participant C as Controlador
    participant S as Servicio
    participant R as Repositorio
    participant DB as PostgreSQL
    O->>C: Formulario + CSRF
    C->>S: guardar(evento)
    S->>S: Validar fecha, sala, categoría, expositores y cupo
    S->>R: save o update
    R->>DB: BEGIN y bloqueo de agenda
    R->>DB: Comprobar horarios de la misma sala
    alt Sin conflicto
        R->>DB: Escribir evento y COMMIT
        R-->>S: Evento guardado
        S-->>C: Resultado
        C-->>O: Confirmación
    else Solapamiento
        R->>DB: ROLLBACK
        R-->>C: Conflicto de horario
        C-->>O: Formulario con datos y error
    end
```

## Actividad: inscripción

```mermaid
flowchart TD
    A([Inicio: participante autenticado]) --> B[Consultar y seleccionar evento]
    B --> C[Revisar detalle y confirmar]
    C --> D{Publicado, dentro de plazo, sin duplicado y con cupo?}
    D -- Sí --> E[Guardar inscripción en transacción]
    E --> F[Commit y confirmación]
    F --> G([Fin])
    D -- No --> H[Informar motivo sin modificar registro]
    H --> G
```

## Actividad: asistencia

```mermaid
flowchart TD
    A([Inicio]) --> B[Abrir participantes del evento]
    B --> C[Seleccionar inscripción]
    C --> D{Organizador responsable y registro activo?}
    D -- Sí --> E[Guardar o corregir asistencia]
    E --> F[Actualizar lista]
    F --> G([Fin])
    D -- No --> H[Rechazar operación]
    H --> G
```
