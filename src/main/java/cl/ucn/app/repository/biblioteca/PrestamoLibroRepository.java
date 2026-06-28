package cl.ucn.app.repository.biblioteca;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.biblioteca.Ejemplar;
import cl.ucn.app.model.biblioteca.Lector;
import cl.ucn.app.model.biblioteca.PrestamoLibro;
import cl.ucn.app.repository.biblioteca.api.IPrestamoLibroRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class PrestamoLibroRepository implements IPrestamoLibroRepository {

    public void save(PrestamoLibro prestamo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            save(prestamo, em);
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

    public void save(PrestamoLibro prestamo, EntityManager em) {
        if (prestamo.getId() == null) {
            em.persist(prestamo);
        } else {
            em.merge(prestamo);
        }
    }

    public PrestamoLibro findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(PrestamoLibro.class, id);
        } finally {
            em.close();
        }
    }

    public List<PrestamoLibro> findByLector(Lector lector) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<PrestamoLibro> query = em.createQuery(
                    "SELECT p FROM PrestamoLibro p WHERE p.lector = :lector ORDER BY p.fechaInicio DESC",
                    PrestamoLibro.class);
            query.setParameter("lector", lector);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public PrestamoLibro findActivoByEjemplar(Ejemplar ejemplar) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<PrestamoLibro> query = em.createQuery(
                    "SELECT p FROM PrestamoLibro p WHERE p.ejemplar = :ejemplar AND p.estado = 'ACTIVO'",
                    PrestamoLibro.class);
            query.setParameter("ejemplar", ejemplar);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<PrestamoLibro> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<PrestamoLibro> query = em.createQuery(
                    "SELECT p FROM PrestamoLibro p", PrestamoLibro.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}