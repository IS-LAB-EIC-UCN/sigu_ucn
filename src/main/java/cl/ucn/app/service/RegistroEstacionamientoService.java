package cl.ucn.app.service;

import cl.ucn.app.model.RegistroEstacionamiento;
import cl.ucn.app.model.Reserva;
import cl.ucn.app.repository.RegistroEstacionamientoRepository;
import cl.ucn.app.repository.ReservaRepository;
import java.time.LocalDate;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import cl.ucn.app.config.JPAUtil;
import jakarta.persistence.EntityManager;

public class RegistroEstacionamientoService {

    private final RegistroEstacionamientoRepository registroRepository;
    private final ReservaRepository reservaRepository;

    public RegistroEstacionamientoService() {
        this.registroRepository = new RegistroEstacionamientoRepository();
        this.reservaRepository = new ReservaRepository();
    }

    public RegistroEstacionamiento registrarIngreso(Long reservaId) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Reserva reserva = em.find(Reserva.class, reservaId);

            if (reserva == null) {
                throw new IllegalArgumentException("La reserva no existe.");
            }

            if (!"APROBADA".equalsIgnoreCase(reserva.getEstado())) {
                throw new IllegalArgumentException("La reserva debe estar aprobada para registrar ingreso.");
            }

            Long registrosExistentes = em.createQuery(
                            "SELECT COUNT(r) FROM RegistroEstacionamiento r " +
                                    "WHERE r.reserva.id = :reservaId",
                            Long.class
                    )
                    .setParameter("reservaId", reservaId)
                    .getSingleResult();

            if (registrosExistentes > 0) {
                throw new IllegalArgumentException("Esta reserva ya tiene un ingreso registrado.");
            }

            RegistroEstacionamiento registro =
                    new RegistroEstacionamiento(reserva, LocalDateTime.now(), "EN_USO");

            reserva.setEstado("ACTIVA");

            em.persist(registro);

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

    public RegistroEstacionamiento registrarSalida(Long registroId) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            RegistroEstacionamiento registro = em.createQuery(
                            "SELECT r FROM RegistroEstacionamiento r " +
                                    "JOIN FETCH r.reserva res " +
                                    "WHERE r.id = :registroId",
                            RegistroEstacionamiento.class
                    )
                    .setParameter("registroId", registroId)
                    .getSingleResult();

            if (!"EN_USO".equalsIgnoreCase(registro.getEstado())) {
                throw new IllegalArgumentException("El vehículo no tiene un ingreso activo.");
            }

            LocalDateTime salida = LocalDateTime.now();
            long minutos = Duration.between(registro.getFechaHoraIngreso(), salida).toMinutes();

            registro.setFechaHoraSalida(salida);
            registro.setTiempoUsoMinutos((int) minutos);
            registro.setEstado("FINALIZADO");

            Reserva reserva = registro.getReserva();
            reserva.setEstado("FINALIZADA");

            em.merge(reserva);
            em.merge(registro);

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

    public List<RegistroEstacionamiento> listarHistorial() {
        return registroRepository.findAll();
    }

    public List<RegistroEstacionamiento> listarHistorialPorUsuario(Long usuarioId) {
        return registroRepository.findByUsuarioId(usuarioId);
    }

    public List<RegistroEstacionamiento> filtrarHistorial(String usuario, String patente, LocalDate fecha) {
        return registroRepository.filtrarHistorial(usuario, patente, fecha);
    }
}