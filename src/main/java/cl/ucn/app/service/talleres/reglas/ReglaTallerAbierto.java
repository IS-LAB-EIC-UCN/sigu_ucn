package cl.ucn.app.service.talleres.reglas;

import cl.ucn.app.exception.ReglaNegocioException;

/** Solo se puede inscribir en talleres activos cuya fecha no haya pasado. */
public class ReglaTallerAbierto implements ReglaInscripcion {

    @Override
    public void validar(SolicitudInscripcion solicitud) {
        if (!solicitud.taller().estaActivo()) {
            throw new ReglaNegocioException("El taller está cancelado y no acepta inscripciones.");
        }
        if (solicitud.taller().getFecha().isBefore(solicitud.hoy())) {
            throw new ReglaNegocioException("El taller ya se realizó; no acepta inscripciones.");
        }
    }
}
