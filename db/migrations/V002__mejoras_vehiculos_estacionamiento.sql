ALTER TABLE vehiculos
    ADD CONSTRAINT uq_vehiculos_patente UNIQUE (patente);

CREATE INDEX IF NOT EXISTS idx_vehiculos_usuario_id
    ON vehiculos(usuario_id);

CREATE INDEX IF NOT EXISTS idx_vehiculos_patente
    ON vehiculos(patente);