-- Ejecutar después de 001-004 con psql -v ON_ERROR_STOP=1 -f <archivo>.
-- Los intervalos incluyen el inicio y excluyen el fin para permitir reservas consecutivas.
BEGIN;

LOCK TABLE reservas_estacionamiento IN ACCESS EXCLUSIVE MODE;

DO $$
DECLARE
    conflictos TEXT;
BEGIN
    SELECT string_agg(format('%s y %s', a.id, b.id), ', ' ORDER BY a.id, b.id)
    INTO conflictos
    FROM reservas_estacionamiento a
    JOIN reservas_estacionamiento b
      ON a.id < b.id
     AND a.fecha_hora_inicio < b.fecha_hora_fin
     AND a.fecha_hora_fin > b.fecha_hora_inicio
    WHERE a.estado IN ('PENDIENTE', 'APROBADA')
      AND b.estado IN ('PENDIENTE', 'APROBADA')
      AND (a.estacionamiento_id = b.estacionamiento_id OR a.vehiculo_id = b.vehiculo_id);

    IF conflictos IS NOT NULL THEN
        RAISE EXCEPTION 'Reservas incompatibles (pares de IDs): %. Resolver con autorización antes de repetir la migración.', conflictos;
    END IF;
END $$;

CREATE EXTENSION IF NOT EXISTS btree_gist;

ALTER TABLE reservas_estacionamiento
    ADD CONSTRAINT excl_reservas_est_espacio_horario
    EXCLUDE USING gist (
        estacionamiento_id WITH =,
        tsrange(fecha_hora_inicio, fecha_hora_fin, '[)') WITH &&
    ) WHERE (estado IN ('PENDIENTE', 'APROBADA'));

ALTER TABLE reservas_estacionamiento
    ADD CONSTRAINT excl_reservas_est_vehiculo_horario
    EXCLUDE USING gist (
        vehiculo_id WITH =,
        tsrange(fecha_hora_inicio, fecha_hora_fin, '[)') WITH &&
    ) WHERE (estado IN ('PENDIENTE', 'APROBADA'));

COMMIT;
