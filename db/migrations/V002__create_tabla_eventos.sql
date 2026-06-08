CREATE TABLE eventos (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    tematica VARCHAR(100),
    capacidad INTEGER NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'PLANIFICADO',
    espacio_id BIGINT NOT NULL,
    CONSTRAINT fk_eventos_espacios
        FOREIGN KEY (espacio_id)
            REFERENCES espacios(id)
            ON UPDATE CASCADE
            ON DELETE RESTRICT,
    CONSTRAINT chk_eventos_capacidad CHECK (capacidad > 0),
    CONSTRAINT chk_eventos_estado CHECK (estado IN ('PLANIFICADO','CONFIRMADO','EN_CURSO','FINALIZADO','CANCELADO')),
    CONSTRAINT chk_eventos_horas CHECK (hora_inicio < hora_fin)
);

CREATE INDEX idx_eventos_fecha ON eventos(fecha);
CREATE INDEX idx_eventos_tematica ON eventos(tematica);
CREATE INDEX idx_eventos_estado ON eventos(estado);
CREATE INDEX idx_eventos_espacio_id ON eventos(espacio_id);
