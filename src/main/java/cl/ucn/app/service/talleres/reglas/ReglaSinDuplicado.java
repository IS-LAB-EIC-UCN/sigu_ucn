package cl.ucn.app.service.talleres.reglas;

import cl.ucn.app.exception.ReglaNegocioException;
import cl.ucn.app.model.talleres.EstadoInscripcion;
import cl.ucn.app.model.talleres.Inscripcion;

import java.util.Objects;

/** Regla de negocio 2: un participante no puede inscribirse dos veces al mismo taller. */
public class ReglaSinDuplicado implements ReglaInscripcion {

    @Override
    public void validar(SolicitudInscripcion solicitud) {
        Long tallerObjetivoId = solicitud.taller().getId();
        for (Inscripcion existente : solicitud.inscripcionesVigentes()) {
            if (Objects.equals(existente.getTaller().getId(), tallerObjetivoId)) {
                if (existente.getEstado() == EstadoInscripcion.EN_ESPERA) {
                    throw new ReglaNegocioException("Ya estás en la lista de espera de este taller.");
                }
                throw new ReglaNegocioException("Ya estás inscrito en este taller.");
            }
        }
    }
}
