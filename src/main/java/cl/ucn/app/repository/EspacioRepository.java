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

    public List<Espacio> findAll() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            TypedQuery<Espacio> query =
                    em.createQuery("SELECT e FROM Espacio e", Espacio.class);

            return query.getResultList();

        } finally {
            em.close();
        }
    }

    public void save(Espacio espacio) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(espacio);

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
    public void delete(Long id) {

        EntityManager em = JPAUtil.getEntityManager();

        try {

            em.getTransaction().begin();

            Espacio espacio = em.find(Espacio.class, id);

            if (espacio != null) {
                em.remove(espacio);
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


    public void update(Espacio espacio) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            em.merge(espacio);

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