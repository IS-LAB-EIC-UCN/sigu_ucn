package cl.ucn.app.service.talleres.reglas;

import cl.ucn.app.exception.ReglaNegocioException;

/**
 * Regla de negocio 4: un participante no puede inscribirse en dos talleres con horario solapado.
 * También cuenta la lista de espera: si el participante sube desde la espera, no debe quedar con
 * dos talleres al mismo tiempo.
 */
public class ReglaSinSolapamiento implements ReglaInscripcion {

    @Override
    public void validar(SolicitudInscripcion solicitud) {
        // TODO [Tarea 2 - RN4 / RF10]: implementar.
        //  - Recorrer solicitud.inscripcionesVigentes().
        //  - Ignorar la del mismo taller (de eso se encarga ReglaSinDuplicado).
        //  - Si existente.getTaller().seSolapaCon(solicitud.taller()) -> lanzar ReglaNegocioException
        //    con un mensaje que diga con qué taller se cruza y su horario.
        //  - Depende de Taller.seSolapaCon(), que también es parte de esta tarea.
    }
}
