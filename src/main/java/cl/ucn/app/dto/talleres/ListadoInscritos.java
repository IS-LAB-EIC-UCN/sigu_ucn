package cl.ucn.app.dto.talleres;

import cl.ucn.app.model.talleres.Inscripcion;
import cl.ucn.app.model.talleres.Taller;

import java.util.List;

/** Inscritos y lista de espera (en orden de llegada) de un taller. */
public record ListadoInscritos(Taller taller, List<Inscripcion> inscritos, List<Inscripcion> listaEspera) {
}
