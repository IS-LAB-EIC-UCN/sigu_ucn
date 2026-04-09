# SIGU-UCN

Sistema integrado de gestión universitaria desarrollado en Java con Javalin, JPA/Hibernate, PostgreSQL, Maven y Docker. El proyecto se construye sobre una base común provista por el docente y está organizado en módulos funcionales desarrollados por equipos, respetando una arquitectura compartida, principios SOLID, persistencia y testing.

## Descripción del proyecto

Este proyecto corresponde a una plataforma académica de desarrollo de software orientada a la construcción de un sistema integrado de gestión universitaria. La aplicación busca centralizar distintos servicios institucionales, tales como reservas, biblioteca, soporte, inventario, eventos, tutorías y estacionamientos, sobre una misma base técnica y arquitectónica.

Cada equipo desarrollará un módulo funcional dentro del sistema, integrándose al menú principal, a la base de datos común y a la estructura general del proyecto. La finalidad es obtener, al finalizar el semestre, un sistema único, coherente e integrado.

## Requisitos previos

Antes de ejecutar el proyecto, asegúrese de tener instalado lo siguiente:

- Java 17 o superior
- Maven 3.9 o superior
- Docker
- Docker Compose
- IntelliJ IDEA o un IDE equivalente

## Estructura general del proyecto

```text
sigu-ucn/
├── docker-compose.yml
├── pom.xml
├── db/
│   └── init/
│       ├── 01_schema.sql
│       └── 02_seed.sql
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── META-INF/
│   │       │   └── persistence.xml
│   │       ├── jte/
│   │       └── static/
│   └── test/
└── README.md
```

## Tecnologías utilizadas

- Java
- Javalin
- JPA / Hibernate
- PostgreSQL
- Maven
- Docker
- JTE
- jQuery
- JUnit 5
- Mockito

---

## 1. Ejecución de la base de datos con Docker

La base de datos PostgreSQL se ejecuta de manera independiente mediante Docker Compose.

### Levantar la base de datos

Desde la raíz del proyecto, ejecutar:

```bash
docker compose up -d
```

Esto hará lo siguiente:

- levantará un contenedor PostgreSQL;
- creará la base de datos `sigu_ucn`;
- ejecutará automáticamente los scripts:
  - `db/init/01_schema.sql`
  - `db/init/02_seed.sql`

### Verificar estado del contenedor

```bash
docker compose ps
```

### Ver logs del contenedor

```bash
docker compose logs -f
```

### Detener la base de datos

```bash
docker compose down
```

### Reiniciar completamente la base de datos

Si necesita recrear la base desde cero y volver a ejecutar los scripts SQL:

```bash
docker compose down -v
docker compose up -d
```

> **Importante:** los scripts de inicialización se ejecutan solo la primera vez que se crea la base de datos dentro del volumen. Si modifica `01_schema.sql` o `02_seed.sql`, deberá eliminar el volumen con `docker compose down -v`.

---

## 2. Ejecución de la aplicación con Maven

La aplicación Java se ejecuta de manera independiente usando Maven o directamente desde IntelliJ IDEA.

### Compilar el proyecto

```bash
mvn clean compile
```

### Ejecutar la aplicación

Si la clase principal es `cl.ucn.app.Main`, ejecutar:

```bash
mvn exec:java -Dexec.mainClass="cl.ucn.app.Main"
```

También puede ejecutarse directamente desde IntelliJ IDEA usando la clase `Main`.

### Ejecutar tests

```bash
mvn test
```

---

## 3. Orden correcto de ejecución

Para correr el sistema correctamente:

### Paso 1: levantar PostgreSQL con Docker

```bash
docker compose up -d
```

### Paso 2: ejecutar la aplicación Java

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="cl.ucn.app.Main"
```

### Paso 3: abrir en navegador

```text
http://localhost:7000
```

---

## 4. Configuración de persistencia

La configuración JPA se encuentra en:

```text
src/main/resources/META-INF/persistence.xml
```

La configuración general de la aplicación se encuentra en:

```text
src/main/resources/application.properties
```

### Base de datos esperada

- Host: `localhost`
- Puerto: `5432`
- Base de datos: `sigu_ucn`
- Usuario: `postgres`
- Contraseña: `postgres`

---

## 5. Scripts SQL

Los scripts de creación e inicialización se encuentran en:

```text
db/init/
```

### Archivos incluidos

- `01_schema.sql`: crea la estructura base de tablas
- `02_seed.sql`: inserta datos iniciales

---


## 6. Arquitectura del sistema

El proyecto adopta una arquitectura organizada en **3 capas principales**, con el objetivo de mantener una separación clara de responsabilidades, favorecer la mantenibilidad y facilitar el testing.

### 6.1. Capa de presentación

Corresponde a la interacción con el usuario y al manejo de las solicitudes HTTP. Esta capa incluye:

- rutas (`routes/`);
- controladores (`controller/`);
- vistas JTE (`templates/`).

Sus responsabilidades principales son:

- recibir peticiones del navegador;
- obtener parámetros de formularios o rutas;
- delegar la lógica al servicio correspondiente;
- renderizar vistas o redirigir al usuario.

### 6.2. Capa de negocio

Corresponde a la lógica de aplicación y reglas del sistema. Esta capa incluye principalmente:

- servicios (`service/`);
- DTOs, cuando sean necesarios (`dto/`).

Sus responsabilidades principales son:

- validar reglas del dominio;
- coordinar operaciones entre controladores y repositorios;
- encapsular la lógica del sistema sin depender de la interfaz web.

### 6.3. Capa de persistencia

Corresponde al acceso a datos y a la comunicación con la base de datos PostgreSQL. Esta capa incluye:

- repositorios (`repository/`);
- entidades del modelo (`model/`);
- configuración JPA (`config/`, `persistence.xml`).

Sus responsabilidades principales son:

- consultar y actualizar datos;
- mapear objetos Java a tablas mediante JPA/Hibernate;
- aislar el acceso a persistencia del resto del sistema.

### 6.4. Flujo general entre capas

El flujo esperado del sistema es el siguiente:

```text
Navegador
   ↓
