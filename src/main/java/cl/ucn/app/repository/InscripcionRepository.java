
package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Evento;
import cl.ucn.app.model.Inscripcion;
import cl.ucn.app.model.Usuario;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;

public class InscripcionRepository {

    public void inscribir(Long eventoId, Long usuarioId) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            // Bloquear el evento durante la inscripción
            Evento evento = em.find(
                    Evento.class,
                    eventoId,
                    LockModeType.PESSIMISTIC_WRITE);

            if (evento == null) {
                throw new IllegalArgumentException(
                        "Evento no encontrado");
            }

            if (!"PUBLICADO".equals(evento.getEstado())) {
                throw new IllegalStateException(
                        "El evento no está disponible para inscripciones");
            }

            if (evento.getFecha().isBefore(
                    java.time.LocalDate.now())) {
                throw new IllegalStateException(
                        "El evento ya pasó");
            }

            // Comprobar si el usuario ya está inscrito
            Long existentes = em.createQuery(
                    "SELECT COUNT(i) FROM Inscripcion i " +
                            "WHERE i.evento.id = :eventoId " +
                            "AND i.usuario.id = :usuarioId",
                    Long.class)
                    .setParameter("eventoId", eventoId)
                    .setParameter("usuarioId", usuarioId)
                    .getSingleResult();

            if (existentes > 0) {
                throw new IllegalStateException(
                        "El usuario ya tiene una inscripción en este evento");
            }

            // Contar las inscripciones activas
            Long inscritos = em.createQuery(
                    "SELECT COUNT(i) FROM Inscripcion i " +
                            "WHERE i.evento.id = :eventoId " +
                            "AND i.estado = 'INSCRITO'",
                    Long.class)
                    .setParameter("eventoId", eventoId)
                    .getSingleResult();

            if (inscritos >= evento.getCupoMaximo()) {
                throw new IllegalStateException(
                        "No quedan cupos disponibles");
            }

            Usuario usuario = em.find(Usuario.class, usuarioId);

            if (usuario == null || !Boolean.TRUE.equals(usuario.getActivo())) {
                throw new IllegalArgumentException(
                        "Usuario no encontrado o inactivo");
            }

            Inscripcion inscripcion = new Inscripcion();
            inscripcion.setEvento(evento);
            inscripcion.setUsuario(usuario);
            inscripcion.setEstado("INSCRITO");
            inscripcion.setFechaInscripcion(LocalDateTime.now());

            em.persist(inscripcion);

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

    public long contarInscritos(Long eventoId) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT COUNT(i) FROM Inscripcion i " +
                            "WHERE i.evento.id = :eventoId " +
                            "AND i.estado = 'INSCRITO'",
                    Long.class)
                    .setParameter("eventoId", eventoId)
                    .getSingleResult();

        } finally {
            em.close();
        }
    }

    public boolean estaInscrito(Long eventoId, Long usuarioId) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            Long cantidad = em.createQuery(
                    "SELECT COUNT(i) FROM Inscripcion i " +
                            "WHERE i.evento.id = :eventoId " +
                            "AND i.usuario.id = :usuarioId " +
                            "AND i.estado = 'INSCRITO'",
                    Long.class)
                    .setParameter("eventoId", eventoId)
                    .setParameter("usuarioId", usuarioId)
                    .getSingleResult();

            return cantidad > 0;

        } finally {
            em.close();
        }
    }

}
