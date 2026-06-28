CREATE TABLE inscripciones (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    evento_id BIGINT NOT NULL,
    fecha_inscripcion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_inscripciones_usuarios
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_inscripciones_eventos
        FOREIGN KEY (evento_id)
        REFERENCES eventos(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT uq_usuario_evento
        UNIQUE (usuario_id, evento_id)
);

CREATE INDEX idx_inscripciones_usuario_id ON inscripciones(usuario_id);
CREATE INDEX idx_inscripciones_evento_id ON inscripciones(evento_id);
