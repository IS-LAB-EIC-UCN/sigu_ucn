package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.EstadoMulta;
import cl.ucn.app.model.Multa;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class MultaRepositoryImpl implements MultaRepository {

    @Override
    public Optional<Multa> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Multa> q = em.createQuery(
                "SELECT m FROM Multa m " +
                "JOIN FETCH m.prestamo p " +
                "JOIN FETCH p.lector " +
                "WHERE m.id = :id", Multa.class);
            q.setParameter("id", id);
            return Optional.of(q.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            if (!JPAUtil.isManagedTransaction()) em.close();
        }
    }

    @Override
    public Optional<Multa> findByPrestamoId(Long prestamoId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Multa> q = em.createQuery(
                "SELECT m FROM Multa m WHERE m.prestamo.id = :prestamoId", Multa.class);
            q.setParameter("prestamoId", prestamoId);
            return Optional.of(q.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            if (!JPAUtil.isManagedTransaction()) em.close();
        }
    }

    @Override
    public List<Multa> findByLectorId(Long lectorId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Multa> q = em.createQuery(
                "SELECT m FROM Multa m " +
                "JOIN FETCH m.prestamo p " +
                "WHERE p.lector.id = :lectorId ORDER BY m.fechaGeneracion DESC", Multa.class);
            q.setParameter("lectorId", lectorId);
            return q.getResultList();
        } finally {
            if (!JPAUtil.isManagedTransaction()) em.close();
        }
    }

    @Override
    public List<Multa> findByLectorIdAndEstado(Long lectorId, EstadoMulta estado) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Multa> q = em.createQuery(
                "SELECT m FROM Multa m " +
                "JOIN FETCH m.prestamo p " +
                "WHERE p.lector.id = :lectorId AND m.estado = :estado ORDER BY m.fechaGeneracion DESC", Multa.class);
            q.setParameter("lectorId", lectorId);
            q.setParameter("estado", estado);
            return q.getResultList();
        } finally {
            if (!JPAUtil.isManagedTransaction()) em.close();
        }
    }

    @Override
    public Multa save(Multa multa) {
        EntityManager em = JPAUtil.getEntityManager();
        boolean managed = JPAUtil.isManagedTransaction();
        try {
            if (!managed) em.getTransaction().begin();
            em.persist(multa);
            if (!managed) em.getTransaction().commit();
            return multa;
        } catch (Exception e) {
            if (!managed && em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            if (!managed) em.close();
        }
    }

    @Override
    public void update(Multa multa) {
        EntityManager em = JPAUtil.getEntityManager();
        boolean managed = JPAUtil.isManagedTransaction();
        try {
            if (!managed) em.getTransaction().begin();
            em.merge(multa);
            if (!managed) em.getTransaction().commit();
        } catch (Exception e) {
            if (!managed && em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            if (!managed) em.close();
        }
    }
}
