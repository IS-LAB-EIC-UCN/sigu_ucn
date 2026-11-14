package cl.ucn.app.dto.talleres;

import java.time.LocalDate;
import java.time.LocalTime;

/** Datos de entrada para registrar un taller (vienen del formulario, ya convertidos de texto). */
public record NuevoTaller(
        String nombre,
        String descripcion,
        String categoria,
        LocalDate fecha,
        LocalTime horaInicio,
        LocalTime horaFin,
        String lugar,
        Integer cupoMaximo
) {
}
