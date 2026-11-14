package cl.ucn.app.service.talleres.reglas;

import cl.ucn.app.exception.ReglaNegocioException;

/** Regla de negocio 2: un participante no puede inscribirse dos veces al mismo taller. */
public class ReglaSinDuplicado implements ReglaInscripcion {

    @Override
    public void validar(SolicitudInscripcion solicitud) {
        // TODO [Tarea 2 - RN2]: implementar.
        //  - Recorrer solicitud.inscripcionesVigentes() (son las INSCRITO o EN_ESPERA del participante).
        //  - Si alguna es del mismo taller (comparar getTaller().getId() con solicitud.taller().getId()),
        //    lanzar new ReglaNegocioException("Ya estás inscrito en este taller.")
        //    (si su estado es EN_ESPERA, el mensaje puede decir "Ya estás en la lista de espera de este taller.").
        //  - Esta regla ya está registrada en TalleresModule: solo falta el cuerpo.
    }
}
