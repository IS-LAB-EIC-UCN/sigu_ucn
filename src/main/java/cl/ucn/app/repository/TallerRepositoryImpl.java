package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Taller;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class TallerRepositoryImpl implements ITallerRepository {

    public void save(Taller taller) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (taller.getId() == null) {
                em.persist(taller);
            } else {
                em.merge(taller);
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

    public Taller findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Taller.class, id);
        } finally {
            em.close();
        }
    }

    public List<Taller> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Taller> query = em.createQuery(
                    "SELECT t FROM Taller t JOIN FETCH t.profesor LEFT JOIN FETCH t.espacio", Taller.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Taller> findByProfesor(Long profesorId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Taller> query = em.createQuery(
                    "SELECT t FROM Taller t JOIN FETCH t.profesor LEFT JOIN FETCH t.espacio WHERE t.profesor.id = :profesorId", Taller.class);
            query.setParameter("profesorId", profesorId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public void delete(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Taller t = em.find(Taller.class, id);
            if (t != null) {
                em.remove(t);
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

    public boolean existeConflicto(Long espacioId, Character bloque, java.time.LocalDate inicio, java.time.LocalDate fin) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(t) FROM Taller t WHERE t.espacio.id = :espacioId AND t.bloqueHorario = :bloque " +
                "AND t.fechaInicio <= :fin AND t.fechaFin >= :inicio", Long.class)
                .setParameter("espacioId", espacioId)
                .setParameter("bloque", bloque)
                .setParameter("inicio", inicio)
                .setParameter("fin", fin)
                .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }
}
