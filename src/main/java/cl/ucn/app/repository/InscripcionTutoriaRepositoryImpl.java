package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.InscripcionTutoria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

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
