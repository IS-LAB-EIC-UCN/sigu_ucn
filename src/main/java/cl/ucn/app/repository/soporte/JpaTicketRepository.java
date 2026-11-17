package cl.ucn.app.repository.soporte;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.soporte.Ticket;
import jakarta.persistence.EntityManager;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.model.soporte.EstadoTicket;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;

public class JpaTicketRepository implements TicketRepository {
    @Override
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

    @Override
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

    @Override
    public Ticket asignarTecnico(Long ticketId, Long tecnicoId, Long administradorId) {
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

            Usuario administrador = em.find(Usuario.class, administradorId);

            if (administrador == null) {
                throw new NoSuchElementException("El administrador no existe.");
            }

            ticket.asignarTecnico(tecnico, administrador);
            em.getTransaction().commit();
            return ticket;
        } catch (RuntimeException e) {
            revertir(em);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Ticket> listarAsignados(Long tecnicoId) {
        return listarPorTecnicoYEstado(tecnicoId, EstadoTicket.ASIGNADO);
    }

    @Override
    public List<Ticket> listarEnResolucion(Long tecnicoId) {
        return listarPorTecnicoYEstado(tecnicoId, EstadoTicket.EN_RESOLUCION);
    }

    @Override
    public Ticket iniciarResolucion(Long ticketId, Long tecnicoId) {
        return modificarComoTecnico(ticketId, tecnicoId, (ticket, tecnico) -> ticket.iniciarResolucion(tecnico));
    }

    @Override
    public Ticket cerrarTicket(Long ticketId, Long tecnicoId, String resolucion) {
        return modificarComoTecnico(ticketId, tecnicoId, (ticket, tecnico) -> {
            ticket.registrarResolucion(resolucion, tecnico);
            ticket.cerrar(tecnico);
        });
    }

    private List<Ticket> listarPorTecnicoYEstado(Long tecnicoId, EstadoTicket estado) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                            "SELECT t FROM Ticket t " +
                                    "WHERE t.tecnicoAsignado.id = :tecnicoId " +
                                    "AND t.estado = :estado " +
                                    "ORDER BY t.fechaCreacion ASC", Ticket.class)
                    .setParameter("tecnicoId", tecnicoId)
                    .setParameter("estado", estado)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    private Ticket modificarComoTecnico(Long ticketId, Long tecnicoId, BiConsumer<Ticket, Usuario> cambio) {
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

            cambio.accept(ticket, tecnico);
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