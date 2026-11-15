package cl.ucn.app.service.talleres.reglas;

import cl.ucn.app.exception.ReglaNegocioException;
import cl.ucn.app.model.talleres.Inscripcion;
import cl.ucn.app.model.talleres.Taller;
import cl.ucn.app.util.Formato;

import java.util.Objects;

/**
 * Regla de negocio 4: un participante no puede inscribirse en dos talleres con horario solapado.
 * También cuenta la lista de espera: si el participante sube desde la espera, no debe quedar con
 * dos talleres al mismo tiempo.
 */
public class ReglaSinSolapamiento implements ReglaInscripcion {

    @Override
    public void validar(SolicitudInscripcion solicitud) {
        Taller nuevoTaller = solicitud.taller();
        for (Inscripcion existente : solicitud.inscripcionesVigentes()) {
            Taller tallerExistente = existente.getTaller();
            if (Objects.equals(tallerExistente.getId(), nuevoTaller.getId())) {
                continue;
            }
            if (tallerExistente.seSolapaCon(nuevoTaller)) {
                throw new ReglaNegocioException(
                        "Conflicto de horario con el taller \"" + tallerExistente.getNombre()
                                + "\" (" + Formato.fecha(tallerExistente.getFecha())
                                + " de " + Formato.hora(tallerExistente.getHoraInicio())
                                + " a " + Formato.hora(tallerExistente.getHoraFin()) + ")."
                );
            }
        }
    }
}
