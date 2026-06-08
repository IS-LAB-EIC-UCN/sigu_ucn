package cl.ucn.app.repository;

import java.util.List;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Prestamo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class PrestamoRepository {
    
    public void save(Prestamo prestamo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(prestamo);
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

    public List<Prestamo> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Prestamo> query = em.createQuery("SELECT p FROM Prestamo p", Prestamo.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Prestamo findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Prestamo.class, id);
        } finally {
            em.close();
        }
    }

    public void alter(Long equipo_id, String estado) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.createQuery("UPDATE Prestamo p SET p.estado = :estado WHERE p.id = :equipo_id").executeUpdate();
            return;
        } finally {
            em.close();
        }
    }

}
