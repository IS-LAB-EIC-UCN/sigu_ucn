package cl.ucn.app.service.talleres.reglas;

import cl.ucn.app.exception.ReglaNegocioException;

/**
 * Una regla de negocio que debe cumplirse para inscribir a un participante.
 * Para agregar una regla nueva se crea otra implementación y se registra en TalleresModule,
 * sin modificar InscripcionService (principio abierto/cerrado).
 */
public interface ReglaInscripcion {

    /** @throws ReglaNegocioException si la solicitud no cumple la regla */
    void validar(SolicitudInscripcion solicitud);
}
