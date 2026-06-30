package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Ticket;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación JPA de ITicketRepository.
 * Usa JPAUtil.getEntityManager() igual que los repos existentes del proyecto.
 */
public class TicketRepositoryImpl implements ITicketRepository {

    @Override
    public void guardar(Ticket ticket) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(ticket);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Ticket> buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Ticket.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public List<Ticket> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT t FROM Ticket t LEFT JOIN FETCH t.solicitante LEFT JOIN FETCH t.tecnico LEFT JOIN FETCH t.categoria",
                    Ticket.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Ticket> filtrar(String estado, String prioridad, Long tecnicoId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder(
                    "SELECT t FROM Ticket t LEFT JOIN FETCH t.solicitante LEFT JOIN FETCH t.categoria WHERE 1=1"
            );
            List<Object[]> params = new ArrayList<>();

            if (estado != null && !estado.isBlank()) {
                jpql.append(" AND t.estado = :estado");
                params.add(new Object[]{"estado", estado});
            }
            if (prioridad != null && !prioridad.isBlank()) {
                jpql.append(" AND t.prioridad = :prioridad");
                params.add(new Object[]{"prioridad", prioridad});
            }
            if (tecnicoId != null) {
                jpql.append(" AND t.tecnico.id = :tecnicoId");
                params.add(new Object[]{"tecnicoId", tecnicoId});
            }

            TypedQuery<Ticket> query = em.createQuery(jpql.toString(), Ticket.class);
            for (Object[] p : params) query.setParameter((String) p[0], p[1]);

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void actualizar(Ticket ticket) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(ticket);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
