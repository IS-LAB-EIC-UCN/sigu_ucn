
CREATE TABLE IF NOT EXISTS eventos (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    lugar VARCHAR(150) NOT NULL,
    cupo_maximo INTEGER NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'BORRADOR',
    organizador_id BIGINT NOT NULL,

    CONSTRAINT fk_eventos_organizador
        FOREIGN KEY (organizador_id)
        REFERENCES usuarios(id),

    CONSTRAINT chk_eventos_cupo
        CHECK (cupo_maximo > 0),

    CONSTRAINT chk_eventos_horario
        CHECK (hora_inicio < hora_fin),

    CONSTRAINT chk_eventos_estado
        CHECK (estado IN (
            'BORRADOR',
            'PUBLICADO',
            'CANCELADO',
            'FINALIZADO'
        ))
);

CREATE INDEX IF NOT EXISTS idx_eventos_fecha
    ON eventos(fecha);

CREATE INDEX IF NOT EXISTS idx_eventos_organizador
    ON eventos(organizador_id);
