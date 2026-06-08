CREATE TABLE expositores (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    email VARCHAR(120),
    afiliacion VARCHAR(200),
    telefono VARCHAR(20),
    bio TEXT
);
