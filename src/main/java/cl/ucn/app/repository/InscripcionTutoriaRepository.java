package cl.ucn.app.repository;

import cl.ucn.app.config.JpaTransaccion;
import cl.ucn.app.model.EstadoInscripcion;
import cl.ucn.app.model.InscripcionTutoria;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public interface InscripcionTutoriaRepository {

    Optional<InscripcionTutoria> findById(Long id);

    void save(InscripcionTutoria inscripcion);

    // ---- Dentro de una transacción ----

    void save(EntityManager em, InscripcionTutoria inscripcion);

    boolean existsByTutoriaIdAndEstudianteIdAndEstado(EntityManager em, Long tutoriaId, Long estudianteId,
                                                      EstadoInscripcion estado);

    long contarActivasPorTutoria(EntityManager em, Long tutoriaId);

    // ---- Lectura ----

    List<InscripcionTutoria> findByEstudianteId(Long estudianteId);

    default boolean existsByTutoriaIdAndEstudianteIdAndEstado(Long tutoriaId, Long estudianteId,
                                                              EstadoInscripcion estado) {
        return JpaTransaccion.leer(em ->
                existsByTutoriaIdAndEstudianteIdAndEstado(em, tutoriaId, estudianteId, estado));
    }

    default long contarActivasPorTutoria(Long tutoriaId) {
        return JpaTransaccion.leer(em -> contarActivasPorTutoria(em, tutoriaId));
    }
}
