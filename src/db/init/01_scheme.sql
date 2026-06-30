
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
CREATE INDEX idx_comentarios_autor_id ON comentarios(autor_id);