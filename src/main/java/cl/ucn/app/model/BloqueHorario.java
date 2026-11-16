package cl.ucn.app.model;

import java.time.LocalTime;

/**
 * Representa un bloque horario de una sala en una fecha determinada.
 * Si {@code estado} es {@code null}, el bloque corresponde a un horario libre.
 */
public record BloqueHorario(LocalTime inicio, LocalTime fin, EstadoReserva estado) {
}
