-- HU02: Asignacion de tickets a tecnicos.

ALTER TABLE tickets ADD COLUMN IF NOT EXISTS tecnico_id BIGINT;
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_tickets_tecnico'
    ) THEN
ALTER TABLE tickets
    ADD CONSTRAINT fk_tickets_tecnico
    FOREIGN KEY (tecnico_id) REFERENCES usuarios(id);
END IF;
END;
$$;

CREATE INDEX IF NOT EXISTS idx_tickets_tecnico_id ON tickets(tecnico_id);