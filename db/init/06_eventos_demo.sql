-- Datos iniciales del modulo 6. No modifica ni duplica eventos ya existentes.

-- Se aplica despues de 05_eventos_catalogos.sql.

BEGIN;

LOCK TABLE eventos IN SHARE ROW EXCLUSIVE MODE;

DO $$
DECLARE organizador BIGINT; sala BIGINT; ponente BIGINT;
BEGIN
 SELECT id INTO organizador FROM usuarios WHERE correo = 'admin@sigu.cl';
 SELECT id INTO sala FROM espacios WHERE nombre = 'Auditorio Central' AND disponible = TRUE ORDER BY id LIMIT 1;
 IF organizador IS NULL OR sala IS NULL THEN
   RAISE EXCEPTION 'No se encontro el organizador o la sala para los eventos iniciales';
 END IF;
 SELECT id INTO ponente FROM expositores WHERE nombre = 'Equipo de Seguridad Digital' ORDER BY id LIMIT 1;
 IF ponente IS NULL THEN
   INSERT INTO expositores(nombre, correo, especialidad) VALUES ('Equipo de Seguridad Digital', '', 'Tecnología') RETURNING id INTO ponente;
 END IF;
 IF NOT EXISTS (SELECT 1 FROM eventos WHERE titulo = 'Charla de ciberseguridad para la vida universitaria' AND fecha = '2026-11-16'::date AND organizador_id = organizador) THEN
   IF EXISTS (SELECT 1 FROM eventos WHERE estado <> 'CANCELADO' AND fecha = '2026-11-16'::date
      AND lower(trim(lugar)) = lower(trim('Auditorio Central')) AND hora_inicio < '11:30:00'::time AND hora_fin > '10:00:00'::time) THEN
     RAISE EXCEPTION 'Existe un cruce de horario al cargar los eventos iniciales';
   END IF;
   INSERT INTO eventos(titulo, descripcion, tematica, fecha, hora_inicio, hora_fin, lugar, cupo_maximo, estado, organizador_id, expositor_id, espacio_id)
   VALUES ('Charla de ciberseguridad para la vida universitaria', 'Buenas prácticas para proteger cuentas, reconocer fraudes y navegar de forma segura en el entorno universitario.', 'Tecnología', '2026-11-16'::date, '10:00:00'::time, '11:30:00'::time, 'Auditorio Central', 80, 'PUBLICADO', organizador, ponente, sala);
 END IF;
END $$;

DO $$
DECLARE organizador BIGINT; sala BIGINT; ponente BIGINT;
BEGIN
 SELECT id INTO organizador FROM usuarios WHERE correo = 'admin@sigu.cl';
 SELECT id INTO sala FROM espacios WHERE nombre = 'Sala A101' AND disponible = TRUE ORDER BY id LIMIT 1;
 IF organizador IS NULL OR sala IS NULL THEN
   RAISE EXCEPTION 'No se encontro el organizador o la sala para los eventos iniciales';
 END IF;
 SELECT id INTO ponente FROM expositores WHERE nombre = 'Equipo de Apoyo a la Investigación' ORDER BY id LIMIT 1;
 IF ponente IS NULL THEN
   INSERT INTO expositores(nombre, correo, especialidad) VALUES ('Equipo de Apoyo a la Investigación', '', 'Investigación') RETURNING id INTO ponente;
 END IF;
 IF NOT EXISTS (SELECT 1 FROM eventos WHERE titulo = 'Taller de investigación y escritura académica' AND fecha = '2026-11-18'::date AND organizador_id = organizador) THEN
   IF EXISTS (SELECT 1 FROM eventos WHERE estado <> 'CANCELADO' AND fecha = '2026-11-18'::date
      AND lower(trim(lugar)) = lower(trim('Sala A101')) AND hora_inicio < '17:00:00'::time AND hora_fin > '15:00:00'::time) THEN
     RAISE EXCEPTION 'Existe un cruce de horario al cargar los eventos iniciales';
   END IF;
   INSERT INTO eventos(titulo, descripcion, tematica, fecha, hora_inicio, hora_fin, lugar, cupo_maximo, estado, organizador_id, expositor_id, espacio_id)
   VALUES ('Taller de investigación y escritura académica', 'Aprende a plantear una pregunta de investigación, organizar fuentes y estructurar un informe académico.', 'Investigación', '2026-11-18'::date, '15:00:00'::time, '17:00:00'::time, 'Sala A101', 35, 'PUBLICADO', organizador, ponente, sala);
 END IF;
