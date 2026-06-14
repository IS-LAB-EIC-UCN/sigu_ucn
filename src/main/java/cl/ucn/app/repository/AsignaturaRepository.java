package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Asignatura;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class AsignaturaRepository {

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

    public Asignatura findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Asignatura.class, id);
        } finally {
            em.close();
        }
    }

    public Asignatura findByCodigo(String codigo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Asignatura> query = em.createQuery(
                    "SELECT a FROM Asignatura a WHERE a.codigo = :codigo",
                    Asignatura.class
            );
            query.setParameter("codigo", codigo);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<Asignatura> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Asignatura> query = em.createQuery(
                    "SELECT a FROM Asignatura a ORDER BY a.nombre",
                    Asignatura.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}