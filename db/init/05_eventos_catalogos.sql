-- Ampliaci?n exclusiva del m?dulo 6. Ejecutable en una base existente sin borrar datos.
CREATE TABLE IF NOT EXISTS expositores (
 id BIGSERIAL PRIMARY KEY, nombre VARCHAR(100) NOT NULL,
 correo VARCHAR(120), especialidad VARCHAR(150)
);
ALTER TABLE eventos ADD COLUMN IF NOT EXISTS tematica VARCHAR(100);
ALTER TABLE eventos ADD COLUMN IF NOT EXISTS expositor_id BIGINT REFERENCES expositores(id);
ALTER TABLE eventos ADD COLUMN IF NOT EXISTS espacio_id BIGINT REFERENCES espacios(id);
CREATE INDEX IF NOT EXISTS idx_eventos_sala_fecha ON eventos(espacio_id, fecha);
CREATE INDEX IF NOT EXISTS idx_eventos_tematica ON eventos(tematica);
