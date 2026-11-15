-- Grupo 8 (Tutorías) - Restricciones adicionales
-- Impide dos tutorías PUBLICADAS del mismo tutor en la misma franja exacta.
-- (El cruce parcial de horarios se valida en TutoriaService dentro de una transacción
--  con el tutor bloqueado; este índice es la última barrera ante concurrencia.)
CREATE UNIQUE INDEX ux_tut_tutorias_franja_publicada
    ON tut_tutorias (tutor_id, inicio, fin)
    WHERE estado = 'PUBLICADA';
