package cl.ucn.app.repository;

import java.util.List;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Entrada;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class EntradaRepository {
    
    public void save(Entrada entrada) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entrada);
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

    public List<Entrada> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Entrada> query = em.createQuery("SELECT e FROM Entrada e", Entrada.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Entrada findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Entrada.class, id);
        } finally {
            em.close();
        }
    }

}
