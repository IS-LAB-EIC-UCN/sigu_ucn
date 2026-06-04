package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Taller;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class TallerRepository {

    public void save(Taller taller) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (taller.getId() == null) {
                em.persist(taller);
            } else {
                em.merge(taller);
            }
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

    public Taller findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Taller.class, id);
        } finally {
            em.close();
        }
    }

    public List<Taller> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Taller> query = em.createQuery(
                    "SELECT t FROM Taller t JOIN FETCH t.profesor", Taller.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
