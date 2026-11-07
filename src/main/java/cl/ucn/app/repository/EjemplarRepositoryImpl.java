package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Ejemplar;
import cl.ucn.app.model.EstadoEjemplar;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class EjemplarRepositoryImpl implements EjemplarRepository {

    @Override
    public Optional<Ejemplar> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Ejemplar.class, id));
        } finally {
            if (!JPAUtil.isManagedTransaction()) em.close();
        }
    }

    @Override
    public Optional<Ejemplar> findByCodigoInventario(String codigoInventario) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Ejemplar> q = em.createQuery(
                "SELECT e FROM Ejemplar e JOIN FETCH e.libro WHERE e.codigoInventario = :codigo", Ejemplar.class);
            q.setParameter("codigo", codigoInventario);
            return Optional.of(q.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            if (!JPAUtil.isManagedTransaction()) em.close();
        }
    }

    @Override
    public List<Ejemplar> findByLibroId(Long libroId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Ejemplar> q = em.createQuery(
                "SELECT e FROM Ejemplar e WHERE e.libro.id = :libroId ORDER BY e.codigoInventario", Ejemplar.class);
            q.setParameter("libroId", libroId);
            return q.getResultList();
        } finally {
            if (!JPAUtil.isManagedTransaction()) em.close();
        }
    }

    @Override
    public List<Ejemplar> findByLibroIdAndEstado(Long libroId, EstadoEjemplar estado) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Ejemplar> q = em.createQuery(
                "SELECT e FROM Ejemplar e WHERE e.libro.id = :libroId AND e.estado = :estado ORDER BY e.codigoInventario", Ejemplar.class);
            q.setParameter("libroId", libroId);
            q.setParameter("estado", estado);
            return q.getResultList();
        } finally {
            if (!JPAUtil.isManagedTransaction()) em.close();
        }
    }

    @Override
    public Ejemplar save(Ejemplar ejemplar) {
        EntityManager em = JPAUtil.getEntityManager();
        boolean managed = JPAUtil.isManagedTransaction();
        try {
            if (!managed) em.getTransaction().begin();
            em.persist(ejemplar);
            if (!managed) em.getTransaction().commit();
            return ejemplar;
        } catch (Exception e) {
            if (!managed && em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            if (!managed) em.close();
        }
    }

    @Override
    public void update(Ejemplar ejemplar) {
        EntityManager em = JPAUtil.getEntityManager();
        boolean managed = JPAUtil.isManagedTransaction();
        try {
            if (!managed) em.getTransaction().begin();
            em.merge(ejemplar);
            if (!managed) em.getTransaction().commit();
        } catch (Exception e) {
            if (!managed && em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            if (!managed) em.close();
        }
    }
}
