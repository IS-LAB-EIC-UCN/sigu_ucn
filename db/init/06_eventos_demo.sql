-- Ocho eventos de ejemplo del docente. No elimina datos existentes.
-- Compatible con la inicialización anterior a las migraciones 07 a 10.
BEGIN;
LOCK TABLE eventos IN SHARE ROW EXCLUSIVE MODE;
DO $$
DECLARE organizador BIGINT; sala BIGINT; capacidad_sala INTEGER; ponente BIGINT; dato RECORD;
BEGIN
 SELECT u.id INTO organizador FROM usuarios u JOIN roles r ON r.id=u.rol_id
 WHERE u.correo='docente@sigu.cl' AND u.activo AND r.nombre='DOCENTE';
 IF organizador IS NULL THEN RAISE EXCEPTION 'Se requiere la cuenta activa docente@sigu.cl con rol DOCENTE'; END IF;
 FOR dato IN SELECT * FROM (VALUES
  ('Taller de desarrollo web con Java', 'Construye una aplicación web sencilla con formularios, validaciones y persistencia. Actividad práctica orientada a estudiantes.', 'Tecnología', 4, '10:00'::time, '12:00'::time, 'Laboratorio L1', 25, 'Equipo de Seguridad Digital'),
  ('Jornada de proyectos e innovación universitaria', 'Comparte propuestas para resolver problemas de la comunidad universitaria y conoce experiencias de emprendimiento estudiantil.', 'Innovación', 6, '11:00'::time, '13:00'::time, 'Auditorio Central', 100, 'Equipo de Innovación y Emprendimiento'),
  ('Taller de metodología de investigación', 'Aprende a formular objetivos, seleccionar fuentes confiables y organizar un proyecto de investigación académica.', 'Investigación', 8, '15:00'::time, '17:00'::time, 'Sala A101', 35, 'Equipo de Apoyo a la Investigación'),
  ('Introducción al análisis y visualización de datos', 'Explora datos de ejemplo, identifica tendencias y prepara gráficos para comunicar resultados de manera clara.', 'Ciencia de datos', 11, '09:00'::time, '11:00'::time, 'Laboratorio L1', 25, 'Equipo de Ciencia de Datos'),
  ('Charla de seguridad digital y protección de cuentas', 'Reconoce intentos de fraude, configura contraseñas seguras y aprende a proteger tus cuentas y archivos académicos.', 'Tecnología', 12, '10:00'::time, '11:30'::time, 'Auditorio Central', 80, 'Equipo de Seguridad Digital'),
  ('Taller de diseño de soluciones para el campus', 'Trabaja en equipo para identificar necesidades del campus y elaborar una propuesta de solución con objetivos y alcance claros.', 'Innovación', 13, '15:00'::time, '17:00'::time, 'Sala A101', 35, 'Equipo de Innovación y Emprendimiento'),
  ('Seminario de presentación de investigaciones', 'Conoce recomendaciones para preparar una exposición académica, organizar resultados y responder preguntas del público.', 'Investigación', 14, '11:00'::time, '13:00'::time, 'Auditorio Central', 100, 'Equipo de Apoyo a la Investigación'),
  ('Laboratorio de limpieza y preparación de datos', 'Practica la detección de datos faltantes, la corrección de formatos y la organización de conjuntos de datos para su análisis.', 'Ciencia de datos', 15, '09:00'::time, '11:00'::time, 'Laboratorio L1', 25, 'Equipo de Ciencia de Datos')
 ) AS datos(titulo,descripcion,categoria,dias,inicio,termino,ubicacion,cupos,expositor)
 LOOP
  SELECT id,capacidad INTO sala,capacidad_sala FROM espacios WHERE nombre=dato.ubicacion AND disponible ORDER BY id LIMIT 1;
  IF sala IS NULL OR capacidad_sala<dato.cupos THEN RAISE EXCEPTION 'Sala no disponible o sin capacidad: %',dato.ubicacion; END IF;
  IF EXISTS (SELECT 1 FROM eventos WHERE titulo=dato.titulo AND organizador_id=organizador) THEN CONTINUE; END IF;
  IF EXISTS (SELECT 1 FROM eventos WHERE espacio_id=sala AND fecha=CURRENT_DATE+dato.dias AND estado<>'CANCELADO' AND hora_inicio<dato.termino AND hora_fin>dato.inicio) THEN
   RAISE EXCEPTION 'Existe un cruce de horario en %',dato.ubicacion;
  END IF;
  SELECT id INTO ponente FROM expositores WHERE nombre=dato.expositor ORDER BY id LIMIT 1;
  IF ponente IS NULL THEN
   INSERT INTO expositores(nombre,correo,especialidad) VALUES(dato.expositor,'',dato.categoria) RETURNING id INTO ponente;
  END IF;
  INSERT INTO eventos(titulo,descripcion,tematica,fecha,hora_inicio,hora_fin,lugar,cupo_maximo,estado,organizador_id,expositor_id,espacio_id)
  VALUES(dato.titulo,dato.descripcion,dato.categoria,CURRENT_DATE+dato.dias,dato.inicio,dato.termino,dato.ubicacion,dato.cupos,'PUBLICADO',organizador,ponente,sala);
 END LOOP;
END $$;
COMMIT;
