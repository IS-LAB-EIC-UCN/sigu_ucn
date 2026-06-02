package cl.ucn.app.repository;

import java.util.List;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.MovimientoInventario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class MovimientoInventarioRepository {
    
    public void save(MovimientoInventario movimiento) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(movimiento);
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

    public List<MovimientoInventario> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<MovimientoInventario> query = em.createQuery("SELECT m FROM MovimientoInventario m", MovimientoInventario.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public MovimientoInventario findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(MovimientoInventario.class, id);
        } finally {
            em.close();
        }
    }
    
}
