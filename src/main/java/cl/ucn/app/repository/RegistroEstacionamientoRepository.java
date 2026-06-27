package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.RegistroEstacionamiento;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;

import java.util.List;
import java.util.Optional;

public class RegistroEstacionamientoRepository {

    public RegistroEstacionamiento save(RegistroEstacionamiento registro) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            if (registro.getId() == null) {
                em.persist(registro);
            } else {
                registro = em.merge(registro);
            }

            em.getTransaction().commit();
            return registro;

        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }

    public Optional<RegistroEstacionamiento> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            RegistroEstacionamiento registro = em.find(RegistroEstacionamiento.class, id);
            return Optional.ofNullable(registro);

        } finally {
            em.close();
        }
    }

    public Optional<RegistroEstacionamiento> findByReservaId(Long reservaId) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            List<RegistroEstacionamiento> registros = em.createQuery(
                            "SELECT r FROM RegistroEstacionamiento r " +
                                    "JOIN FETCH r.reserva res " +
                                    "JOIN FETCH res.usuario " +
                                    "JOIN FETCH res.espacio " +
                                    "LEFT JOIN FETCH res.vehiculo " +
                                    "WHERE res.id = :reservaId",
                            RegistroEstacionamiento.class
                    )
                    .setParameter("reservaId", reservaId)
                    .getResultList();

            if (registros.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(registros.get(0));

        } finally {
            em.close();
        }
    }

    public List<RegistroEstacionamiento> findAll() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT r FROM RegistroEstacionamiento r " +
                            "JOIN FETCH r.reserva res " +
                            "JOIN FETCH res.usuario " +
                            "JOIN FETCH res.espacio " +
                            "LEFT JOIN FETCH res.vehiculo " +
                            "ORDER BY r.fechaHoraIngreso DESC",
                    RegistroEstacionamiento.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    public List<RegistroEstacionamiento> findByUsuarioId(Long usuarioId) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                            "SELECT r FROM RegistroEstacionamiento r " +
                                    "JOIN FETCH r.reserva res " +
                                    "JOIN FETCH res.usuario u " +
                                    "JOIN FETCH res.espacio " +
                                    "LEFT JOIN FETCH res.vehiculo " +
                                    "WHERE u.id = :usuarioId " +
                                    "ORDER BY r.fechaHoraIngreso DESC",
                            RegistroEstacionamiento.class
                    )
                    .setParameter("usuarioId", usuarioId)
                    .getResultList();

        } finally {
            em.close();
        }
    }

    public Optional<RegistroEstacionamiento> findIngresoActivoByReservaId(Long reservaId) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            List<RegistroEstacionamiento> registros = em.createQuery(
                            "SELECT r FROM RegistroEstacionamiento r " +
                                    "JOIN FETCH r.reserva res " +
                                    "WHERE res.id = :reservaId " +
                                    "AND r.estado = 'EN_USO'",
                            RegistroEstacionamiento.class
                    )
                    .setParameter("reservaId", reservaId)
                    .getResultList();

            if (registros.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(registros.get(0));

        } finally {
            em.close();
        }
    }


    public List<RegistroEstacionamiento> filtrarHistorial(String usuario, String patente, LocalDate fecha) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            String jpql =
                    "SELECT r FROM RegistroEstacionamiento r " +
                            "JOIN FETCH r.reserva res " +
                            "JOIN FETCH res.usuario u " +
                            "JOIN FETCH res.espacio e " +
                            "LEFT JOIN FETCH res.vehiculo v " +
                            "WHERE 1 = 1 ";

            if (usuario != null && !usuario.isBlank()) {
                jpql += "AND LOWER(u.nombre) LIKE LOWER(:usuario) ";
            }

            if (patente != null && !patente.isBlank()) {
                jpql += "AND LOWER(v.patente) LIKE LOWER(:patente) ";
            }

            if (fecha != null) {
                jpql += "AND res.fechaReserva = :fecha ";
            }

            jpql += "ORDER BY r.fechaHoraIngreso DESC";

            var query = em.createQuery(jpql, RegistroEstacionamiento.class);

            if (usuario != null && !usuario.isBlank()) {
                query.setParameter("usuario", "%" + usuario + "%");
            }

            if (patente != null && !patente.isBlank()) {
                query.setParameter("patente", "%" + patente + "%");
            }

            if (fecha != null) {
                query.setParameter("fecha", fecha);
            }

            return query.getResultList();

        } finally {
            em.close();
        }
    }
}