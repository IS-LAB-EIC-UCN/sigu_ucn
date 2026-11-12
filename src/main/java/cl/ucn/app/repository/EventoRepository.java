
package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Evento;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class EventoRepository implements EventoStore {

    public void save(Evento evento) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            bloquearYValidar(em, evento);
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
                    "SELECT DISTINCT e FROM Evento e JOIN FETCH e.organizador o JOIN FETCH o.rol LEFT JOIN FETCH e.expositor LEFT JOIN FETCH e.espacio LEFT JOIN FETCH e.expositores ORDER BY e.fecha ASC",
                    Evento.class);

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Evento findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT DISTINCT e FROM Evento e " +
                            "JOIN FETCH e.organizador LEFT JOIN FETCH e.expositores " +
                            "WHERE e.id = :id",
                    Evento.class)
                    .setParameter("id", id)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

        } finally {
            em.close();
        }
    }

    public void update(Evento evento) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            bloquearYValidar(em, evento);
            Evento actual = em.find(Evento.class, evento.getId(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
            if (actual == null) throw new IllegalArgumentException("Evento no encontrado");
            if ("CANCELADO".equals(actual.getEstado()) && !"CANCELADO".equals(evento.getEstado()))
                throw new IllegalArgumentException("El evento fue cancelado. Actualiza la pantalla.");
            long inscritos = em.createQuery("SELECT COUNT(i) FROM Inscripcion i WHERE i.evento.id = :id AND i.estado = 'INSCRITO'", Long.class)
                    .setParameter("id", evento.getId()).getSingleResult();
            if (evento.getCupoMaximo() < inscritos) {
                throw new IllegalArgumentException("El cupo no puede ser menor que la cantidad de inscritos");
            }
            em.merge(evento);

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

    /** Recupera el evento sin reservar su horario antiguo ni reactivar asistentes. */
    public void reactivar(Long id) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            try {
                em.getTransaction().begin();
                em.createNativeQuery("LOCK TABLE eventos IN SHARE ROW EXCLUSIVE MODE").executeUpdate();
                Evento evento = em.find(Evento.class, id, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
                if (evento == null) throw new IllegalArgumentException("Evento no encontrado.");
                if (!"CANCELADO".equals(evento.getEstado()))
                    throw new IllegalArgumentException("Solo se pueden reactivar eventos cancelados.");
                em.createQuery("UPDATE Inscripcion SET estado = 'CANCELADO', asistencia = false WHERE evento.id = :id AND estado = 'INSCRITO'")
                    .setParameter("id", id).executeUpdate();
                evento.setEstado("BORRADOR");
                em.getTransaction().commit();
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw e;
            }
        }
    }

    // Serializa escrituras de agenda. La comprobaci?n y la escritura comparten transacci?n.
    private void bloquearYValidar(EntityManager em, Evento evento) {
        em.createNativeQuery("LOCK TABLE eventos IN SHARE ROW EXCLUSIVE MODE").executeUpdate();
        if ("CANCELADO".equals(evento.getEstado())) return;
        long conflictos = em.createQuery("SELECT COUNT(e) FROM Evento e WHERE e.estado <> 'CANCELADO' " +
                "AND e.fecha = :fecha AND e.horaInicio < :fin AND e.horaFin > :inicio " +
                "AND lower(trim(e.lugar)) = lower(trim(:lugar)) AND (:id IS NULL OR e.id <> :id)", Long.class)
                .setParameter("fecha", evento.getFecha()).setParameter("fin", evento.getHoraFin())
                .setParameter("inicio", evento.getHoraInicio()).setParameter("lugar", evento.getLugar())
                .setParameter("id", evento.getId()).getSingleResult();
        if (conflictos > 0) throw new IllegalArgumentException("La ubicación ya tiene un evento en ese horario.");
    }

}
