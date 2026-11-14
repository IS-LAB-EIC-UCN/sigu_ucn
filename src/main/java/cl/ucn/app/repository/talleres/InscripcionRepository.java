package cl.ucn.app.repository.talleres;

import cl.ucn.app.model.talleres.EstadoInscripcion;
import cl.ucn.app.model.talleres.Inscripcion;

import java.util.List;
import java.util.Optional;

/** Acceso a datos de inscripciones. */
public interface InscripcionRepository {

    Inscripcion save(Inscripcion inscripcion);

    /** Guarda varios cambios en una sola transacción (por ejemplo: anular + promover). */
    void updateAll(List<Inscripcion> inscripciones);

    /** Busca por id trayendo también el taller y el usuario. */
    Optional<Inscripcion> findById(Long id);

    long countByTallerAndEstado(Long tallerId, EstadoInscripcion estado);

    /** Inscripciones INSCRITO o EN_ESPERA del participante, con su taller cargado. */
    List<Inscripcion> findVigentesByUsuario(Long usuarioId);

    /** Todas las inscripciones del participante (incluye anuladas), más recientes primero. */
    List<Inscripcion> findByUsuario(Long usuarioId);

    /** Inscripciones de un taller en un estado, ordenadas por fecha de inscripción (orden de llegada). */
    List<Inscripcion> findByTallerAndEstado(Long tallerId, EstadoInscripcion estado);

    /** La inscripción en espera más antigua del taller (la siguiente en recibir cupo). */
    Optional<Inscripcion> findPrimeraEnEspera(Long tallerId);
}
