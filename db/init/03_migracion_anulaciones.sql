-- Migracion para añadir soporte a anulaciones pendientes con justificacion

ALTER TABLE inscripciones ADD COLUMN justificacion TEXT;

ALTER TABLE inscripciones DROP CONSTRAINT chk_inscripciones_estado;

ALTER TABLE inscripciones ADD CONSTRAINT chk_inscripciones_estado 
    CHECK (estado IN ('INSCRITO', 'EN_ESPERA', 'CANCELADO', 'ANULACION_PENDIENTE'));
