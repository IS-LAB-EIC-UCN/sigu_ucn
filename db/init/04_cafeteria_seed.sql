-- Roles y usuarios adicionales para el Módulo Cafetería (Grupo 7)
INSERT INTO roles (nombre) VALUES
    ('CAJERO'),
    ('BARISTA')
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO usuarios (nombre, correo, password, activo, rol_id) VALUES
    ('Cajero Cafetería', 'cajero@sigu.cl', 'cajero123', TRUE, (SELECT id FROM roles WHERE nombre = 'CAJERO')),
    ('Barista Cafetería', 'barista@sigu.cl', 'barista123', TRUE, (SELECT id FROM roles WHERE nombre = 'BARISTA'))
ON CONFLICT (correo) DO NOTHING;

-- Datos iniciales Módulo Cafetería (Grupo 7)
INSERT INTO categoria (nombre, descripcion) VALUES
    ('Bebidas Calientes', 'Café, té, infusiones y chocolate caliente'),
    ('Bebidas Frías', 'Jugos naturales, bebidas y café helado'),
    ('Pastelería', 'Masas dulces, empolvados, panqueques y tortas'),
    ('Sándwiches y Salados', 'Sándwiches, empanadas y snacks salados');

INSERT INTO producto (nombre, precio, stock, categoria_id) VALUES
    ('Café Americano', 1500.00, 50, 1),
    ('Cappuccino', 1900.00, 35, 1),
    ('Jugo Natural Frambuesa', 2200.00, 25, 2),
    ('Trozos de Empolvados', 1200.00, 20, 3),
    ('Panqueques Gorditos', 2500.00, 15, 3),
    ('Sándwich Barros Jarpa', 2800.00, 20, 4);

