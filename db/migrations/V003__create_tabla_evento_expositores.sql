CREATE TABLE evento_expositores (
    evento_id BIGINT NOT NULL,
    expositor_id BIGINT NOT NULL,
    PRIMARY KEY (evento_id, expositor_id),
    CONSTRAINT fk_ee_eventos
        FOREIGN KEY (evento_id)
            REFERENCES eventos(id)
            ON UPDATE CASCADE
            ON DELETE CASCADE,
    CONSTRAINT fk_ee_expositores
        FOREIGN KEY (expositor_id)
            REFERENCES expositores(id)
            ON UPDATE CASCADE
            ON DELETE RESTRICT
);

CREATE INDEX idx_ee_evento_id ON evento_expositores(evento_id);
CREATE INDEX idx_ee_expositor_id ON evento_expositores(expositor_id);
