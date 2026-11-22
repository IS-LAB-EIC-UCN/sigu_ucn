-- RF-06: conservar asistencia por inscripción, sin alterar su estado.
ALTER TABLE inscripciones ADD COLUMN IF NOT EXISTS asistencia BOOLEAN NOT NULL DEFAULT FALSE;
