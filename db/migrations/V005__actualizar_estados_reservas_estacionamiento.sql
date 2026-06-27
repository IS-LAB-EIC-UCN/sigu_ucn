ALTER TABLE reservas
DROP CONSTRAINT IF EXISTS chk_reservas_estado;

ALTER TABLE reservas
    ADD CONSTRAINT chk_reservas_estado
        CHECK (estado IN ('PENDIENTE', 'APROBADA', 'RECHAZADA', 'CANCELADA', 'ACTIVA', 'FINALIZADA'));