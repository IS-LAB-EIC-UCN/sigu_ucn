
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nombre_usuario VARCHAR(100) NOT NULL,
    correo VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    rol_id BIGINT NOT NULL,
    CONSTRAINT fk_usuarios_roles
        FOREIGN KEY (rol_id)
        REFERENCES roles(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);


CREATE TABLE categorias_ticket (
    id BIGSERIAL PRIMARY KEY,
    nombre_categoria VARCHAR(64) NOT NULL,
    contenido TEXT NOT NULL
);

CREATE TABLE tickets (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    descripcion TEXT NOT NULL,
    prioridad VARCHAR(16) NOT NULL DEFAULT 'MEDIA',
    estado VARCHAR(16) NOT NULL DEFAULT 'ABIERTO',
    resolucion TEXT,
    fecha_creacion TIMESTAMP DEFAULT NOW(),
    fecha_cierre TIMESTAMP,
    solicitante_id BIGINT,
    tecnico_id BIGINT,
    categoria_id BIGINT,
    CONSTRAINT fk_tickets_solicitante
        FOREIGN KEY (solicitante_id)
        REFERENCES usuarios(id)
        ON UPDATE CASCADE
        ON DELETE SET NULL,
    CONSTRAINT fk_tickets_tecnico
        FOREIGN KEY (tecnico_id)
        REFERENCES usuarios(id)
        ON UPDATE CASCADE
        ON DELETE SET NULL,
    CONSTRAINT fk_tickets_categoria
        FOREIGN KEY (categoria_id)
        REFERENCES categorias_ticket(id)
        ON UPDATE CASCADE
        ON DELETE SET NULL,
    CONSTRAINT chk_tickets_prioridad
        CHECK (prioridad IN ('BAJA', 'MEDIA', 'ALTA')),
    CONSTRAINT chk_tickets_estado
        CHECK (estado IN ('ABIERTO', 'EN_PROCESO', 'CERRADO'))
);

CREATE TABLE comentarios (
    id BIGSERIAL PRIMARY KEY,
    contenido TEXT NOT NULL,
    fecha TIMESTAMP DEFAULT NOW(),
    ticket_id BIGINT,
    autor_id BIGINT,
    CONSTRAINT fk_comentarios_ticket
        FOREIGN KEY (ticket_id)
        REFERENCES tickets(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_comentarios_autor
        FOREIGN KEY (autor_id)
        REFERENCES usuarios(id)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);


CREATE INDEX idx_usuarios_rol_id ON usuarios(rol_id);
CREATE INDEX idx_tickets_solicitante_id ON tickets(solicitante_id);
CREATE INDEX idx_tickets_tecnico_id ON tickets(tecnico_id);
CREATE INDEX idx_tickets_categoria_id ON tickets(categoria_id);
CREATE INDEX idx_tickets_estado ON tickets(estado);
CREATE INDEX idx_tickets_prioridad ON tickets(prioridad);
CREATE INDEX idx_tickets_fecha_creacion ON tickets(fecha_creacion);
CREATE INDEX idx_comentarios_ticket_id ON comentarios(ticket_id);
-- ============================================================
-- 01_schema.sql
-- Esquema base del sistema SIGU-UCN
-- Generado a partir de las entidades JPA del proyecto.
-- ============================================================

-- ── Tabla: roles ─────────────────────────────────────────────
CREATE TABLE roles (
    id      SERIAL PRIMARY KEY,
    nombre  VARCHAR(50) NOT NULL UNIQUE
);

-- ── Tabla: usuarios ──────────────────────────────────────────
CREATE TABLE usuarios (
    id              SERIAL PRIMARY KEY,
    nombre_usuario  VARCHAR(100) NOT NULL,
    correo          VARCHAR(120) NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    activo          BOOLEAN NOT NULL DEFAULT TRUE,
    rol_id          INT NOT NULL REFERENCES roles(id)
);

-- ── Tabla: espacios ──────────────────────────────────────────
CREATE TABLE espacios (
    id          SERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    tipo        VARCHAR(50) NOT NULL,
    capacidad   INT NOT NULL,
    disponible  BOOLEAN NOT NULL DEFAULT TRUE
);

-- ── Tabla: reservas ──────────────────────────────────────────
CREATE TABLE reservas (
    id              SERIAL PRIMARY KEY,
    fecha_reserva   DATE NOT NULL,
    hora_inicio     TIME NOT NULL,
    hora_fin        TIME NOT NULL,
    estado          VARCHAR(30) NOT NULL,
    usuario_id      INT NOT NULL REFERENCES usuarios(id),
    espacio_id      INT NOT NULL REFERENCES espacios(id)
);

-- ── Tabla: categorias_ticket ─────────────────────────────────
CREATE TABLE categorias_ticket (
    id                SERIAL PRIMARY KEY,
    nombre_categoria  VARCHAR(64) NOT NULL,
    contenido         TEXT NOT NULL
);

-- ── Tabla: tickets ───────────────────────────────────────────
-- Estados válidos: ABIERTO | EN_PROCESO | CERRADO
-- Prioridades válidas: BAJA | MEDIA | ALTA
CREATE TABLE tickets (
    id              SERIAL PRIMARY KEY,
    titulo          VARCHAR(100) NOT NULL,
    descripcion     TEXT NOT NULL,
    prioridad       VARCHAR(16) NOT NULL DEFAULT 'MEDIA'
                    CHECK (prioridad IN ('BAJA', 'MEDIA', 'ALTA')),
    estado          VARCHAR(16) NOT NULL DEFAULT 'ABIERTO'
                    CHECK (estado IN ('ABIERTO', 'EN_PROCESO', 'CERRADO')),
    resolucion      TEXT,
    fecha_creacion  TIMESTAMP DEFAULT NOW(),
    fecha_cierre    TIMESTAMP,
    solicitante_id  INT REFERENCES usuarios(id),
    tecnico_id      INT REFERENCES usuarios(id),
    categoria_id    INT REFERENCES categorias_ticket(id)
);

-- ── Tabla: comentarios ───────────────────────────────────────
CREATE TABLE comentarios (
    id          SERIAL PRIMARY KEY,
    contenido   TEXT NOT NULL,
    fecha       TIMESTAMP DEFAULT NOW(),
    ticket_id   INT REFERENCES tickets(id),
    autor_id    INT REFERENCES usuarios(id)
);

-- ── Índices ──────────────────────────────────────────────────
CREATE INDEX idx_comentarios_autor_id ON comentarios(autor_id);
CREATE INDEX idx_comentarios_ticket_id ON comentarios(ticket_id);
CREATE INDEX idx_tickets_solicitante_id ON tickets(solicitante_id);
CREATE INDEX idx_tickets_tecnico_id ON tickets(tecnico_id);
CREATE INDEX idx_reservas_usuario_id ON reservas(usuario_id);
CREATE INDEX idx_reservas_espacio_id ON reservas(espacio_id);