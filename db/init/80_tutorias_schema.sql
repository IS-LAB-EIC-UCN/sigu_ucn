-- Grupo 8 (Tutorías) - Esquema
-- Tablas propias con prefijo tut_

CREATE TABLE tut_asignaturas (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(120) NOT NULL,
    carrera VARCHAR(120) NOT NULL,
    semestre INTEGER NOT NULL,
    CONSTRAINT chk_tut_asignaturas_semestre
        CHECK (semestre BETWEEN 1 AND 12)
);

CREATE TABLE tut_estudiantes (
    id BIGSERIAL PRIMARY KEY,
    correo VARCHAR(120) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    carrera VARCHAR(120) NOT NULL,
    semestre INTEGER NOT NULL,
    usuario_id BIGINT UNIQUE,
    CONSTRAINT fk_tut_estudiantes_usuarios
        FOREIGN KEY (usuario_id)
            REFERENCES usuarios(id)
            ON UPDATE CASCADE
            ON DELETE RESTRICT,
    CONSTRAINT chk_tut_estudiantes_semestre
        CHECK (semestre BETWEEN 1 AND 12)
);

CREATE TABLE tut_estudiante_asignaturas (
    estudiante_id BIGINT NOT NULL,
    asignatura_id BIGINT NOT NULL,
    PRIMARY KEY (estudiante_id, asignatura_id),
    CONSTRAINT fk_tut_est_asig_estudiantes
        FOREIGN KEY (estudiante_id)
            REFERENCES tut_estudiantes(id)
            ON DELETE CASCADE,
    CONSTRAINT fk_tut_est_asig_asignaturas
        FOREIGN KEY (asignatura_id)
            REFERENCES tut_asignaturas(id)
            ON DELETE CASCADE
);

CREATE TABLE tut_tutores (
    id BIGSERIAL PRIMARY KEY,
    estudiante_id BIGINT NOT NULL UNIQUE,
    certificado BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_tut_tutores_estudiantes
        FOREIGN KEY (estudiante_id)
            REFERENCES tut_estudiantes(id)
            ON DELETE RESTRICT
);

CREATE TABLE tut_tutor_asignaturas (
    tutor_id BIGINT NOT NULL,
    asignatura_id BIGINT NOT NULL,
    PRIMARY KEY (tutor_id, asignatura_id),
    CONSTRAINT fk_tut_tutor_asig_tutores
        FOREIGN KEY (tutor_id)
            REFERENCES tut_tutores(id)
            ON DELETE CASCADE,
    CONSTRAINT fk_tut_tutor_asig_asignaturas
        FOREIGN KEY (asignatura_id)
            REFERENCES tut_asignaturas(id)
            ON DELETE CASCADE
);

CREATE TABLE tut_tutorias (
    id BIGSERIAL PRIMARY KEY,
    tutor_id BIGINT NOT NULL,
    asignatura_id BIGINT NOT NULL,
    inicio TIMESTAMP NOT NULL,
    fin TIMESTAMP NOT NULL,
    modalidad VARCHAR(20) NOT NULL,
    sala VARCHAR(50),
    capacidad INTEGER NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PUBLICADA',
    motivo_cancelacion VARCHAR(255),
    CONSTRAINT fk_tut_tutorias_tutores
        FOREIGN KEY (tutor_id)
            REFERENCES tut_tutores(id)
            ON DELETE RESTRICT,
    CONSTRAINT fk_tut_tutorias_asignaturas
        FOREIGN KEY (asignatura_id)
            REFERENCES tut_asignaturas(id)
            ON DELETE RESTRICT,
    CONSTRAINT chk_tut_tutorias_duracion
        CHECK (fin >= inicio + INTERVAL '30 minutes'),
    CONSTRAINT chk_tut_tutorias_horario
        CHECK (inicio::date = fin::date
            AND inicio::time >= TIME '07:00'
            AND fin::time <= TIME '22:00'),
    CONSTRAINT chk_tut_tutorias_capacidad
        CHECK (capacidad > 0),
    CONSTRAINT chk_tut_tutorias_modalidad
        CHECK (modalidad IN ('ONLINE', 'PRESENCIAL')),
    CONSTRAINT chk_tut_tutorias_sala
        CHECK (modalidad = 'ONLINE' OR sala IS NOT NULL),
    CONSTRAINT chk_tut_tutorias_estado
        CHECK (estado IN ('PUBLICADA', 'CANCELADA', 'REALIZADA'))
);

CREATE TABLE tut_inscripciones (
    id BIGSERIAL PRIMARY KEY,
    tutoria_id BIGINT NOT NULL,
    estudiante_id BIGINT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'RESERVADA',
    fecha_inscripcion TIMESTAMP NOT NULL DEFAULT NOW(),
    motivo_cancelacion VARCHAR(255),
    asistio BOOLEAN,
    CONSTRAINT fk_tut_inscripciones_tutorias
        FOREIGN KEY (tutoria_id)
            REFERENCES tut_tutorias(id)
            ON DELETE RESTRICT,
    CONSTRAINT fk_tut_inscripciones_estudiantes
        FOREIGN KEY (estudiante_id)
            REFERENCES tut_estudiantes(id)
            ON DELETE RESTRICT,
    CONSTRAINT chk_tut_inscripciones_estado
        CHECK (estado IN ('RESERVADA', 'CANCELADA'))
);

CREATE UNIQUE INDEX ux_tut_inscripciones_activa
    ON tut_inscripciones (tutoria_id, estudiante_id)
    WHERE estado = 'RESERVADA';

CREATE INDEX idx_tut_tutorias_tutor_id ON tut_tutorias(tutor_id);
CREATE INDEX idx_tut_tutorias_asignatura_id ON tut_tutorias(asignatura_id);
CREATE INDEX idx_tut_tutorias_inicio ON tut_tutorias(inicio);
CREATE INDEX idx_tut_inscripciones_estudiante_id ON tut_inscripciones(estudiante_id);
