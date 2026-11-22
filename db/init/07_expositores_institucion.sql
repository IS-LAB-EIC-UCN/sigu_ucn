-- RF-11: conservar la institución además de la especialidad.
ALTER TABLE expositores ADD COLUMN IF NOT EXISTS institucion VARCHAR(150) NOT NULL DEFAULT '';
