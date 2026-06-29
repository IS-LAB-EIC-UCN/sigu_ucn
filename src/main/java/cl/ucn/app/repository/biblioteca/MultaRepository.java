package cl.ucn.app.repository.biblioteca;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.biblioteca.Lector;
import cl.ucn.app.model.biblioteca.Multa;
import cl.ucn.app.repository.biblioteca.api.IMultaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class MultaRepository implements IMultaRepository {

    public void save(Multa multa) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            save(multa, em);
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

    public void save(Multa multa, EntityManager em) {
        if (multa.getId() == null) {
            em.persist(multa);
        } else {
            em.merge(multa);
        }
    }

    public Multa findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Multa> query = em.createQuery(
                "SELECT m FROM Multa m LEFT JOIN FETCH m.prestamo p LEFT JOIN FETCH p.lector LEFT JOIN FETCH p.ejemplar e LEFT JOIN FETCH e.libro l WHERE m.id = :id", Multa.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (jakarta.persistence.NoResultException ex) {
            return null;
        } finally {
            em.close();
        }
    }

    public Multa findByPrestamo(cl.ucn.app.model.biblioteca.PrestamoLibro prestamo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Multa> query = em.createQuery(
                "SELECT m FROM Multa m LEFT JOIN FETCH m.prestamo p LEFT JOIN FETCH p.lector LEFT JOIN FETCH p.ejemplar e LEFT JOIN FETCH e.libro l WHERE m.prestamo = :prestamo", Multa.class);
            query.setParameter("prestamo", prestamo);
            return query.getSingleResult();
        } catch (jakarta.persistence.NoResultException ex) {
            return null;
        } finally {
            em.close();
        }
    }

    public Multa findByPrestamo(cl.ucn.app.model.biblioteca.PrestamoLibro prestamo, EntityManager em) {
        try {
            TypedQuery<Multa> query = em.createQuery(
                "SELECT m FROM Multa m WHERE m.prestamo = :prestamo", Multa.class);
            query.setParameter("prestamo", prestamo);
            return query.getSingleResult();
        } catch (jakarta.persistence.NoResultException ex) {
            return null;
        }
    }

    public List<Multa> findPendientesByLector(Lector lector) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Multa> query = em.createQuery(
                    "SELECT m FROM Multa m WHERE m.prestamo.lector = :lector AND m.pagada = false",
                    Multa.class);
            query.setParameter("lector", lector);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Multa> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Multa> query = em.createQuery(
                    "SELECT m FROM Multa m", Multa.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}