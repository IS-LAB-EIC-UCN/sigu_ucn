CREATE TABLE usuario (
    id SERIAL PRIMARY KEY,
    nombre_usuario VARCHAR(100) NOT NULL,
    correo VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(16) NOT NULL,
    rol VARCHAR(16) NOT NULL DEFAULT 'USUARIO'
);
CREATE TABLE categoria_ticket (
    id SERIAL PRIMARY KEY,
    nombre_categoria VARCHAR (64) NOT NULL,
    contenido TEXT NOT NULL
);

CREATE TABLE ticket (
    id_ticket SERIAL PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    descripcion TEXT NOT NULL,
    categoria VARCHAR(64),
    prioridad VARCHAR(16) NOT NULL DEFAULT 'MEDIA',
    estado VARCHAR(16) NOT NULL DEFAULT 'ABIERTO',
    resolucion TEXT,
    fecha_creacion TIMESTAMP DEFAULT NOW(),
    fecha_cierre TIMESTAMP,
    solicitante_id INT REFERENCES usuario(id_usuario),
    tecnico_id INT REFERENCES usuario(id_usuario),
    categoria_id INT REFERENCES categoria_ticket(id)
);

CREATE TABLE comentario (
    id_comentario SERIAL PRIMARY KEY,
    contenido TEXT NOT NULL,
    fecha TIMESTAMP DEFAULT NOW(),
    ticket_id INT REFERENCES ticket(id),
    autor_id INT REFERENCES usuario(id)
);