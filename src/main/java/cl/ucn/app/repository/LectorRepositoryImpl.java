package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Lector;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class LectorRepositoryImpl implements LectorRepository {

    @Override
    public Optional<Lector> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Lector.class, id));
        } finally {
            if (!JPAUtil.isManagedTransaction()) em.close();
        }
    }

    @Override
    public Optional<Lector> findByRut(String rut) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Lector> q = em.createQuery("SELECT l FROM Lector l WHERE l.rut = :rut", Lector.class);
            q.setParameter("rut", rut);
            return Optional.of(q.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            if (!JPAUtil.isManagedTransaction()) em.close();
        }
    }

    @Override
    public Optional<Lector> findByCorreo(String correo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Lector> q = em.createQuery("SELECT l FROM Lector l WHERE l.correo = :correo", Lector.class);
            q.setParameter("correo", correo);
            return Optional.of(q.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            if (!JPAUtil.isManagedTransaction()) em.close();
        }
    }

    @Override
    public List<Lector> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT l FROM Lector l ORDER BY l.nombre", Lector.class).getResultList();
        } finally {
            if (!JPAUtil.isManagedTransaction()) em.close();
        }
    }

    @Override
    public Lector save(Lector lector) {
        EntityManager em = JPAUtil.getEntityManager();
        boolean managed = JPAUtil.isManagedTransaction();
        try {
            if (!managed) em.getTransaction().begin();
            em.persist(lector);
            if (!managed) em.getTransaction().commit();
            return lector;
        } catch (Exception e) {
            if (!managed && em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            if (!managed) em.close();
        }
    }

    @Override
    public void update(Lector lector) {
        EntityManager em = JPAUtil.getEntityManager();
        boolean managed = JPAUtil.isManagedTransaction();
        try {
            if (!managed) em.getTransaction().begin();
            em.merge(lector);
            if (!managed) em.getTransaction().commit();
        } catch (Exception e) {
            if (!managed && em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            if (!managed) em.close();
        }
    }
}
