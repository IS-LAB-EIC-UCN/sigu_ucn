-- =====================================================================
-- Grupo 3 - Sistema de inscripción a talleres
-- Migración V001: tablas talleres e inscripciones
-- Aplicar sobre una base ya inicializada con db/init/01_scheme.sql
-- =====================================================================

CREATE TABLE talleres (
    id            BIGSERIAL PRIMARY KEY,
    nombre        VARCHAR(120) NOT NULL,
    descripcion   VARCHAR(500),
    categoria     VARCHAR(50)  NOT NULL,
    fecha         DATE         NOT NULL,
    hora_inicio   TIME         NOT NULL,
    hora_fin      TIME         NOT NULL,
    lugar         VARCHAR(100),
    cupo_maximo   INTEGER      NOT NULL,
    estado        VARCHAR(20)  NOT NULL DEFAULT 'ACTIVO',
    CONSTRAINT chk_talleres_cupo   CHECK (cupo_maximo > 0),
    CONSTRAINT chk_talleres_horas  CHECK (hora_inicio < hora_fin),
    CONSTRAINT chk_talleres_estado CHECK (estado IN ('ACTIVO', 'CANCELADO'))
);

CREATE TABLE inscripciones (
    id                 BIGSERIAL PRIMARY KEY,
    taller_id          BIGINT      NOT NULL,
    usuario_id         BIGINT      NOT NULL,
    estado             VARCHAR(20) NOT NULL,
    fecha_inscripcion  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_anulacion    TIMESTAMP,
    CONSTRAINT fk_inscripciones_talleres
        FOREIGN KEY (taller_id) REFERENCES talleres(id)
            ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_inscripciones_usuarios
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
            ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_inscripciones_estado
        CHECK (estado IN ('INSCRITO', 'EN_ESPERA', 'ANULADA'))
);

-- Regla de negocio reforzada en la base: un participante no puede tener
-- dos inscripciones vigentes (INSCRITO o EN_ESPERA) en el mismo taller.
-- Las anuladas no cuentan, para permitir volver a inscribirse.
CREATE UNIQUE INDEX uq_inscripcion_vigente
    ON inscripciones (taller_id, usuario_id)
    WHERE estado <> 'ANULADA';

CREATE INDEX idx_talleres_fecha       ON talleres(fecha);
CREATE INDEX idx_talleres_categoria   ON talleres(categoria);
CREATE INDEX idx_inscripciones_taller ON inscripciones(taller_id);
CREATE INDEX idx_inscripciones_usuario ON inscripciones(usuario_id);
