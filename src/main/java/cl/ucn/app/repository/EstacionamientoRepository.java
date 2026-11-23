
package cl.ucn.app.repository;

import java.util.List;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Estacionamiento;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;

public class EstacionamientoRepository {

    public void save(Estacionamiento estacionamiento) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();

            em.persist(estacionamiento);

            transaction.commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Estacionamiento findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(Estacionamiento.class, id);
        } finally {
            em.close();
        }
    }

    public Estacionamiento findByCodigo(String codigo) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT e FROM Estacionamiento e WHERE e.codigo = :codigo",
                    Estacionamiento.class
            ).setParameter("codigo", codigo)
             .getSingleResult();

        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<Estacionamiento> findAll() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT e FROM Estacionamiento e ORDER BY e.codigo",
                    Estacionamiento.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    public boolean existsByCodigo(String codigo) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT COUNT(e) FROM Estacionamiento e WHERE e.codigo = :codigo",
                    Long.class
            ).setParameter("codigo", codigo)
             .getSingleResult() > 0;
        } finally {
            em.close();
        }
    }
    
}
