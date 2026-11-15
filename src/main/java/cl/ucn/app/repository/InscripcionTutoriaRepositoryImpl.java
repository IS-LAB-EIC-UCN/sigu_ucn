package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.InscripcionTutoria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.Optional;
import java.util.List;

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
    public List<InscripcionTutoria> findByEstudianteId(Long estudianteId) {
            EntityManager em = JPAUtil.getEntityManager();
            try {
                TypedQuery<InscripcionTutoria> query = em.createQuery(
                        "SELECT i FROM InscripcionTutoria i " +
                                "JOIN FETCH i.tutoria t JOIN FETCH t.asignatura " +
                                "JOIN FETCH t.tutor tu JOIN FETCH tu.estudiante " +
                                "WHERE i.estudiante.id = :estudianteId ORDER BY t.inicio",
                        InscripcionTutoria.class);
                query.setParameter("estudianteId", estudianteId);
                return query.getResultList();
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
}
