CREATE TABLE expositores (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    email VARCHAR(120),
    afiliacion VARCHAR(200),
    telefono VARCHAR(20),
    bio TEXT
);

CREATE TABLE eventos (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    tematica VARCHAR(100),
    capacidad INTEGER NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'PLANIFICADO',
    espacio_id BIGINT NOT NULL,
    CONSTRAINT fk_eventos_espacios
        FOREIGN KEY (espacio_id)
            REFERENCES espacios(id)
            ON UPDATE CASCADE
            ON DELETE RESTRICT,
    CONSTRAINT chk_eventos_capacidad CHECK (capacidad > 0),
    CONSTRAINT chk_eventos_estado CHECK (estado IN ('PLANIFICADO','CONFIRMADO','EN_CURSO','FINALIZADO','CANCELADO')),
    CONSTRAINT chk_eventos_horas CHECK (hora_inicio < hora_fin)
);

CREATE TABLE evento_expositores (
    evento_id BIGINT NOT NULL,
    expositor_id BIGINT NOT NULL,
    PRIMARY KEY (evento_id, expositor_id),
    CONSTRAINT fk_ee_eventos
        FOREIGN KEY (evento_id)
            REFERENCES eventos(id)
            ON UPDATE CASCADE
            ON DELETE CASCADE,
    CONSTRAINT fk_ee_expositores
        FOREIGN KEY (expositor_id)
            REFERENCES expositores(id)
            ON UPDATE CASCADE
            ON DELETE RESTRICT
);

INSERT INTO expositores (nombre, email, afiliacion, telefono, bio) VALUES
    ('Dr. Juan Pérez', 'jperez@ucn.cl', 'Universidad Católica del Norte', '+56 9 1234 5678', 'Especialista en inteligencia artificial y machine learning.'),
    ('Dra. María González', 'mgonzalez@ucn.cl', 'Universidad Católica del Norte', '+56 9 8765 4321', 'Investigadora en ciencias ambientales y sustentabilidad.'),
    ('Mg. Carlos López', 'clopez@ucn.cl', 'Universidad de Chile', '+56 9 5566 7788', 'Experto en gestión de proyectos tecnológicos e innovación.');

CREATE INDEX idx_eventos_fecha ON eventos(fecha);
CREATE INDEX idx_eventos_tematica ON eventos(tematica);
CREATE INDEX idx_eventos_estado ON eventos(estado);
CREATE INDEX idx_eventos_espacio_id ON eventos(espacio_id);
CREATE INDEX idx_ee_evento_id ON evento_expositores(evento_id);
CREATE INDEX idx_ee_expositor_id ON evento_expositores(expositor_id);

CREATE TABLE inscripciones (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    evento_id BIGINT NOT NULL,
    fecha_inscripcion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_inscripciones_usuarios
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_inscripciones_eventos
        FOREIGN KEY (evento_id)
        REFERENCES eventos(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT uq_usuario_evento
        UNIQUE (usuario_id, evento_id)
);

CREATE INDEX idx_inscripciones_usuario_id ON inscripciones(usuario_id);
CREATE INDEX idx_inscripciones_evento_id ON inscripciones(evento_id);

