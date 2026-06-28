package cl.ucn.app.repository.biblioteca;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.biblioteca.Lector;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class LectorRepository {

    public void save(Lector lector) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            save(lector, em);
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

    public void save(Lector lector, EntityManager em) {
        if (lector.getId() == null) {
            em.persist(lector);
        } else {
            em.merge(lector);
        }
    }

    public void delete(Lector lector) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Lector managed = lector.getId() != null ? em.find(Lector.class, lector.getId()) : lector;
            if (managed != null) {
                em.remove(managed);
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

    public Lector findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Lector.class, id);
        } finally {
            em.close();
        }
    }

    public Lector findByRut(String rut) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Lector> query = em.createQuery(
                    "SELECT l FROM Lector l WHERE l.rut = :rut", Lector.class);
            query.setParameter("rut", rut);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public Lector findByCorreo(String correo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Lector> query = em.createQuery(
                    "SELECT l FROM Lector l WHERE l.correo = :correo", Lector.class);
            query.setParameter("correo", correo);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<Lector> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Lector> query = em.createQuery(
                    "SELECT l FROM Lector l", Lector.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}