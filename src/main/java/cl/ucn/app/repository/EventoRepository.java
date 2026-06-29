package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Evento;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class EventoRepository {

    public Evento save(Evento evento) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (evento.getId() == null) {
                em.persist(evento);
            } else {
                evento = em.merge(evento);
            }
            em.getTransaction().commit();
            return evento;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Evento findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Evento> query = em.createQuery(
                    "SELECT e FROM Evento e JOIN FETCH e.espacio LEFT JOIN FETCH e.expositores LEFT JOIN FETCH e.inscripciones WHERE e.id = :id",
                    Evento.class);
            query.setParameter("id", id);
            return query.getResultStream().findFirst().orElse(null);
        } finally {
            em.close();
        }
    }

    public List<Evento> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Evento> query = em.createQuery(
                    "SELECT DISTINCT e FROM Evento e JOIN FETCH e.espacio LEFT JOIN FETCH e.expositores WHERE e.fecha >= CURRENT_DATE ORDER BY e.fecha, e.horaInicio",
                    Evento.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public boolean existeConflictoHorario(Long espacioId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, Long excludeId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(e) FROM Evento e " +
                    "WHERE e.espacio.id = :espacioId AND e.fecha = :fecha " +
                    "AND e.estado <> 'CANCELADO' " +
                    "AND ((e.horaInicio < :horaFin AND e.horaFin > :horaInicio))";
            if (excludeId != null) {
                jpql += " AND e.id <> :excludeId";
            }
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("espacioId", espacioId);
            query.setParameter("fecha", fecha);
            query.setParameter("horaInicio", horaInicio);
            query.setParameter("horaFin", horaFin);
            if (excludeId != null) {
                query.setParameter("excludeId", excludeId);
            }
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }

    public boolean expositorTieneConflictoHorario(Long expositorId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, Long excludeId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(e) FROM Evento e JOIN e.expositores ex " +
                    "WHERE ex.id = :expositorId AND e.fecha = :fecha " +
                    "AND e.estado <> 'CANCELADO' " +
                    "AND ((e.horaInicio < :horaFin AND e.horaFin > :horaInicio))";
            if (excludeId != null) {
                jpql += " AND e.id <> :excludeId";
            }
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("expositorId", expositorId);
            query.setParameter("fecha", fecha);
            query.setParameter("horaInicio", horaInicio);
            query.setParameter("horaFin", horaFin);
            if (excludeId != null) {
                query.setParameter("excludeId", excludeId);
            }
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }

    public List<Evento> findByFecha(LocalDate fecha) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Evento> query = em.createQuery(
                    "SELECT DISTINCT e FROM Evento e JOIN FETCH e.espacio LEFT JOIN FETCH e.expositores WHERE e.fecha = :fecha ORDER BY e.horaInicio",
                    Evento.class);
            query.setParameter("fecha", fecha);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public void delete(Evento evento) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Evento managed = em.merge(evento);
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

    public boolean existenEventosActivosPorEspacio(Long espacioId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(e) FROM Evento e WHERE e.espacio.id = :espacioId AND e.estado <> 'CANCELADO'",
                    Long.class);
            query.setParameter("espacioId", espacioId);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }

    public List<Evento> findByTematica(String tematica) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Evento> query = em.createQuery(
                    "SELECT DISTINCT e FROM Evento e JOIN FETCH e.espacio LEFT JOIN FETCH e.expositores WHERE LOWER(e.tematica) LIKE LOWER(:tematica) ORDER BY e.fecha, e.horaInicio",
                    Evento.class);
            query.setParameter("tematica", "%" + tematica + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Evento> findByFilters(LocalDate fecha, String tematica) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder(
                    "SELECT DISTINCT e FROM Evento e JOIN FETCH e.espacio LEFT JOIN FETCH e.expositores WHERE 1=1 AND e.fecha >= CURRENT_DATE");
            if (fecha != null) {
                jpql.append(" AND e.fecha = :fecha");
            }
            if (tematica != null && !tematica.isBlank()) {
                jpql.append(" AND LOWER(e.tematica) LIKE LOWER(:tematica)");
            }
            jpql.append(" ORDER BY e.fecha, e.horaInicio");

            TypedQuery<Evento> query = em.createQuery(jpql.toString(), Evento.class);
            if (fecha != null) {
                query.setParameter("fecha", fecha);
            }
            if (tematica != null && !tematica.isBlank()) {
                query.setParameter("tematica", "%" + tematica + "%");
            }
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
