package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Expositor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class ExpositorRepository {

    public Expositor save(Expositor expositor) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (expositor.getId() == null) {
                em.persist(expositor);
            } else {
                expositor = em.merge(expositor);
            }
            em.getTransaction().commit();
            return expositor;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Expositor findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Expositor.class, id);
        } finally {
            em.close();
        }
    }

    public List<Expositor> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Expositor> query = em.createQuery(
                    "SELECT e FROM Expositor e ORDER BY e.nombre",
                    Expositor.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Expositor findByEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Expositor> query = em.createQuery(
                    "SELECT e FROM Expositor e WHERE e.email = :email",
                    Expositor.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
}
