-- ==========================================================
-- Módulo Biblioteca (Grupo 2) - Esquema
-- Crea las tablas bibliotecarios, lectores, categorias, autores,
-- libros, libros_autores, ejemplares, prestamos y multas,
-- con sus restricciones e índices.
-- ==========================================================

CREATE TABLE bibliotecarios (
    id BIGSERIAL PRIMARY KEY,
    rut VARCHAR(12) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(120) NOT NULL UNIQUE
);

CREATE TABLE lectores (
    id BIGSERIAL PRIMARY KEY,
    rut VARCHAR(12) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(120) NOT NULL UNIQUE,
    tipo VARCHAR(20) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'HABILITADO',
    CONSTRAINT chk_lectores_tipo
        CHECK (tipo IN ('ESTUDIANTE', 'DOCENTE', 'FUNCIONARIO')),
    CONSTRAINT chk_lectores_estado
        CHECK (estado IN ('HABILITADO', 'BLOQUEADO'))
);

CREATE TABLE categorias (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE autores (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL
);

CREATE TABLE libros (
    id BIGSERIAL PRIMARY KEY,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    titulo VARCHAR(200) NOT NULL,
    editorial VARCHAR(100) NOT NULL,
    anio_publicacion INTEGER NOT NULL,
    categoria_id BIGINT NOT NULL,
    CONSTRAINT fk_libros_categoria
        FOREIGN KEY (categoria_id)
            REFERENCES categorias(id)
            ON UPDATE CASCADE
            ON DELETE RESTRICT
);

CREATE TABLE libros_autores (
    libro_id BIGINT NOT NULL,
    autor_id BIGINT NOT NULL,
    PRIMARY KEY (libro_id, autor_id),
    CONSTRAINT fk_libros_autores_libro
        FOREIGN KEY (libro_id)
            REFERENCES libros(id)
            ON UPDATE CASCADE
            ON DELETE CASCADE,
    CONSTRAINT fk_libros_autores_autor
        FOREIGN KEY (autor_id)
            REFERENCES autores(id)
            ON UPDATE CASCADE
            ON DELETE RESTRICT
);

CREATE TABLE ejemplares (
    id BIGSERIAL PRIMARY KEY,
    codigo_inventario VARCHAR(50) NOT NULL UNIQUE,
    ubicacion VARCHAR(100) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE',
    libro_id BIGINT NOT NULL,
    CONSTRAINT fk_ejemplares_libro
        FOREIGN KEY (libro_id)
            REFERENCES libros(id)
            ON UPDATE CASCADE
            ON DELETE CASCADE,
    CONSTRAINT chk_ejemplares_estado
        CHECK (estado IN ('DISPONIBLE', 'PRESTADO', 'EN_REPARACION', 'DADO_DE_BAJA'))
);

CREATE TABLE prestamos (
    id BIGSERIAL PRIMARY KEY,
    fecha_prestamo DATE NOT NULL,
    fecha_limite DATE NOT NULL,
    fecha_devolucion DATE,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    lector_id BIGINT NOT NULL,
    ejemplar_id BIGINT NOT NULL,
    recibido_por_id BIGINT,
    CONSTRAINT fk_prestamos_lector
        FOREIGN KEY (lector_id)
            REFERENCES lectores(id)
            ON UPDATE CASCADE
            ON DELETE RESTRICT,
    CONSTRAINT fk_prestamos_ejemplar
        FOREIGN KEY (ejemplar_id)
            REFERENCES ejemplares(id)
            ON UPDATE CASCADE
            ON DELETE RESTRICT,
    CONSTRAINT fk_prestamos_recibido_por
        FOREIGN KEY (recibido_por_id)
            REFERENCES bibliotecarios(id)
            ON UPDATE CASCADE
            ON DELETE RESTRICT,
    CONSTRAINT chk_prestamos_estado
        CHECK (estado IN ('ACTIVO', 'DEVUELTO'))
);

CREATE TABLE multas (
    id BIGSERIAL PRIMARY KEY,
    dias_atraso INTEGER NOT NULL DEFAULT 0,
    monto BIGINT NOT NULL DEFAULT 0,
    fecha_generacion DATE NOT NULL,
    fecha_pago DATE,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    prestamo_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_multas_prestamo
        FOREIGN KEY (prestamo_id)
            REFERENCES prestamos(id)
            ON UPDATE CASCADE
            ON DELETE CASCADE,
    CONSTRAINT chk_multas_estado
        CHECK (estado IN ('PENDIENTE', 'PAGADA'))
);

CREATE INDEX idx_lectores_rut ON lectores(rut);
CREATE INDEX idx_libros_isbn ON libros(isbn);
CREATE INDEX idx_ejemplares_codigo ON ejemplares(codigo_inventario);
CREATE INDEX idx_prestamos_lector_id ON prestamos(lector_id);
CREATE INDEX idx_prestamos_ejemplar_id ON prestamos(ejemplar_id);
CREATE INDEX idx_prestamos_estado ON prestamos(estado);
CREATE INDEX idx_multas_estado ON multas(estado);
