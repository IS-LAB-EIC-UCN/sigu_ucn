ALTER TABLE prestamo DROP CONSTRAINT IF EXISTS chk_prestamo_estado;
ALTER TABLE prestamo ADD CONSTRAINT chk_prestamo_estado
    CHECK (estado IN ('ACTIVO', 'FINALIZADO'));
