package cl.ucn.app.repository;

import cl.ucn.app.model.Recurso;
import cl.ucn.app.config.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

public class InventarioRepository {

    public List<Recurso> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Recurso> query = em.createQuery("SELECT r FROM Recurso r", Recurso.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    public List<Recurso> findByCategoria(String categoria) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT r FROM Recurso r WHERE LOWER(r.categoria) = :categoria";
            TypedQuery<Recurso> query = em.createQuery(jpql, Recurso.class);
            query.setParameter("categoria", categoria.toLowerCase().trim());
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }
}