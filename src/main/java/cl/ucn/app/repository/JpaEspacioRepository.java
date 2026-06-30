package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Espacio;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;

public class JpaEspacioRepository implements EspacioRepository {
    @Override
    public void save(Espacio espacio) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (espacio.getId() == null) {
                em.persist(espacio);
            } else {
                em.merge(espacio);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Espacio> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Espacio.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public List<Espacio> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT e FROM Espacio e", Espacio.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Espacio> findByTipo(String tipo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT e FROM Espacio e WHERE e.tipo = :tipo", Espacio.class)
                    .setParameter("tipo", tipo)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
