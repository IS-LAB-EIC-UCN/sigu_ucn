package cl.ucn.app.service.talleres;

import cl.ucn.app.model.talleres.EstadoInscripcion;
import cl.ucn.app.model.talleres.Taller;

/**
 * Reglas de negocio 1 y 3: el cupo no puede superarse; si no hay cupo, el participante
 * pasa a lista de espera. Decide en qué estado queda una inscripción nueva.
 */
public class PoliticaCupo {

    public EstadoInscripcion estadoParaNuevaInscripcion(Taller taller, long inscritosActuales) {
        return inscritosActuales < taller.getCupoMaximo()
                ? EstadoInscripcion.INSCRITO
                : EstadoInscripcion.EN_ESPERA;
    }
}
