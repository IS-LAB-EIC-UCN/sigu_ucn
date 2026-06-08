package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Ticket;
import jakarta.persistence.EntityManager;

import java.util.List;

public class TicketRepository {

    public void guardar(Ticket ticket) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(ticket);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Ticket buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(Ticket.class, id);
        } finally {
            em.close();
        }
    }

    public List<Ticket> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT t FROM Ticket t",
                    Ticket.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    public void actualizar(Ticket ticket) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(ticket);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void eliminar(Long id) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            Ticket ticket = em.find(Ticket.class, id);

            if (ticket != null) {
                em.getTransaction().begin();
                em.remove(ticket);
                em.getTransaction().commit();
            }
        } finally {
            em.close();
        }
    }
}