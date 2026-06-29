package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Espacio;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class EspacioRepository {

    public Espacio findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Espacio.class, id);
        } finally {
            em.close();
        }
    }

    public List<Espacio> findDisponibles() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Espacio> query = em.createQuery(
                    "SELECT e FROM Espacio e WHERE e.disponible = true ORDER BY e.nombre",
                    Espacio.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Espacio> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Espacio> query = em.createQuery(
                    "SELECT e FROM Espacio e ORDER BY e.nombre",
                    Espacio.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Espacio save(Espacio espacio) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (espacio.getId() == null) {
                em.persist(espacio);
            } else {
                espacio = em.merge(espacio);
            }
            em.getTransaction().commit();
            return espacio;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void delete(Espacio espacio) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Espacio managed = em.merge(espacio);
            em.remove(managed);
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
