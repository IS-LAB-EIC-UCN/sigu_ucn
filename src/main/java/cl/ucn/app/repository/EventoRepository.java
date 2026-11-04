
package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Evento;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class EventoRepository {

    public void save(Evento evento) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(evento);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Evento> findAll() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            TypedQuery<Evento> query = em.createQuery(
                    "SELECT e FROM Evento e ORDER BY e.fecha ASC",
                    Evento.class);

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Evento findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(Evento.class, id);
        } finally {
            em.close();
        }
    }
}
