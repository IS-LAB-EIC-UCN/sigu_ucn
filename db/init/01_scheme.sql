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
                                               CHECK (subtotal >= 0)
);

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