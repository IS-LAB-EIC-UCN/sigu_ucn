-- RF-03/RF-12: categoría persistente y migración de las temáticas existentes.
BEGIN;
CREATE TABLE IF NOT EXISTS categorias(id BIGSERIAL PRIMARY KEY, nombre VARCHAR(100) NOT NULL);
CREATE UNIQUE INDEX IF NOT EXISTS uq_categorias_nombre ON categorias(lower(trim(nombre)));
ALTER TABLE eventos ADD COLUMN IF NOT EXISTS categoria_id BIGINT REFERENCES categorias(id);
INSERT INTO categorias(nombre) SELECT DISTINCT COALESCE(NULLIF(trim(tematica),''),'General') FROM eventos ON CONFLICT DO NOTHING;
INSERT INTO categorias(nombre) VALUES ('General'),('Tecnología'),('Investigación'),('Innovación'),('Ciencia de datos') ON CONFLICT DO NOTHING;
UPDATE eventos e SET categoria_id=c.id FROM categorias c WHERE e.categoria_id IS NULL AND lower(trim(c.nombre))=lower(COALESCE(NULLIF(trim(e.tematica),''),'General'));
CREATE INDEX IF NOT EXISTS idx_eventos_categoria ON eventos(categoria_id);
COMMIT;
