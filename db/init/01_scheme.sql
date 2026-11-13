-- ==========================================
-- MÓDULO CAFETERÍA (GRUPO 7)
-- ==========================================
CREATE TABLE Categoria (
                           id SERIAL PRIMARY KEY,
                           nombre VARCHAR(100) NOT NULL,
                           descripcion TEXT
);

CREATE TABLE Producto (
                          id SERIAL PRIMARY KEY,
                          nombre VARCHAR(100) NOT NULL,
                          precio NUMERIC(10, 2) NOT NULL CHECK (precio >= 0),
                          stock INTEGER NOT NULL CHECK (stock >= 0),
                          categoria_id INTEGER REFERENCES Categoria(id)
);

CREATE TABLE Pedido (
                        id SERIAL PRIMARY KEY,
                        fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        total NUMERIC(10, 2) NOT NULL DEFAULT 0,
                        estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
                            CHECK (estado IN ('PENDIENTE', 'EN_PREPARACION', 'LISTO', 'ENTREGADO', 'PAGADO', 'ANULADO'))
);

CREATE TABLE DetallePedido (
                               pedido_id INTEGER REFERENCES Pedido(id),
                               producto_id INTEGER REFERENCES Producto(id),
                               cantidad INTEGER NOT NULL CHECK (cantidad > 0),
                               precio_registrado NUMERIC(10, 2) NOT NULL,
                               PRIMARY KEY (pedido_id, producto_id)
);