-- HU01: Estructura inicial para crear tickets.

CREATE TABLE tickets (id BIGSERIAL PRIMARY KEY, titulo VARCHAR(150) NOT NULL, descripcion TEXT NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'CREADO', fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_id BIGINT NOT NULL,
    CONSTRAINT fk_tickets_usuarios
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_tickets_estado
    CHECK (estado IN ('CREADO', 'ASIGNADO', 'EN_RESOLUCION', 'CERRADO')),
    CONSTRAINT chk_tickets_titulo CHECK (LENGTH(TRIM(titulo)) > 0),
    CONSTRAINT chk_tickets_descripcion CHECK (LENGTH(TRIM(descripcion)) > 0)
);

CREATE INDEX idx_tickets_usuario_id
    ON tickets(usuario_id);
