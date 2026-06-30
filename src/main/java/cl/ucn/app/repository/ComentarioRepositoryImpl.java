package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Comentario;
import jakarta.persistence.EntityManager;
import java.util.List;

public class ComentarioRepositoryImpl implements IComentarioRepository {

    @Override
    public void guardar(Comentario comentario) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(comentario);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Comentario> listarPorTicket(Long ticketId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT c FROM Comentario c LEFT JOIN FETCH c.autor WHERE c.ticket.id = :ticketId ORDER BY c.fecha ASC",
                    Comentario.class
            ).setParameter("ticketId", ticketId).getResultList();
        } finally {
            em.close();
        }
    }
}