END $$;

DO $$
DECLARE organizador BIGINT; sala BIGINT; ponente BIGINT;
BEGIN
 SELECT id INTO organizador FROM usuarios WHERE correo = 'admin@sigu.cl';
 SELECT id INTO sala FROM espacios WHERE nombre = 'Auditorio Central' AND disponible = TRUE ORDER BY id LIMIT 1;
 IF organizador IS NULL OR sala IS NULL THEN
   RAISE EXCEPTION 'No se encontro el organizador o la sala para los eventos iniciales';
 END IF;
 SELECT id INTO ponente FROM expositores WHERE nombre = 'Equipo de Innovación y Emprendimiento' ORDER BY id LIMIT 1;
 IF ponente IS NULL THEN
   INSERT INTO expositores(nombre, correo, especialidad) VALUES ('Equipo de Innovación y Emprendimiento', '', 'Innovación') RETURNING id INTO ponente;
 END IF;
 IF NOT EXISTS (SELECT 1 FROM eventos WHERE titulo = 'Encuentro de innovación y emprendimiento estudiantil' AND fecha = '2026-11-23'::date AND organizador_id = organizador) THEN
   IF EXISTS (SELECT 1 FROM eventos WHERE estado <> 'CANCELADO' AND fecha = '2026-11-23'::date
      AND lower(trim(lugar)) = lower(trim('Auditorio Central')) AND hora_inicio < '13:00:00'::time AND hora_fin > '11:00:00'::time) THEN
     RAISE EXCEPTION 'Existe un cruce de horario al cargar los eventos iniciales';
   END IF;
   INSERT INTO eventos(titulo, descripcion, tematica, fecha, hora_inicio, hora_fin, lugar, cupo_maximo, estado, organizador_id, expositor_id, espacio_id)
   VALUES ('Encuentro de innovación y emprendimiento estudiantil', 'Espacio para compartir ideas, conocer herramientas para desarrollar proyectos y conectar con otros estudiantes.', 'Innovación', '2026-11-23'::date, '11:00:00'::time, '13:00:00'::time, 'Auditorio Central', 100, 'PUBLICADO', organizador, ponente, sala);
 END IF;
END $$;

DO $$
DECLARE organizador BIGINT; sala BIGINT; ponente BIGINT;
BEGIN
 SELECT id INTO organizador FROM usuarios WHERE correo = 'admin@sigu.cl';
 SELECT id INTO sala FROM espacios WHERE nombre = 'Laboratorio L1' AND disponible = TRUE ORDER BY id LIMIT 1;
 IF organizador IS NULL OR sala IS NULL THEN
   RAISE EXCEPTION 'No se encontro el organizador o la sala para los eventos iniciales';
 END IF;
 SELECT id INTO ponente FROM expositores WHERE nombre = 'Equipo de Ciencia de Datos' ORDER BY id LIMIT 1;
 IF ponente IS NULL THEN
   INSERT INTO expositores(nombre, correo, especialidad) VALUES ('Equipo de Ciencia de Datos', '', 'Ciencia de datos') RETURNING id INTO ponente;
 END IF;
 IF NOT EXISTS (SELECT 1 FROM eventos WHERE titulo = 'Taller práctico de análisis de datos' AND fecha = '2026-11-26'::date AND organizador_id = organizador) THEN
   IF EXISTS (SELECT 1 FROM eventos WHERE estado <> 'CANCELADO' AND fecha = '2026-11-26'::date
      AND lower(trim(lugar)) = lower(trim('Laboratorio L1')) AND hora_inicio < '11:00:00'::time AND hora_fin > '09:00:00'::time) THEN
     RAISE EXCEPTION 'Existe un cruce de horario al cargar los eventos iniciales';
   END IF;
   INSERT INTO eventos(titulo, descripcion, tematica, fecha, hora_inicio, hora_fin, lugar, cupo_maximo, estado, organizador_id, expositor_id, espacio_id)
   VALUES ('Taller práctico de análisis de datos', 'Introducción práctica a la limpieza, exploración y visualización de datos mediante ejercicios guiados.', 'Ciencia de datos', '2026-11-26'::date, '09:00:00'::time, '11:00:00'::time, 'Laboratorio L1', 25, 'PUBLICADO', organizador, ponente, sala);
 END IF;
END $$;

COMMIT;
