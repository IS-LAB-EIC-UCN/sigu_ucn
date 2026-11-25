
ALTER TABLE estacionamientos
ADD COLUMN tipo VARCHAR(20) NOT NULL DEFAULT 'COMUN';

ALTER TABLE estacionamientos
ADD CONSTRAINT chk_estacionamientos_tipo
CHECK (tipo IN ('COMUN', 'DOCENTES'));
