
-- Grupo 6: Inscripciones a eventos académicos

CREATE TABLE IF NOT EXISTS inscripciones (
    id BIGSERIAL PRIMARY KEY,

    evento_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,

    fecha_inscripcion TIMESTAMP NOT NULL
        DEFAULT CURRENT_TIMESTAMP,

    estado VARCHAR(20) NOT NULL
        DEFAULT 'INSCRITO',

    CONSTRAINT fk_inscripciones_evento
        FOREIGN KEY (evento_id)
        REFERENCES eventos(id),

    CONSTRAINT fk_inscripciones_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id),

    CONSTRAINT uq_inscripcion_evento_usuario
        UNIQUE (evento_id, usuario_id),

    CONSTRAINT chk_inscripciones_estado
        CHECK (estado IN (
            'INSCRITO',
            'CANCELADO'
        ))
);

-- Facilitar consultas de participantes por evento
CREATE INDEX IF NOT EXISTS idx_inscripciones_evento
    ON inscripciones(evento_id);

-- Facilitar consultas de eventos de un usuario
CREATE INDEX IF NOT EXISTS idx_inscripciones_usuario
    ON inscripciones(usuario_id);
