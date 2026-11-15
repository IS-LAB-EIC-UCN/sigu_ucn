package cl.ucn.app.repository.soporte;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.soporte.Ticket;
import jakarta.persistence.EntityManager;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.model.soporte.EstadoTicket;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.NoSuchElementException;

public class TicketRepository {
    public Ticket guardar(Ticket ticket) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(ticket);
            em.getTransaction().commit();
            return ticket;
        } catch (RuntimeException e) {
            revertir(em);
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Ticket> listarPendientes() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                            "SELECT t FROM Ticket t " +
                                    "WHERE t.estado = :estado " +
                                    "AND t.tecnicoAsignado IS NULL " +
                                    "ORDER BY t.fechaCreacion ASC", Ticket.class)
                    .setParameter("estado", EstadoTicket.CREADO)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Ticket asignarTecnico(Long ticketId, Long tecnicoId) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            Ticket ticket = em.find(Ticket.class, ticketId, LockModeType.PESSIMISTIC_WRITE);

            if (ticket == null) {
                throw new NoSuchElementException("El ticket no existe.");
            }

            public List<Ticket> listarAsignados(Long tecnicoId) {
                EntityManager em = JPAUtil.getEntityManager();

                try {
                    return em.createQuery(
                                    "SELECT t FROM Ticket t " +
                                            "WHERE t.tecnicoAsignado.id = :tecnicoId " +
                                            "AND t.estado = :estado " +
                                            "ORDER BY t.fechaCreacion ASC", Ticket.class)
                            .setParameter("tecnicoId", tecnicoId)
                            .setParameter("estado", EstadoTicket.ASIGNADO)
                            .getResultList();
                } finally {
                    em.close();
                }
            }

            public Ticket iniciarResolucion(Long ticketId, Long tecnicoId) {
                EntityManager em = JPAUtil.getEntityManager();

                try {
                    em.getTransaction().begin();
                    Ticket ticket = em.find(Ticket.class, ticketId, LockModeType.PESSIMISTIC_WRITE);

                    if (ticket == null) {
                        throw new NoSuchElementException("El ticket no existe.");
                    }

                    Usuario tecnico = em.find(Usuario.class, tecnicoId);

                    if (tecnico == null) {
                        throw new NoSuchElementException("El técnico no existe.");
                    }

                    ticket.iniciarResolucion(tecnico);
                    em.getTransaction().commit();
                    return ticket;
                } catch (RuntimeException e) {
                    revertir(em);
                    throw e;
                } finally {
                    em.close();
                }
            }

            Usuario tecnico = em.find(Usuario.class, tecnicoId);

            if (tecnico == null) {
                throw new IllegalArgumentException("El técnico no existe.");
            }

            ticket.asignarTecnico(tecnico);
            em.getTransaction().commit();
            return ticket;
        } catch (RuntimeException e) {
            revertir(em);
            throw e;
        } finally {
            em.close();
        }
    }

    private void revertir(EntityManager em) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}