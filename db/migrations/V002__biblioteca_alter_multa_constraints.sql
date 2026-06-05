-- Agregar columna fecha_generacion a tabla multa
ALTER TABLE multa ADD COLUMN fecha_generacion DATE NOT NULL DEFAULT CURRENT_DATE;

-- Hacer prestamo_id UNIQUE para garantizar relación 1:1 entre Multa y PrestamoLibro
ALTER TABLE multa ADD CONSTRAINT uk_multa_prestamo_id UNIQUE (prestamo_id);

-- Agregar CHECK en ejemplar para restricción de estados válidos
ALTER TABLE ejemplar ADD CONSTRAINT chk_ejemplar_estado
  CHECK (estado IN ('DISPONIBLE', 'PRESTADO'));

-- Agregar CHECK en prestamo para restricción de estados válidos
ALTER TABLE prestamo ADD CONSTRAINT chk_prestamo_estado
  CHECK (estado IN ('ACTIVO', 'FINALIZADO', 'ATRASADO'));

