-- =====================================================================
-- Grupo 1 - Sistema de reservas de salas universitarias
-- Extiende las tablas base (espacios, reservas) sin modificar 01_scheme.sql.
-- Todos los cambios son aditivos y compatibles con el resto de los módulos.
-- =====================================================================

-- Necesaria para combinar igualdad (espacio_id) y rangos (&&) en un índice GiST.
CREATE EXTENSION IF NOT EXISTS btree_gist;

-- ---------------------------------------------------------------------
-- espacios: ubicación de la sala (RF-03)
-- ---------------------------------------------------------------------
ALTER TABLE espacios
    ADD COLUMN IF NOT EXISTS ubicacion VARCHAR(150);

-- ---------------------------------------------------------------------
-- reservas: motivo (RF-05), motivo de rechazo (RF-07),
--           fecha de creación para ordenar pendientes (RF-11)
-- ---------------------------------------------------------------------
ALTER TABLE reservas
    ADD COLUMN IF NOT EXISTS motivo         VARCHAR(255),
    ADD COLUMN IF NOT EXISTS motivo_rechazo VARCHAR(255),
    ADD COLUMN IF NOT EXISTS fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- RF-07: toda reserva rechazada debe registrar su motivo.
ALTER TABLE reservas
    ADD CONSTRAINT chk_reservas_motivo_rechazo
        CHECK (estado <> 'RECHAZADA' OR motivo_rechazo IS NOT NULL);

-- ---------------------------------------------------------------------
-- RN-01 / RF-10 / RNF-04: una sala no puede tener dos reservas activas
-- (PENDIENTE o APROBADA) con horarios solapados.
-- La restricción vive en la base de datos, por lo que también protege
-- frente a solicitudes concurrentes. El rango es semiabierto [inicio, fin),
-- así una reserva 09:00-10:00 y otra 10:00-11:00 NO se consideran en conflicto.
-- ---------------------------------------------------------------------
ALTER TABLE reservas
    ADD CONSTRAINT excl_reservas_sin_solape
        EXCLUDE USING gist (
            espacio_id WITH =,
            tsrange(fecha_reserva + hora_inicio, fecha_reserva + hora_fin, '[)') WITH &&
        )
        WHERE (estado IN ('PENDIENTE', 'APROBADA'));

-- Índices para el historial y los filtros por estado (RF-08, RF-09, RF-11)
CREATE INDEX IF NOT EXISTS idx_reservas_estado         ON reservas(estado);
CREATE INDEX IF NOT EXISTS idx_reservas_fecha_creacion ON reservas(fecha_creacion);
