INSERT INTO roles (nombre)
VALUES ('TUTOR')
    ON CONFLICT (nombre) DO NOTHING;

INSERT INTO usuarios (nombre, correo, password, activo, rol_id)
SELECT 'Tutor Demo', 'tutor@sigu.cl', 'tutor123', TRUE, r.id
FROM roles r
WHERE r.nombre = 'TUTOR'
  AND NOT EXISTS (
    SELECT 1
    FROM usuarios u
    WHERE u.correo = 'tutor@sigu.cl'
);