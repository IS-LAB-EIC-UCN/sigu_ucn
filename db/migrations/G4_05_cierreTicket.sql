-- HU05: Resolucion y cierre de tickets.

ALTER TABLE tickets ADD COLUMN IF NOT EXISTS resolucion TEXT;
ALTER TABLE tickets ADD COLUMN IF NOT EXISTS fecha_cierre TIMESTAMP;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'chk_tickets_cierre'
    ) THEN
ALTER TABLE tickets
    ADD CONSTRAINT chk_tickets_cierre
    CHECK (estado <> 'CERRADO' OR (resolucion IS NOT NULL AND fecha_cierre IS NOT NULL));
END IF;
END;
$$;
