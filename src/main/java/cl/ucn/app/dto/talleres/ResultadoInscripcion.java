package cl.ucn.app.dto.talleres;

import cl.ucn.app.model.talleres.EstadoInscripcion;

/**
 * Resultado de inscribir a un participante.
 * Si quedó en lista de espera, posicionEspera indica su lugar (1 = el siguiente en entrar).
 */
public record ResultadoInscripcion(Long inscripcionId, EstadoInscripcion estado, int posicionEspera) {

    public boolean quedoEnEspera() {
        return estado == EstadoInscripcion.EN_ESPERA;
    }
}
