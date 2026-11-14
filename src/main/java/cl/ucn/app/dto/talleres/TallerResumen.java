package cl.ucn.app.dto.talleres;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Datos de un taller listos para mostrar en una vista, incluyendo cuántos inscritos
 * y cuántos en lista de espera tiene. Se construye directamente desde la consulta JPQL.
 */
public record TallerResumen(
        Long id,
        String nombre,
        String categoria,
        LocalDate fecha,
        LocalTime horaInicio,
        LocalTime horaFin,
        String lugar,
        Integer cupoMaximo,
        Long inscritos,
        Long enEspera
) {
    public long cuposDisponibles() {
        return Math.max(0, cupoMaximo - inscritos);
    }

    public boolean estaLleno() {
        return cuposDisponibles() == 0;
    }
}
