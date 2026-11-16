package cl.ucn.app.service;

import cl.ucn.app.model.BloqueHorario;
import cl.ucn.app.model.Espacio;

import java.time.LocalDate;
import java.util.List;

/**
 * Resultado de la consulta de disponibilidad de una sala para una fecha.
 */
public record Disponibilidad(
        Espacio espacio,
        LocalDate fecha,
        List<BloqueHorario> ocupados,
        List<BloqueHorario> libres
) {
}
