package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.EstadoPrestamo;
import cl.ucn.app.model.Prestamo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class PrestamoRepositoryImpl implements PrestamoRepository {

    @Override
    public Optional<Prestamo> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Prestamo> q = em.createQuery(
                "SELECT p FROM Prestamo p " +
                "JOIN FETCH p.lector " +
                "JOIN FETCH p.ejemplar e " +
                "JOIN FETCH e.libro " +
                "LEFT JOIN FETCH p.multa " +
                "WHERE p.id = :id", Prestamo.class);
            q.setParameter("id", id);
            return Optional.of(q.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            if (!JPAUtil.isManagedTransaction()) em.close();
        }
    }

    @Override
    public Optional<Prestamo> findActivoPorEjemplarId(Long ejemplarId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Prestamo> q = em.createQuery(
                "SELECT p FROM Prestamo p " +
                "JOIN FETCH p.lector " +
                "JOIN FETCH p.ejemplar e " +
                "JOIN FETCH e.libro " +
                "WHERE p.ejemplar.id = :ejemplarId AND p.estado = :estado", Prestamo.class);
            q.setParameter("ejemplarId", ejemplarId);
            q.setParameter("estado", EstadoPrestamo.ACTIVO);
            return Optional.of(q.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            if (!JPAUtil.isManagedTransaction()) em.close();
        }
    }

    @Override
    public List<Prestamo> findByLectorId(Long lectorId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Prestamo> q = em.createQuery(
                "SELECT p FROM Prestamo p " +
                "JOIN FETCH p.ejemplar e " +
                "JOIN FETCH e.libro " +
                "LEFT JOIN FETCH p.multa " +
                "WHERE p.lector.id = :lectorId ORDER BY p.fechaPrestamo DESC", Prestamo.class);
            q.setParameter("lectorId", lectorId);
            return q.getResultList();
        } finally {
            if (!JPAUtil.isManagedTransaction()) em.close();
        }
    }

    @Override
    public List<Prestamo> findByLectorIdAndEstado(Long lectorId, EstadoPrestamo estado) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Prestamo> q = em.createQuery(
                "SELECT p FROM Prestamo p " +
                "JOIN FETCH p.ejemplar e " +
                "JOIN FETCH e.libro " +
                "LEFT JOIN FETCH p.multa " +
                "WHERE p.lector.id = :lectorId AND p.estado = :estado ORDER BY p.fechaPrestamo DESC", Prestamo.class);
            q.setParameter("lectorId", lectorId);
            q.setParameter("estado", estado);
            return q.getResultList();
        } finally {
            if (!JPAUtil.isManagedTransaction()) em.close();
        }
    }

    @Override
    public long countActivosByLectorId(Long lectorId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> q = em.createQuery(
                "SELECT COUNT(p) FROM Prestamo p WHERE p.lector.id = :lectorId AND p.estado = :estado", Long.class);
            q.setParameter("lectorId", lectorId);
            q.setParameter("estado", EstadoPrestamo.ACTIVO);
            return q.getSingleResult();
        } finally {
            if (!JPAUtil.isManagedTransaction()) em.close();
        }
    }

    @Override
    public Prestamo save(Prestamo prestamo) {
        EntityManager em = JPAUtil.getEntityManager();
        boolean managed = JPAUtil.isManagedTransaction();
        try {
            if (!managed) em.getTransaction().begin();
            em.persist(prestamo);
            if (!managed) em.getTransaction().commit();
            return prestamo;
        } catch (Exception e) {
            if (!managed && em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            if (!managed) em.close();
        }
    }

    @Override
    public void update(Prestamo prestamo) {
        EntityManager em = JPAUtil.getEntityManager();
        boolean managed = JPAUtil.isManagedTransaction();
        try {
            if (!managed) em.getTransaction().begin();
            em.merge(prestamo);
            if (!managed) em.getTransaction().commit();
        } catch (Exception e) {
            if (!managed && em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            if (!managed) em.close();
        }
    }
}
