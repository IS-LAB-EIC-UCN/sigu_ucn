package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Tutor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class TutorRepositoryImpl implements TutorRepository {

    @Override
    public List<Tutor> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Tutor> query = em.createQuery(
                    "SELECT t FROM Tutor t JOIN FETCH t.estudiante e " +
                            "LEFT JOIN FETCH t.asignaturasCertificadas ORDER BY e.nombre",
                    Tutor.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Tutor> findByUsuarioId(Long usuarioId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Tutor> query = em.createQuery(
                    "SELECT t FROM Tutor t JOIN FETCH t.estudiante e " +
                            "LEFT JOIN FETCH t.asignaturasCertificadas WHERE e.usuario.id = :usuarioId",
                    Tutor.class
            );
            query.setParameter("usuarioId", usuarioId);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public boolean existsByEstudianteId(Long estudianteId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(t) FROM Tutor t WHERE t.estudiante.id = :estudianteId",
                    Long.class
            );
            query.setParameter("estudianteId", estudianteId);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }

    @Override
    public void save(Tutor tutor) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(tutor);
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
