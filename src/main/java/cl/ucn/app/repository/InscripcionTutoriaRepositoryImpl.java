package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.config.JpaTransaccion;
import cl.ucn.app.model.EstadoInscripcion;
import cl.ucn.app.model.InscripcionTutoria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class InscripcionTutoriaRepositoryImpl implements InscripcionTutoriaRepository {

    @Override
    public Optional<InscripcionTutoria> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<InscripcionTutoria> query = em.createQuery(
                    "SELECT i FROM InscripcionTutoria i JOIN FETCH i.tutoria " +
                            "JOIN FETCH i.estudiante WHERE i.id = :id",
                    InscripcionTutoria.class
            );
            query.setParameter("id", id);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public long countActivasByTutoriaId(Long tutoriaId) {
            EntityManager em = JPAUtil.getEntityManager();
            try {
                TypedQuery<Long> query = em.createQuery(
                        "SELECT COUNT(i) FROM InscripcionTutoria i " +
                                "WHERE i.tutoria.id = :tutoriaId AND i.estado = cl.ucn.app.model.EstadoInscripcion.RESERVADA",
                        Long.class);
                query.setParameter("tutoriaId", tutoriaId);
                return query.getSingleResult();
            } finally {
                em.close();
            }
        }

    @Override
    public void save(InscripcionTutoria inscripcion) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(inscripcion);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void save(EntityManager em, InscripcionTutoria inscripcion) {
        em.persist(inscripcion);
        em.flush();
    }

    @Override
    public boolean existsByTutoriaIdAndEstudianteIdAndEstado(EntityManager em, Long tutoriaId, Long estudianteId,
                                                             EstadoInscripcion estado) {
        Long cantidad = em.createQuery(
                        "SELECT COUNT(i) FROM InscripcionTutoria i WHERE i.tutoria.id = :tutoriaId " +
                                "AND i.estudiante.id = :estudianteId AND i.estado = :estado",
                        Long.class)
                .setParameter("tutoriaId", tutoriaId)
                .setParameter("estudianteId", estudianteId)
                .setParameter("estado", estado)
                .getSingleResult();
        return cantidad > 0;
    }

    @Override
    public long contarActivasPorTutoria(EntityManager em, Long tutoriaId) {
        return em.createQuery(
                        "SELECT COUNT(i) FROM InscripcionTutoria i " +
                                "WHERE i.tutoria.id = :tutoriaId AND i.estado = :estado",
                        Long.class)
                .setParameter("tutoriaId", tutoriaId)
                .setParameter("estado", EstadoInscripcion.RESERVADA)
                .getSingleResult();
    }

    @Override
    public List<InscripcionTutoria> findByEstudianteId(Long estudianteId) {
        return JpaTransaccion.leer(em -> em.createQuery(
                        "SELECT i FROM InscripcionTutoria i JOIN FETCH i.tutoria t JOIN FETCH t.asignatura " +
                                "JOIN FETCH t.tutor tu JOIN FETCH tu.estudiante " +
                                "WHERE i.estudiante.id = :estudianteId ORDER BY t.inicio",
                        InscripcionTutoria.class)
                .setParameter("estudianteId", estudianteId)
                .getResultList());
    }
}