Ruta
   ↓
Controlador
   ↓
Servicio
   ↓
Repositorio
   ↓
Base de datos
```

La respuesta vuelve en sentido inverso hasta llegar nuevamente al navegador.

### 6.5. Ejemplo: flujo de autenticación (login)

A continuación se muestra un ejemplo simplificado para ayudar a comprender cómo interactúan las capas del sistema.

#### Paso 1: el usuario accede a la página de login

El navegador solicita la ruta:

```text
/login
```

La ruta correspondiente delega la operación al controlador de autenticación, el cual renderiza la vista `login.jte`.

#### Paso 2: el usuario envía el formulario

El formulario envía una petición `POST` a la ruta:

```text
/login
```

La ruta deriva la solicitud al método `doLogin(...)` del `AuthController`.

#### Paso 3: el controlador recibe los datos

El controlador obtiene los parámetros del formulario:

- correo;
- contraseña.

Luego delega la validación al servicio de autenticación.

#### Paso 4: el servicio ejecuta la lógica del login

El `AuthService` verifica:

- que el usuario exista;
- que esté activo;
- que la contraseña sea válida.

Para ello, utiliza el repositorio correspondiente.

#### Paso 5: el repositorio consulta la base de datos

El `UsuarioRepository` consulta la base de datos usando JPA/Hibernate para buscar el usuario por correo.

#### Paso 6: el controlador decide la respuesta

Si la autenticación es correcta:

- guarda datos del usuario en sesión;
- redirige a `/home`.

Si la autenticación falla:

- vuelve a renderizar `login.jte`;
- muestra un mensaje de error al usuario.

### 6.6. Beneficio de esta arquitectura

Esta organización en capas permite:

- evitar mezclar HTML, reglas de negocio y acceso a datos en una misma clase;
- facilitar el trabajo modular por equipos;
- mejorar la comprensión del proyecto;
- hacer pruebas unitarias e integración de manera más ordenada.


---

## 6. Flujo de trabajo recomendado para estudiantes

1. Clonar el repositorio.
2. Levantar la base de datos con Docker.
3. Abrir el proyecto en IntelliJ IDEA.
4. Verificar que Maven descargue las dependencias.
5. Ejecutar la clase principal `Main`.
6. Trabajar únicamente en la rama asignada al equipo.
7. Subir cambios mediante `git push`.
8. Solicitar integración mediante pull request hacia `develop`.

---

## 7. Notas importantes

- No trabajar directamente sobre `main`.
- No trabajar directamente sobre `develop`.
- Cada equipo debe usar únicamente su rama asignada.
- Los cambios sobre la base de datos deben documentarse y versionarse correctamente.
- Las tablas base no deben modificarse sin coordinación previa.

---

## 8. Solución de problemas comunes

### Error: la aplicación no conecta a la base de datos

Verifique que PostgreSQL esté en ejecución:

```bash
docker compose ps
```

Si no está activo, levántelo con:

```bash
docker compose up -d
```

### Error: las tablas no existen

Reinicie completamente la base:

```bash
docker compose down -v
docker compose up -d
```

### Error: no se encuentra la unidad de persistencia

Verifique que el archivo exista en:

```text
src/main/resources/META-INF/persistence.xml
```

### Error: el puerto 5432 o 7000 está ocupado

Será necesario cambiar el puerto en:

- `docker-compose.yml` para PostgreSQL;
- la configuración de Javalin para la aplicación.

---

## 9. Integración del proyecto

La base del sistema será común para todos los equipos. Cada grupo desarrollará un módulo funcional dentro de la misma arquitectura, respetando:

- la estructura de paquetes;
- la configuración base;
- el modelo de persistencia;
- la navegación principal del sistema.
