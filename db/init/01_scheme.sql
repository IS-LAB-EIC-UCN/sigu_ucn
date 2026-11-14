CREATE TABLE roles (
                       id BIGSERIAL PRIMARY KEY,
                       nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE usuarios (
                          id BIGSERIAL PRIMARY KEY,
                          nombre VARCHAR(100) NOT NULL,
                          correo VARCHAR(120) NOT NULL UNIQUE,
                          password VARCHAR(255) NOT NULL,
                          activo BOOLEAN NOT NULL DEFAULT TRUE,
                          rol_id BIGINT NOT NULL,
                          CONSTRAINT fk_usuarios_roles
                              FOREIGN KEY (rol_id)
                                  REFERENCES roles(id)
                                  ON UPDATE CASCADE
                                  ON DELETE RESTRICT
);

CREATE TABLE espacios (
                          id BIGSERIAL PRIMARY KEY,
                          nombre VARCHAR(100) NOT NULL,
                          tipo VARCHAR(50) NOT NULL,
                          capacidad INTEGER NOT NULL,
                          disponible BOOLEAN NOT NULL DEFAULT TRUE,
                          CONSTRAINT chk_espacios_capacidad
                              CHECK (capacidad > 0)
);

CREATE TABLE reservas (
                          id BIGSERIAL PRIMARY KEY,
                          fecha_reserva DATE NOT NULL,
                          hora_inicio TIME NOT NULL,
                          hora_fin TIME NOT NULL,
                          estado VARCHAR(30) NOT NULL,
                          usuario_id BIGINT NOT NULL,
                          espacio_id BIGINT NOT NULL,
                          CONSTRAINT fk_reservas_usuarios
                              FOREIGN KEY (usuario_id)
                                  REFERENCES usuarios(id)
                                  ON UPDATE CASCADE
                                  ON DELETE RESTRICT,
                          CONSTRAINT fk_reservas_espacios
                              FOREIGN KEY (espacio_id)
                                  REFERENCES espacios(id)
                                  ON UPDATE CASCADE
                                  ON DELETE RESTRICT,
                          CONSTRAINT chk_reservas_horas
                              CHECK (hora_inicio < hora_fin),
                          CONSTRAINT chk_reservas_estado
                              CHECK (estado IN ('PENDIENTE', 'APROBADA', 'RECHAZADA', 'CANCELADA'))
);

CREATE INDEX idx_usuarios_rol_id ON usuarios(rol_id);
CREATE INDEX idx_reservas_usuario_id ON reservas(usuario_id);
CREATE INDEX idx_reservas_espacio_id ON reservas(espacio_id);
CREATE INDEX idx_reservas_fecha_reserva ON reservas(fecha_reserva);

--GRUPO 5

CREATE TABLE inv_categorias (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL UNIQUE
);

CREATE TABLE inv_proveedores (
    id BIGSERIAL PRIMARY KEY,
    identificador VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(120) NOT NULL,
    telefono VARCHAR(30),
    correo VARCHAR(120),
    direccion VARCHAR(200)
);

CREATE TABLE inv_productos (
    id BIGSERIAL PRIMARY KEY,
    tipo VARCHAR(10) NOT NULL,
    codigo VARCHAR(30) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    categoria_id BIGINT NOT NULL,
    proveedor_id BIGINT,
    estado VARCHAR(20),
    stock_actual INTEGER,
    stock_minimo INTEGER,
    stock_inicializado BOOLEAN,
    CONSTRAINT fk_inv_productos_categorias
        FOREIGN KEY (categoria_id)
            REFERENCES inv_categorias(id)
            ON UPDATE CASCADE
            ON DELETE RESTRICT,
    CONSTRAINT fk_inv_productos_proveedores
        FOREIGN KEY (proveedor_id)
        REFERENCES inv_proveedores(id)
        ON UPDATE CASCADE
        ON DELETE SET NULL,
    CONSTRAINT chk_inv_productos_tipo
        CHECK (tipo IN ('EQUIPO', 'INSUMO')),
    -- Regla de negocio_ el stock no puede ser negativo
    CONSTRAINT chk_inv_productos_stock_actual
        CHECK (stock_actual IS NULL OR stock_actual >= 0),
    CONSTRAINT chk_inv_productos_stock_minimo
        CHECK (stock_minimo IS NULL OR stock_minimo >= 0),
    --COHERENCIAA entre el tipo yu sus columnas propias
    CONSTRAINT chk_inv_productos_coherencia
        CHECK (
            (tipo = 'EQUIPO' AND estado IN ('DISPONIBLE', 'PRESTADO'))
            OR
            (tipo = 'INSUMO' AND estado IS NULL
                AND stock_actual IS NOT NULL
                AND stock_minimo IS NOT NULL
                AND stock_inicializado IS NOT NULL)
        )
);

CREATE TABLE inv_prestamos (
    id BIGSERIAL PRIMARY KEY,
    equipo_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    fecha_prestamo TIMESTAMP NOT NULL,
    fecha_devolucion TIMESTAMP,
    estado VARCHAR(20) NOT NULL,
    CONSTRAINT fk_inv_prestamos_productos
        FOREIGN KEY (equipo_id)
            REFERENCES inv_productos(id)
            ON UPDATE CASCADE
            ON DELETE RESTRICT,
    CONSTRAINT fk_inv_prestamos_usuarios
        FOREIGN KEY (usuario_id)
            REFERENCES usuarios(id)
            ON UPDATE CASCADE
            ON DELETE RESTRICT,
    CONSTRAINT chk_inv_prestamos_estado
        CHECK (estado in ('ACTIVO', 'FINALIZADO')),
    -- Un prestamo finalizado debe tener fecha de devolución, y uno activo
    CONSTRAINT chk_inv_prestamos_devolucion
        CHECK (
            (estado = 'ACTIVO' AND fecha_devolucion IS NULL)
            OR
            (estado = 'FINALIZADO' AND fecha_devolucion IS NOT NULL
                AND fecha_devolucion >= fecha_prestamo)
        )
);

--Regla de negocio: un equipo prestado no puede prestarse nuevamente
--Solo puede existir un préstamos activo por equipo

CREATE UNIQUE INDEX uq_inv_prestamos_equipo_activo
    ON inv_prestamos(equipo_id)
    WHERE estado = 'ACTIVO';

-- Regla de negocio: toda salida y entrada debe quedar registrada
CREATE TABLE inv_movimientos (
    id BIGSERIAL PRIMARY KEY,
    insumo_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    tipo VARCHAR(10) NOT NULL,
    cantidad INTEGER NOT NULL,
    fecha TIMESTAMP NOT NULL,
    CONSTRAINT fk_inv_movimientos_productos
        FOREIGN KEY (insumo_id)
            REFERENCES inv_productos(id)
            ON UPDATE CASCADE
            ON DELETE RESTRICT,
    CONSTRAINT fk_inv_movimientos_usuarios
        FOREIGN KEY (usuario_id)
            REFERENCES usuarios(id)
            ON UPDATE CASCADE
            ON DELETE RESTRICT,
    CONSTRAINT chk_inv_movimientos_tipo
        CHECK (tipo IN ('ENTRADA', 'SALIDA')),
    CONSTRAINT chk_inv_movimientos_cantidad
        CHECK (cantidad > 0)
);

CREATE INDEX idx_inv_productos_categoria_id ON inv_productos(categoria_id);
CREATE INDEX idx_inv_productos_tipo ON inv_productos(tipo);
CREATE INDEX idx_inv_prestamos_usuario_id ON inv_prestamos(usuario_id);
CREATE INDEX idx_inv_movimientos_insumo_id ON inv_movimientos(insumo_id);
CREATE INDEX idx_inv_movimientos_fecha ON inv_movimientos(fecha);