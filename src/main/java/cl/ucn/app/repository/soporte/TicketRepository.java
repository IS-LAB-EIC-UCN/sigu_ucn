package cl.ucn.app.repository.soporte;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.soporte.Ticket;
import jakarta.persistence.EntityManager;

public class TicketRepository {
    public Ticket guardar(Ticket ticket) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(ticket);
            em.getTransaction().commit();
            return ticket;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
