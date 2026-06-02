package cl.ucn.app.repository;

import java.util.List;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Salida;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class SalidaRepository {
    
    public void save(Salida salida) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(salida);
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

    public List<Salida> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Salida> query = em.createQuery("SELECT s FROM Salida s", Salida.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Salida findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Salida.class, id);
        } finally {
            em.close();
        }
    }

}
