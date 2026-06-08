# Comprensión de Negocio: Sistema de Gestión de Eventos Académicos (SIGU-UCN)
## Módulo Funcional - Grupo 6

---

### 1. Identificación del Proyecto e Integrantes
* **Proyecto Base:** Sistema Integrado de Gestión Universitaria (SIGU-UCN)
* **Repositorio Oficial:** [https://github.com/IS-LAB-EIC-UCN/sigu_ucn](https://github.com/IS-LAB-EIC-UCN/sigu_ucn)
* **Módulo Asignado:** Sistema de Gestión de Eventos Académicos (Módulo 4.6)
* **Grupo de Trabajo:** Grupo 6
* **Integrantes:**
  * Eros Cortés
  * Diego Ravanal
  * Martín Trujillo
* **Curso:** Taller de Ingeniería de Software (EIC - Ingeniería de Software I-2026)
* **Institución:** Escuela de Ingeniería, Universidad Católica del Norte (UCN), Sede Coquimbo
* **Docente:** Daniel San Martín
* **Ayudante:** Martín Castillo

---

### 2. Introducción y Propósito
Este documento establece el marco de **Comprensión de Negocio** para el Módulo de Gestión de Eventos Académicos, desarrollado por el Grupo 6 en el contexto del proyecto corporativo SIGU-UCN. El propósito fundamental es alinear los requerimientos funcionales y reglas de negocio levantados en el **Entregable 1** con las directrices arquitectónicas, técnicas y metodológicas exigidas en el **Taller de Ingeniería de Software** y la **Documentación Técnica del Taller**. 

El objetivo es asegurar que la solución propuesta no sea un software aislado, sino un módulo altamente integrado que conviva de manera homogénea en una plataforma común, compartiendo la base de datos PostgreSQL, respetando el flujo de trabajo colaborativo en GitHub, y garantizando una separación estricta de responsabilidades mediante una arquitectura orientada a objetos organizada en capas (Java 21, Javalin, JPA/Hibernate y JTE).

---

### 3. Descripción del Problema y Contexto de Negocio
La Universidad Católica del Norte (UCN) organiza de manera recurrente múltiples actividades académicas y de extensión, tales como charlas magistrales, seminarios de investigación, simposios, conferencias internacionales y eventos institucionales orientados a la comunidad universitaria y externa. 

En la actualidad, la institución carece de un mecanismo centralizado y automatizado para gestionar estas actividades. Esto genera problemáticas críticas en la operación:
1. **Falta de visibilidad y difusión:** Los estudiantes, docentes e invitados no disponen de una agenda unificada para enterarse de los eventos próximos, recurriendo a canales informales (correos masivos, afiches físicos) que merman la participación.
2. **Conflictos logísticos:** La reserva de espacios y asignación de horarios se realiza manualmente, propiciando colisiones donde dos eventos son agendados en la misma sala, fecha y rango horario.
3. **Descontrol de aforos:** No existe un control de inscripciones en tiempo real, lo que genera sobrecupos que vulneran las capacidades reales de las salas/auditorios, afectando la calidad del evento y la seguridad.
4. **Deficiencia administrativa:** La recolección de asistencia y la gestión de datos de expositores se realiza mediante plantillas dispersas, dificultando la emisión ágil de listas de asistentes y la trazabilidad institucional.

Para mitigar esto, el módulo de **Gestión de Eventos Académicos** se integrará en SIGU-UCN como una solución centralizada que automatice el ciclo de vida completo de un evento (creación, asignación de expositores y salas, control de cupos, inscripciones en línea y generación de agendas filtrables), optimizando los recursos y la experiencia comunitaria.

---

### 4. Objetivos del Módulo

#### Objetivo General
Diseñar, modelar, implementar y probar un módulo funcional de Gestión de Eventos Académicos integrado en la plataforma SIGU-UCN, aplicando prácticas de la ingeniería de software como control de versiones por ramas, diseño orientado a objetos, principios SOLID, pruebas automáticas y arquitectura por capas.

#### Objetivos Específicos
1. Proveer un panel administrativo para la gestión integral de eventos, expositores y asignación rigurosa de ubicaciones físicas (salas/auditorios).
2. Implementar un mecanismo automático de inscripción para usuarios (asistentes) que valide de forma determinista la disponibilidad de cupos y prevenga registros duplicados.
3. Desarrollar una agenda de libre acceso que publique los cupos en tiempo real y permita aplicar filtros avanzados por temática y rangos de fecha.
4. Garantizar la consistencia relacional y el control de concurrencia en la base de datos PostgreSQL para impedir conflictos de solapamiento horario en las salas.
5. Desarrollar un diseño limpio basado en código Java 21, delegando el enrutamiento a Javalin, el renderizado limpio a JTE, y abstrayendo la lógica de persistencia con JPA/Hibernate sin acoplar la lógica de negocio a la capa HTTP.

---

### 5. Alcance del Sistema (Scope)

#### Dentro del Alcance (In-Scope)
El sistema resolverá de forma estricta las siguientes funcionalidades:
* **Gestión de Entidades Core:** Registro, edición, consulta y cancelación de Eventos Académicos, incluyendo atributos esenciales (nombre, descripción, fecha, hora, ubicación y temática).
* **Gestión de Expositores:** Registro autónomo de expositores y su posterior vinculación/desvinculación a uno o más eventos.
* **Control de Infraestructura:** Registro y administración de salas o ubicaciones con sus respectivas capacidades máximas.
* **Ciclo de Inscripción:** Flujo automatizado para que los usuarios se registren como asistentes, con actualización automática del inventario de cupos (`cupoDisponible = cupoMaximo - inscripcionesActivas`).
* **Cancelación en Cascada:** Capacidad del administrador de cancelar un evento, provocando la invalidación automática de todas las inscripciones asociadas y la liberación de los expositores y salas en ese horario.
* **Cancelación de Usuario:** Permitir a los asistentes anular su propia inscripción para liberar cupos de forma inmediata.
* **Reportabilidad Básica:** Emisión de listas oficiales de asistentes inscritos por evento, accesibles según los permisos definidos.
* **Visualización Dinámica:** Agenda pública de eventos futuros con capacidades de filtrado por descriptores temáticos y criterios cronológicos.
* **Validación en Tiempo Real:** Motor de reglas que valide e impida colisiones de sala/horario y duplicados de inscripción antes de persistir los datos.

#### Fuera del Alcance (Out-of-Scope)
Para mantener acotada la complejidad del taller de 3 meses, quedan explícitamente excluidos:
* **Módulos de Pago:** No se gestionarán cobros, transacciones financieras, pasarelas de pago ni eventos comerciales lucrativos.
* **Notificaciones Externas:** El sistema no enviará correos electrónicos (SMTP), mensajes SMS, ni alertas automáticas externas en esta versión. Toda confirmación ocurrirá en la interfaz de usuario.
* **Roles Complejos Simultáneos:** No se gestionarán perfiles híbridos complejos. Un usuario actuará bajo un único rol unívoco en el contexto de la sesión (Administrador o Usuario Asistente).
* **Autonomía del Frontend:** No se desarrollarán aplicaciones de página única (SPA) con frameworks JS pesados (React, Vue, angular). Toda vista se procesará en el servidor mediante JTE.
* **Creación Descentralizada:** Los usuarios comunes no poseen atribuciones para publicar, modificar o cancelar eventos; esta facultad es estrictamente del Administrador.

#### 5.2. Estrategia recomendada para este curso
Para este proyecto, la estrategia más conveniente es distinguir entre:
* Un esquema base, ubicado en `db/init/`, que permite crear la base desde cero.
* Una carpeta de migraciones, por ejemplo `db/migrations/`, donde queden registrados los cambios posteriores del esquema.

De este modo, el proyecto conserva una base inicial clara, pero además deja trazabilidad de cómo fue evolucionando la estructura relacional a medida que los grupos agregan módulos o ajustan tablas existentes.

#### 5.3. Por qué no conviene editar siempre el esquema inicial
Si cada cambio se hace únicamente sobre `01_schema.sql`, aparecen varios problemas:
* Se pierde la historia de los cambios.
* No queda claro qué grupo modificó una tabla o por qué.
* Se dificulta la revisión técnica en los pull requests.
* Es más complejo detectar qué cambio introdujo un error de integración.

Por eso, aunque `01_schema.sql` siga existiendo como esquema base, los cambios nuevos deberían quedar reflejados en archivos adicionales versionados en una carpeta de migraciones.

#### 5.4. Estructura sugerida
Una organización razonable del repositorio podría ser la siguiente:
```text
db/
├── init/
│   ├── 01_schema.sql
│   └── 02_seed.sql
└── migrations/
    ├── V001__create_tabla_usuario.sql
    ├── V002__create_tabla_rol.sql
    ├── V003__alter_tabla_usuario_add_email.sql
    └── V004__create_tabla_asignatura.sql
```
Aquí, `init/` representa la base inicial del sistema y `migrations/` representa los cambios incrementales posteriores.

#### 5.5. Cómo se debería documentar un cambio de esquema
Cuando un grupo necesite modificar la base de datos, la práctica recomendada es la siguiente:
1. Identificar con precisión el cambio relacional requerido.
2. Mantener consistencia con el modelo de dominio y con las entidades JPA.
3. Crear un archivo SQL nuevo dentro de `db/migrations/`.
4. Nombrarlo de manera secuencial y descriptiva.
5. Incluir allí el cambio correspondiente, por ejemplo `CREATE TABLE`, `ALTER TABLE`, claves foráneas, índices o restricciones.
6. Mencionar ese archivo en el pull request para que el cambio quede explícitamente revisado.

Por ejemplo, si se agrega una nueva columna `email` a la tabla `usuario`, no sería buena práctica modificar silenciosamente varios archivos sin dejar rastro. Lo correcto sería crear un archivo como:
`V003__alter_tabla_usuario_add_email.sql`
y dentro de él escribir el cambio correspondiente.

---

### 6. Actores del Sistema
El sistema interactúa con dos actores principales claramente diferenciados en sus permisos y casos de uso:

| Actor | Descripción | Responsabilidades en el Sistema |
| :--- | :--- | :--- |
| **Administrador** | Personal institucional responsable de la gestión y logística de los eventos de la universidad. | - Registrar, editar y cancelar eventos académicos.<br>- Registrar expositores y vincularlos a los eventos.<br>- Administrar y designar ubicaciones/salas físicas.<br>- Definir y modificar los cupos máximos de aforo.<br>- Consultar y emitir las listas de asistentes por evento.<br>- Monitorear la agenda global. |
| **Usuario (Asistente)** | Estudiantes, académicos, funcionarios o invitados externos que desean participar en las actividades. | - Consultar la agenda de eventos académicos próximos.<br>- Filtrar eventos según su temática de interés o fecha.<br>- Inscribirse de forma autónoma en eventos con cupos libres.<br>- Cancelar su inscripción para liberar cupos.<br>- Visualizar la lista de asistentes confirmados en los eventos de su interés. |

---

### 7. Especificación de Requisitos

#### Requisitos Funcionales (RF)
* **RF-01: Registrar Eventos:** El sistema debe permitir al Administrador crear un nuevo evento capturando obligatoriamente: nombre, descripción, fecha, hora de inicio, ubicación (sala) y temática descriptor.
* **RF-02: Registrar Expositores:** El sistema debe permitir al Administrador registrar expositores con sus datos de perfil (nombre, especialidad) y asociarlos de forma directa a un evento específico.
* **RF-03: Administrar Ubicaciones o Salas:** El sistema debe proveer la gestión de espacios físicos indicando el nombre/código de la sala y su capacidad estricta de aforo.
* **RF-04: Definir y Publicar Cupos:** El sistema debe permitir definir el cupo máximo por evento (el cual no puede superar la capacidad de la sala asignada) y calcular/publicar de manera automática y transparente los cupos disponibles residuales en la agenda pública.
* **RF-05: Inscribir Asistentes:** El sistema debe permitir a un usuario autenticado seleccionar un evento vigente y registrarse como asistente, disminuyendo en una unidad el cupo disponible del evento.
* **RF-06: Cancelar Inscripción:** El sistema debe permitir tanto al Usuario (su propio registro) como al Administrador anular una inscripción activa, restaurando el cupo disponible de manera inmediata.
* **RF-07: Cancelar Evento en Cascada:** El sistema debe permitir al Administrador cancelar un evento completo. Esta acción debe cambiar el estado del evento a "Cancelado", anular en cascada todas las inscripciones vigentes de los asistentes y liberar los compromisos de agenda de expositores y salas.
* **RF-08: Emitir Lista de Asistentes:** El sistema debe generar, a solicitud de usuarios o administradores, un listado ordenado con la identificación y contacto de todas las personas inscritas con éxito en un evento específico.
* **RF-09: Consultar Agenda de Eventos:** El sistema debe desplegar una vista cronológica (agenda) con todos los eventos planificados para fechas futuras que se encuentren en estado activo.
* **RF-10: Filtrar Eventos:** El sistema debe ofrecer componentes de filtrado interactivo en la agenda para segmentar las actividades por su temática (e.g., Computación, Liderazgo, Ciencias) y/o por rangos de fecha específicos.

#### Requisitos No Funcionales (RNF)
* **RNF-01: Claridad y Legibilidad de Interfaz:** Siguiendo las exigencias del taller, la interfaz del lado del servidor generada mediante plantillas JTE debe ser limpia, legible, intuitiva y estandarizada con el menú común de navegación de SIGU-UCN, utilizando layouts semánticos claros para evitar sobrecarga visual.
* **RNF-02: Validación Robusta de Datos:** El sistema debe validar de manera estricta los datos en la frontera de entrada (formularios web) y en la capa de negocio. Se auditará la nulabilidad, tipos de datos correctos, formatos de correo y consistencia temporal (e.g., fecha de evento no puede ser menor a la fecha actual).
* **RNF-03: Manejo Controlado de Errores y Mensajería:** El sistema debe capturar las excepciones operacionales de manera segura, impidiendo que el usuario visualice trazas de error de código (stacktraces). Debe retornar mensajes claros, amigables e informativos a la interfaz a través de fragmentos de alerta específicos ante fallas de negocio o restricciones de base de datos.

---

### 8. Reglas de Negocio (RN)
Estas reglas constituyen las restricciones lógicas y de dominio inquebrantables que deben ser gobernadas por los componentes de software (específicamente en la Capa de Servicios de Java 21) y soportadas por restricciones relacionales en PostgreSQL:

* **RN-01: Control de Cupo Máximo:** Un evento bajo ninguna circunstancia puede admitir un número de inscripciones activas superior al `cupoMaximo` parametrizado. Si el `cupoDisponible` llega a cero (0), el botón de inscripción debe deshabilitarse en la vista y cualquier solicitud POST directa debe ser rebotada con un mensaje de error de aforo completo.
* **RN-02: Restricción de Inscripción Duplicada:** Una persona (identificada por su ID de usuario único) no puede registrarse más de una vez en el mismo evento académico. El sistema debe validar la existencia previa de una inscripción activa para ese par `(idUsuario, idEvento)` antes de procesar un nuevo alta.
* **RN-03: Exclusividad de Ubicación y Horario (Evitar Colisiones):** No se permite el agendamiento de dos o más eventos simultáneos en la misma sala física. El sistema debe validar en tiempo real que para una `idSala`, `fecha` y `hora` dadas, no exista otro evento activo en el sistema. En caso de coincidencia, se rechazará el registro solicitando un reintento logístico.
* **RN-04: Bloqueo de Acciones sobre Eventos Cancelados:** Un evento académico que ha sido transicionado al estado "Cancelado" queda congelado para transacciones de usuario. El sistema tiene prohibido aceptar nuevas inscripciones de asistentes en eventos cancelados.
* **RN-05: Coherencia de Aforo vs Capacidad instalada:** El `cupoMaximo` de un evento definido por el Administrador no puede exceder bajo ninguna lógica la capacidad física estructurada de la `Sala` donde se llevará a cabo.
* **RN-06: No Retroactividad Temporal:** No se permite la creación de eventos ni el procesamiento de inscripciones en fechas u horas pasadas. Toda actividad debe programarse en el futuro relativo al tiempo del servidor.

---

### 9. Arquitectura General y Mapeo Tecnológico
De acuerdo con la **Documentación Técnica del Taller**, el desarrollo del Grupo 6 se estructurará rigurosamente bajo el patrón arquitectónico por capas predefinido en la base común de SIGU-UCN, garantizando bajo acoplamiento y alta cohesión.

```
[ Capa de Presentación: Vistas JTE ] (HTML Dinámico, Layouts, Fragmentos Reutilizables)
                 ▲
                 │ (ctx.render / ctx.attribute)
                 ▼
[ Capa de Control: Javalin Controllers ] (Rutas HTTP GET/POST, Validación de Parámetros)
                 ▲
                 │ (Inyección de Dependencias / Llamadas a Métodos de Servicio)
                 ▼
[ Capa de Negocio: Domain Services ] (Validación de RN-01 a RN-06, Lógica Corporativa)
                 ▲
                 │ (EntityManager / Patrón Repositorio)
                 ▼
[ Capa de Datos: JPA / Hibernate ORM ] (Mapeo de Entidades Java a PostgreSQL)
                 ▲
                 │ (Conexión JDBC / Docker Compose)
                 ▼
[ Base de Datos: PostgreSQL ] (Scripts de Inicialización 01_schema.sql y 02_seed.sql)
```

#### Rol Específico de las Tecnologías en el Módulo:
1. **Java 21:** Lenguaje núcleo de desarrollo. Se empleará POO clásica para las entidades de dominio, asegurando la aplicación de los **Principios SOLID** (e.g., Principio de Responsabilidad Única en Servicios y Controladores). Se implementarán `records` para la transferencia de datos limpios (DTOs) y `enum` para el control estricto de estados (e.g., `EstadoEvento { ACTIVO, CANCELADO }`).
2. **Maven:** Gestión automatizada de la compilación (Java 21) y dependencias. Se integrará al flujo nativo para la precompilación de las plantillas JTE y la ejecución automatizada de la suite de pruebas.
3. **Javalin:** Actuará exclusivamente como la capa de transporte HTTP. Las rutas web mapearán solicitudes entrantes (e.g., `POST /eventos/inscribir`). Los controladores asociados extraerán parámetros del objeto `Context` (parámetros de formulario, de consulta o sesión) y delegarán de inmediato la ejecución a la capa de servicio, evitando contener lógica de negocio interna.
4. **JTE (Java Template Engine):** Motor de renderizado del lado del servidor. El módulo del Grupo 6 se integrará al layout maestro del sistema (`layout.jte`), incorporando sus opciones en el menú principal común. Las vistas de listados, formularios de registro de expositores y agendas recibirán datos completamente digeridos y procesados por el controlador; la vista se limitará a iterar y formatear de manera limpia.
5. **JPA / Hibernate:** Orquestará el mapeo Objeto-Relacional (ORM). Se estructurarán las clases Java correspondientes con anotaciones explícitas de relación:
   * **`Evento`**: Entidad raíz mapeada a la tabla `eventos`. Poseerá una relación `@ManyToOne` hacia `Sala` (clave foránea `id_sala`) y una relación `@OneToMany` o `@ManyToMany` hacia `Expositor` e `Inscripcion`.
   * **`Expositor`**: Mapeada a `expositores`, vinculada a eventos.
   * **`Sala`**: Mapeada a `salas`, controlando la capacidad.
   * **`Inscripcion`**: Entidad intermedia mapeada a `inscripciones` que modela la relación asociativa entre un `Usuario` y un `Evento`, almacenando metadatos como la `fechaInscripcion` y el estado del registro.
   * **`Usuario`**: Entidad compartida del sistema base de SIGU-UCN.
6. **PostgreSQL & Docker Compose:** El esquema del módulo no será autogenerado por Hibernate. Siguiendo el rigor del taller, el Grupo 6 diseñará e incorporará las sentencias DDL explícitas en el script compartido `db/init/01_schema.sql` (tablas, llaves primarias, llaves foráneas y restricciones `UNIQUE` compuestas) y los datos de prueba iniciales en `db/init/02_seed.sql`.

---

### 10. Planificación del Proyecto (Backlog Priorizado por Sprints)
Basado en el diseño ágil y progresivo determinado en el taller, el desarrollo se dividirá de forma estricta en 4 Sprints, prohibiendo las subidas masivas finales y promoviendo el uso integrado del tablero Kanban de GitHub.

```
                      PLANIFICACIÓN DE DEPLOYMENT PROGRESIVO
                      
   SPRINT 1: Gestión Base     SPRINT 2: Inscripciones      SPRINT 3: Consultas         SPRINT 4: Calidad
 ┌───────────────────────┐   ┌───────────────────────┐   ┌───────────────────────┐   ┌───────────────────────┐
 │ • CRUD Eventos (Admin)│   │ • Publicar Cupos      │   │ • Filtros Avanzados   │   │ • Refine UI JTE       │
 │ • CRUD Expositores    │   │ • Validar Colisiones  │   │   (Temática / Fecha)  │   │ • Manejo de Errores   │
 │ • Gestión de Salas    │ ─►│ • Alta Asistentes     │ ─►│ • Emitir Listas de    │ ─►│   Amigables           │
 │ • Cancelación Cascada │   │ • Control Duplicados  │   │   Asistencia          │   │ • Pruebas de          │
 │ • Persistencia Base   │   │ • Pruebas Unitarias   │   │ • Agenda Completa     │   │   Integración Final   │
 └───────────────────────┘   └───────────────────────┘   └───────────────────────┘   └───────────────────────┘
```

#### Sprint 1: Gestión Base de Eventos (Estructura y Logística)
* **Objetivo:** Implementar los mantenedores y operaciones fundamentales del administrador para poblar el sistema.
* **Funcionalidades:**
  1. **Registrar un evento:** Formulario administrativo e inserción básica en base de datos.
  2. **Registrar expositores:** Entidad expositor y su asociación física a las actividades.
  3. **Administrar ubicaciones:** Carga de salas iniciales y control de sus capacidades.
  4. **Definir cupos de un evento:** Restricción de aforo inicial a nivel de negocio.
  5. **Cancelar un evento (Flujo Administrativo):** Implementación del estado lógico de cancelación en cascada.
* **Entregables Técnicos:** Scripts SQL de tablas core, mapeo ORM inicial con Hibernate, y controladores Javalin básicos para la administración de datos.

#### Sprint 2: Inscripciones y Reglas Principales de Dominio
* **Objetivo:** Desarrollar el motor de lógica transaccional y reglas de negocio automatizadas para los asistentes.
* **Funcionalidades:**
  1. **Publicar cupos disponibles:** Algoritmo dinámico en la capa de servicio que reste el conteo de inscripciones vigentes al cupo máximo del evento.
  2. **Validar conflictos de ubicación, fecha y horario:** Lógica preventiva estricta en el servicio Java para rechazar registros de eventos que colisionen en espacio-tiempo (RN-03).
  3. **Inscribirse como asistente:** Endpoint transaccional seguro que genere el registro en la tabla asociativa.
  4. **Validar llenado de cupos:** Bloqueo preventivo de inscripciones si la capacidad está copada (RN-01).
  5. **Validar inscripción duplicada:** Consulta de control antes de persistir (RN-02).
  6. **Bloquear inscripciones en eventos cancelados:** Restricción lógica en el servicio de inscripción (RN-04).
  7. **Cancelar inscripción de asistente:** Flujo de liberación de cupo por parte del usuario.
* **Entregables Técnicos:** Cobertura exhaustiva de **Pruebas Unitarias** con JUnit 5 y Mockito simulando las transacciones de servicios y el aislamiento de reglas de negocio.

#### Sprint 3: Consulta, Búsqueda y Gestión de Información
* **Objetivo:** Habilitar los mecanismos de salida de información y visualización avanzada para los actores de la comunidad.
* **Funcionalidades:**
  1. **Filtrar eventos por temática y/o fecha:** Construcción de consultas dinámicas en la capa de persistencia (JPA Criteria o JPQL parametrizado) seguras contra inyección SQL para la segmentación de datos.
  2. **Emitir lista de asistentes:** Vista estructurada que capture los datos relacionales de los inscritos confirmados en un evento académico y permita su visualización ordenada.
  3. **Consultar agenda de eventos:** Pantalla principal del módulo integrada en el menú común que muestre los eventos futuros activos.

#### Sprint 4: Mejora de Interfaz, Control de Robustez y Pruebas
* **Objetivo:** Pulir la experiencia de usuario, robustecer el sistema ante fallos y asegurar la correcta integración física en la plataforma compartida.
* **Funcionalidades:**
  1. **Mejorar interfaz clara y legible:** Revisión estética sobre las plantillas JTE para garantizar consistencia visual con las pautas de diseño y la usabilidad institucional.
  2. **Manejo de errores y mensajes al usuario:** Inserción de bloques catch dedicados y redirecciones controladas con alertas personalizadas que informen al usuario amigablemente sobre violaciones de reglas de negocio sin romper el flujo de la aplicación.
  3. **Pruebas de Integración:** Ejecución de pruebas integrales de punta a punta (End-to-End/Integration Tests) que verifiquen el ciclo completo: persistencia real en la base de datos PostgreSQL en contenedor Docker, control de rutas Javalin y consistencia relacional múltiple.

---

### 11. Criterios de Aceptación Técnicos del Módulo
Cada componente desarrollado por el Grupo 6 se considerará apto para la fusión en la rama principal (`main`) solo si cumple con las métricas derivadas del documento técnico:
1. **Acoplamiento Web Cero:** Ninguna regla de negocio (como la verificación de colisión de horarios de salas) puede estar escrita dentro de un bloque lambda de Javalin o un controlador. Todo debe residir en clases de servicio Java reutilizables y testeables de manera aislada.
2. **Consistencia de Tipos en Persistencia:** El ciclo de vida de las entidades administradas por JPA debe estar perfectamente coordinado (uso correcto de `EntityManager`). Las llaves foráneas definidas en las entidades deben reflejar fielmente las restricciones establecidas de mutuo acuerdo en el script `01_schema.sql`.
3. **Validación de Vistas:** Las plantillas JTE no deben realizar llamados a repositorios ni ejecutar cálculos lógicos complejos. Deben recibir datos tipados e inmutables listos para su presentación.
4. **Validaciones en Cascada:** Al invocar la cancelación administrativa de un evento, se debe constatar mediante pruebas automatizadas que los registros de inscripción vinculados mutaron su estado o se gestionaron de acuerdo a la integridad referencial definida, liberando los aforos de manera consistente.
