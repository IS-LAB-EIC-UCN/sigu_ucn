-- HU02/HU03: Rol de tecnico de soporte y usuario de prueba.

INSERT INTO roles (nombre)
VALUES ('TECNICO')
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO usuarios (nombre, correo, password, activo, rol_id)
SELECT 'Tecnico Demo', 'tecnico@sigu.cl', 'tecnico123', TRUE, r.id
FROM roles r
WHERE r.nombre = 'TECNICO'
ON CONFLICT (correo) DO NOTHING;
