package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Asignatura;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class AsignaturaRepositoryImpl implements AsignaturaRepository {

    @Override
    public List<Asignatura> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Asignatura> query = em.createQuery(
                    "SELECT a FROM Asignatura a ORDER BY a.semestre, a.nombre",
                    Asignatura.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Asignatura> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Asignatura.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Asignatura> findByCodigo(String codigo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Asignatura> query = em.createQuery(
                    "SELECT a FROM Asignatura a WHERE a.codigo = :codigo",
                    Asignatura.class
            );
            query.setParameter("codigo", codigo);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Asignatura> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Asignatura> query = em.createQuery(
                    "SELECT a FROM Asignatura a WHERE a.id IN :ids",
                    Asignatura.class
            );
            query.setParameter("ids", ids);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void save(Asignatura asignatura) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(asignatura);
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

    @Override
    public void update(Asignatura asignatura) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(asignatura);
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

    @Override
    public void delete(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Asignatura asignatura = em.find(Asignatura.class, id);
            if (asignatura != null) {
                em.remove(asignatura);
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
}
