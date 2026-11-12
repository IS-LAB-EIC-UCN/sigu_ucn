
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

            if (!evento.isDisponible()) {
                throw new IllegalStateException(
                        "El evento ya pasó");
            }

            // Buscar una inscripción anterior del usuario
            java.util.List<Inscripcion> anteriores = em.createQuery(
                    "SELECT i FROM Inscripcion i " +
                            "WHERE i.evento.id = :eventoId " +
                            "AND i.usuario.id = :usuarioId",
                    Inscripcion.class)
                    .setParameter("eventoId", eventoId)
                    .setParameter("usuarioId", usuarioId)
                    .getResultList();

            Inscripcion anterior = anteriores.isEmpty()
                    ? null
                    : anteriores.get(0);

            if (anterior != null && "INSCRITO".equals(anterior.getEstado())) {
                throw new IllegalStateException(
                        "El usuario ya tiene una inscripción activa en este evento");
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

            if (anterior != null) {

                // Reactivar una inscripción previamente cancelada
                anterior.setEstado("INSCRITO");
                anterior.setAsistencia(false);
                anterior.setFechaInscripcion(LocalDateTime.now());

            } else {

                // Crear una inscripción nueva
                Inscripcion inscripcion = new Inscripcion();

                inscripcion.setEvento(evento);
                inscripcion.setUsuario(usuario);
                inscripcion.setEstado("INSCRITO");
                inscripcion.setFechaInscripcion(LocalDateTime.now());

                em.persist(inscripcion);
            }

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

    public java.util.Map<Long, long[]> resumenPorEvento(Long usuarioId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            java.util.Map<Long, long[]> resumen = new java.util.HashMap<>();
            for (Object[] fila : em.createQuery(
                    "SELECT i.evento.id, COUNT(i), SUM(CASE WHEN i.usuario.id = :usuarioId THEN 1 ELSE 0 END) " +
                    "FROM Inscripcion i WHERE i.estado = 'INSCRITO' GROUP BY i.evento.id", Object[].class)
                    .setParameter("usuarioId", usuarioId).getResultList()) {
                resumen.put((Long) fila[0], new long[]{((Number) fila[1]).longValue(), ((Number) fila[2]).longValue()});
            }
            return resumen;
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

    public void cancelarInscripcion(Long eventoId, Long usuarioId) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            // Bloquear el evento para coordinar cambios de inscripciones
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
                        "El evento no está disponible para cancelar inscripciones");
            }

            // Buscar la inscripción activa del usuario
            java.util.List<Inscripcion> inscripciones = em.createQuery(
                    "SELECT i FROM Inscripcion i " +
                            "WHERE i.evento.id = :eventoId " +
                            "AND i.usuario.id = :usuarioId " +
                            "AND i.estado = 'INSCRITO'",
                    Inscripcion.class)
                    .setParameter("eventoId", eventoId)
                    .setParameter("usuarioId", usuarioId)
                    .getResultList();

            if (inscripciones.isEmpty()) {
                throw new IllegalStateException(
                        "No tienes una inscripción activa en este evento");
            }

            // Cancelación lógica: conservar el registro histórico
            Inscripcion inscripcion = inscripciones.get(0);
            inscripcion.setEstado("CANCELADO");

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

    public void registrarAsistencia(Long eventoId, Long inscripcionId, Long usuarioId, String rol, boolean asistio) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            try {
                em.getTransaction().begin();
                Evento evento=em.find(Evento.class,eventoId,LockModeType.PESSIMISTIC_WRITE);
                if(evento==null) throw new IllegalArgumentException("Evento no encontrado.");
                if(!cl.ucn.app.service.PermisosEventos.administra(rol,usuarioId,evento.getOrganizador().getId()))
                    throw new SecurityException("No tienes permisos para registrar asistencia.");
                if(!"PUBLICADO".equals(evento.getEstado()) && !"FINALIZADO".equals(evento.getEstado()))
                    throw new IllegalStateException("Solo se registra asistencia en eventos publicados o finalizados.");
                Inscripcion inscripcion=em.find(Inscripcion.class,inscripcionId,LockModeType.PESSIMISTIC_WRITE);
                if(inscripcion==null || !eventoId.equals(inscripcion.getEvento().getId()))
                    throw new IllegalArgumentException("La inscripción no pertenece al evento.");
                if(!"INSCRITO".equals(inscripcion.getEstado())) throw new IllegalStateException("La inscripción está cancelada.");
                inscripcion.setAsistencia(asistio);em.getTransaction().commit();
            } catch(RuntimeException ex) {
                if(em.getTransaction().isActive()) em.getTransaction().rollback();
                throw ex;
            }
        }
    }
    public java.util.List<Inscripcion> listarPorEvento(Long eventoId) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT i FROM Inscripcion i " +
                            "JOIN FETCH i.usuario " +
                            "WHERE i.evento.id = :eventoId " +
                            "ORDER BY i.fechaInscripcion DESC",
                    Inscripcion.class)
                    .setParameter("eventoId", eventoId)
                    .getResultList();

        } finally {
            em.close();
        }
    }

}
