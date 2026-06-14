package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.TutoriaReserva;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class TutoriaReservaRepository {

    public void save(TutoriaReserva reserva) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(reserva);
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

    public void update(TutoriaReserva reserva) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(reserva);
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

    public TutoriaReserva findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<TutoriaReserva> query = em.createQuery(
                    "SELECT r FROM TutoriaReserva r " +
                            "JOIN FETCH r.tutoria t " +
                            "JOIN FETCH t.usuario " +
                            "JOIN FETCH t.asignatura " +
                            "JOIN FETCH r.estudiante " +
                            "WHERE r.id = :id",
                    TutoriaReserva.class
            );

            query.setParameter("id", id);

            return query.getSingleResult();

        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public TutoriaReserva findByTutoriaId(Long tutoriaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<TutoriaReserva> query = em.createQuery(
                    "SELECT r FROM TutoriaReserva r " +
                            "JOIN FETCH r.tutoria " +
                            "WHERE r.tutoria.id = :tutoriaId",
                    TutoriaReserva.class
            );

            query.setParameter("tutoriaId", tutoriaId);

            return query.getSingleResult();

        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<TutoriaReserva> findByEstudiante(Long estudianteId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<TutoriaReserva> query = em.createQuery(
                    "SELECT r FROM TutoriaReserva r " +
                            "JOIN FETCH r.tutoria t " +
                            "JOIN FETCH r.estudiante " +
                            "JOIN FETCH t.usuario " +
                            "JOIN FETCH t.asignatura " +
                            "WHERE r.estudiante.id = :estudianteId " +
                            "ORDER BY t.fecha, t.horaInicio",
                    TutoriaReserva.class
            );

            query.setParameter("estudianteId", estudianteId);

            return query.getResultList();

        } finally {
            em.close();
        }
    }

    public List<TutoriaReserva> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<TutoriaReserva> query = em.createQuery(
                    "SELECT r FROM TutoriaReserva r " +
                            "JOIN FETCH r.tutoria t " +
                            "JOIN FETCH r.estudiante " +
                            "JOIN FETCH t.usuario " +
                            "JOIN FETCH t.asignatura " +
                            "ORDER BY t.fecha, t.horaInicio",
                    TutoriaReserva.class
            );

            return query.getResultList();

        } finally {
            em.close();
        }
    }
    public List<TutoriaReserva> findByTutor(Long tutorId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<TutoriaReserva> query = em.createQuery(
                    "SELECT r FROM TutoriaReserva r " +
                            "JOIN FETCH r.tutoria t " +
                            "JOIN FETCH r.estudiante " +
                            "JOIN FETCH t.usuario " +
                            "JOIN FETCH t.asignatura " +
                            "WHERE t.usuario.id = :tutorId " +
                            "ORDER BY t.fecha, t.horaInicio",
                    TutoriaReserva.class
            );

            query.setParameter("tutorId", tutorId);

            return query.getResultList();

        } finally {
            em.close();
        }
    }
}