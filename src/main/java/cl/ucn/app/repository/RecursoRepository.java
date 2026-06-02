package cl.ucn.app.repository;

import java.util.List;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Recurso;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

public class RecursoRepository {

    public void save(Recurso recurso) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(recurso);
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

    public List<Recurso> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Recurso> query = em.createQuery("SELECT r FROM Recurso r", Recurso.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Recurso findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Recurso.class, id);
        } finally {
            em.close();
        }
    }

    public List<Recurso> findByCategoria(String categoria) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Recurso> query = em.createQuery(
                    "SELECT r FROM Recurso r JOIN FETCH r.rol WHERE r.tipo = :categoria",
                    Recurso.class
            );
            query.setParameter("categoria", categoria);
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

}
