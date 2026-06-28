package cl.ucn.app.repository.biblioteca;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.biblioteca.Ejemplar;
import cl.ucn.app.model.biblioteca.Libro;
import cl.ucn.app.repository.biblioteca.api.IEjemplarRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class EjemplarRepository implements IEjemplarRepository {

    public void save(Ejemplar ejemplar) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            save(ejemplar, em);
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

    public void save(Ejemplar ejemplar, EntityManager em) {
        if (ejemplar.getId() == null) {
            em.persist(ejemplar);
        } else {
            em.merge(ejemplar);
        }
    }

    public void delete(Ejemplar ejemplar) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Ejemplar managed = ejemplar.getId() != null ? em.find(Ejemplar.class, ejemplar.getId()) : ejemplar;
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

    public Ejemplar findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Ejemplar.class, id);
        } finally {
            em.close();
        }
    }

    public List<Ejemplar> findByLibro(Libro libro) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Ejemplar> query = em.createQuery(
                    "SELECT e FROM Ejemplar e WHERE e.libro = :libro", Ejemplar.class);
            query.setParameter("libro", libro);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Ejemplar> findDisponiblesByLibro(Libro libro) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Ejemplar> query = em.createQuery(
                    "SELECT e FROM Ejemplar e WHERE e.libro = :libro AND e.estado = 'DISPONIBLE'",
                    Ejemplar.class);
            query.setParameter("libro", libro);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Ejemplar> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Ejemplar> query = em.createQuery(
                    "SELECT e FROM Ejemplar e", Ejemplar.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}