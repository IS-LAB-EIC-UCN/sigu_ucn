DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'multa' AND column_name = 'fecha_generacion'
    ) THEN
        ALTER TABLE multa ADD COLUMN fecha_generacion DATE NOT NULL DEFAULT CURRENT_DATE;
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'uk_multa_prestamo_id'
    ) THEN
        ALTER TABLE multa ADD CONSTRAINT uk_multa_prestamo_id UNIQUE (prestamo_id);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'chk_ejemplar_estado'
    ) THEN
        ALTER TABLE ejemplar ADD CONSTRAINT chk_ejemplar_estado
            CHECK (estado IN ('DISPONIBLE', 'PRESTADO'));
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'chk_prestamo_estado'
    ) THEN
        ALTER TABLE prestamo ADD CONSTRAINT chk_prestamo_estado
            CHECK (estado IN ('ACTIVO', 'FINALIZADO', 'ATRASADO'));
    END IF;
END $$;
