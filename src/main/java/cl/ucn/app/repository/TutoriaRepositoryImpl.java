package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.EstadoTutoria;
import cl.ucn.app.model.Tutoria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.Optional;

public class TutoriaRepositoryImpl implements TutoriaRepository {

    @Override
    public Optional<Tutoria> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Tutoria> query = em.createQuery(
                    "SELECT t FROM Tutoria t JOIN FETCH t.asignatura " +
                            "JOIN FETCH t.tutor tu JOIN FETCH tu.estudiante WHERE t.id = :id",
                    Tutoria.class
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
    public long countByAsignatura(Long asignaturaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(t) FROM Tutoria t WHERE t.asignatura.id = :asignaturaId",
                    Long.class
            );
            query.setParameter("asignaturaId", asignaturaId);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public long countByAsignaturaAndEstado(Long asignaturaId, EstadoTutoria estado) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(t) FROM Tutoria t " +
                            "WHERE t.asignatura.id = :asignaturaId AND t.estado = :estado",
                    Long.class
            );
            query.setParameter("asignaturaId", asignaturaId);
            query.setParameter("estado", estado);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public void save(Tutoria tutoria) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(tutoria);
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
