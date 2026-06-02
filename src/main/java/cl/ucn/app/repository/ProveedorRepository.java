package cl.ucn.app.repository;

import java.util.List;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Proveedor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class ProveedorRepository {
    
    public void save(Proveedor proveedor) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(proveedor);
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

    public List<Proveedor> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Proveedor> query = em.createQuery("SELECT p FROM Proveedor p", Proveedor.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Proveedor findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Proveedor.class, id);
        } finally {
            em.close();
        }
    }

}
