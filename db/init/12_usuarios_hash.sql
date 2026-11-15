-- =====================================================================
-- Grupo 1 - RNF-01: ninguna contraseña se almacena en texto plano.
-- Reemplaza las contraseñas de los usuarios de 02_seed.sql por su hash
-- PBKDF2-HMAC-SHA256 (formato pbkdf2_sha256$iteraciones$sal$hash),
-- generado con cl.ucn.app.security.Pbkdf2PasswordHasher.
--
-- Las credenciales de acceso NO cambian:
--   admin@sigu.cl      / admin123
--   docente@sigu.cl    / docente123
--   estudiante@sigu.cl / estudiante123
-- =====================================================================

UPDATE usuarios SET password = 'pbkdf2_sha256$310000$TNdAkVRPoOlPIqVQKaQ9rA==$/39UE7zeIiQH0lnvUV1bYHBMNMOtDcyXgWn3C9CLMU4=' WHERE correo = 'admin@sigu.cl';
UPDATE usuarios SET password = 'pbkdf2_sha256$310000$XeIK3noxiws7u59D6iVfpA==$0/uiTlGDIrMRlWrymL/YAk2Yaps2APMmVLBZaBQ56YE=' WHERE correo = 'docente@sigu.cl';
UPDATE usuarios SET password = 'pbkdf2_sha256$310000$mkVOgSylxRPIZSM3/waxgA==$VFRu5aSWtz6sUBROdfqgcNXk8QU/yBNprYBNZNji6Y8=' WHERE correo = 'estudiante@sigu.cl';
