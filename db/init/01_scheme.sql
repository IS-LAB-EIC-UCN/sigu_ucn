CREATE TABLE categorias_cafeteria (
                                      id BIGSERIAL PRIMARY KEY,
                                      nombre VARCHAR(100) NOT NULL UNIQUE);

CREATE TABLE productos_cafeteria (
                                     id BIGSERIAL PRIMARY KEY,
                                     nombre VARCHAR(100) NOT NULL,
                                     precio DECIMAL(10,2) NOT NULL,
                                     stock INTEGER NOT NULL,
                                     categoria_id BIGINT NOT NULL,
                                     CONSTRAINT fk_productos_cafeteria_categorias
                                         FOREIGN KEY (categoria_id)
                                             REFERENCES categorias_cafeteria(id)
                                             ON UPDATE CASCADE
                                             ON DELETE RESTRICT,
                                     CONSTRAINT chk_productos_cafeteria_precio
                                         CHECK (precio > 0),
                                     CONSTRAINT chk_productos_cafeteria_stock
                                         CHECK (stock >= 0));

CREATE TABLE pedidos_cafeteria (
                                   id BIGSERIAL PRIMARY KEY,
                                   fecha_pedido TIMESTAMP NOT NULL,
                                   estado VARCHAR(30) NOT NULL,
                                   total DECIMAL(10,2) NOT NULL,
                                   CONSTRAINT chk_pedidos_cafeteria_estado
                                       CHECK (estado IN ('PENDIENTE', 'EN_PREPARACION', 'LISTO', 'ENTREGADO', 'ANULADO')),
                                   CONSTRAINT chk_pedidos_cafeteria_total
                                       CHECK (total >= 0));

CREATE TABLE detalle_pedidos_cafeteria (
                                           id BIGSERIAL PRIMARY KEY,
                                           pedido_id BIGINT NOT NULL,
                                           producto_id BIGINT NOT NULL,
                                           cantidad INTEGER NOT NULL,
                                           subtotal DECIMAL(10,2) NOT NULL,
                                           CONSTRAINT fk_detalle_pedidos_cafeteria_pedidos
                                               FOREIGN KEY (pedido_id)
                                                   REFERENCES pedidos_cafeteria(id)
                                                   ON UPDATE CASCADE
                                                   ON DELETE RESTRICT,
                                           CONSTRAINT fk_detalle_pedidos_cafeteria_productos
                                               FOREIGN KEY (producto_id)
                                                   REFERENCES productos_cafeteria(id)
                                                   ON UPDATE CASCADE
                                                   ON DELETE RESTRICT,
                                           CONSTRAINT chk_detalle_pedidos_cafeteria_cantidad
                                               CHECK (cantidad > 0),
                                           CONSTRAINT chk_detalle_pedidos_cafeteria_subtotal
                                               CHECK (subtotal >= 0));

CREATE TABLE ventas_cafeteria (
                                  id BIGSERIAL PRIMARY KEY,
                                  pedido_id BIGINT NOT NULL UNIQUE,
                                  fecha_venta TIMESTAMP NOT NULL,
                                  total DECIMAL(10,2) NOT NULL,
                                  CONSTRAINT fk_ventas_cafeteria_pedidos
                                      FOREIGN KEY (pedido_id)
                                          REFERENCES pedidos_cafeteria(id)
                                          ON UPDATE CASCADE
                                          ON DELETE RESTRICT,
                                  CONSTRAINT chk_ventas_cafeteria_total
                                      CHECK (total >= 0));
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
