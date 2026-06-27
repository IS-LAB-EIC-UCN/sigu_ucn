package cl.ucn.app.service;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Espacio;
import cl.ucn.app.model.Reserva;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.model.Vehiculo;
import cl.ucn.app.repository.EspacioRepository;
import cl.ucn.app.repository.ReservaRepository;
import cl.ucn.app.repository.VehiculoRepository;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class EstacionamientoService {

    private final EspacioRepository espacioRepository;
    private final ReservaRepository reservaRepository;
    private final VehiculoRepository vehiculoRepository;

    public EstacionamientoService() {
        this.espacioRepository = new EspacioRepository();
        this.reservaRepository = new ReservaRepository();
        this.vehiculoRepository = new VehiculoRepository();
    }

    public List<Espacio> obtenerEspacios() {
        return espacioRepository.findAll();
    }

    public Espacio obtenerPorId(Long id) {
        return espacioRepository.findById(id);
    }

    public void guardar(Espacio espacio) {
        espacioRepository.save(espacio);
    }

    public void eliminar(Long id) {
        espacioRepository.delete(id);
    }

    public void actualizar(Espacio espacio) {
        espacioRepository.update(espacio);
    }

    public List<Espacio> obtenerEspaciosDisponiblesPorRol(String usuarioRol) {
        return obtenerEspaciosPorRol(usuarioRol);
    }

    public List<Vehiculo> obtenerVehiculosPorUsuario(Long usuarioId) {
        return vehiculoRepository.findByUsuarioId(usuarioId);
    }

    public List<Reserva> obtenerReservasPorUsuario(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId);
    }

    public List<Reserva> obtenerReservasActivas() {
        return reservaRepository.findReservasActivas();
    }

    public void reservarEspacio(Long usuarioId, Long espacioId, Long vehiculoId,
                                Integer puestoNumero,
                                LocalDate fechaReserva,
                                LocalTime horaInicio,
                                LocalTime horaFin) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Usuario usuario = em.find(Usuario.class, usuarioId);
            Espacio espacio = em.find(Espacio.class, espacioId);
            Vehiculo vehiculo = em.find(Vehiculo.class, vehiculoId);

            if (usuario == null) {
                throw new IllegalArgumentException("Usuario no encontrado");
            }

            if (espacio == null) {
                throw new IllegalArgumentException("Espacio no encontrado");
            }

            if (vehiculo == null) {
                throw new IllegalArgumentException("Vehículo no encontrado");
            }

            if (!vehiculo.getUsuario().getId().equals(usuarioId)) {
                throw new IllegalStateException("El vehículo no pertenece al usuario");
            }

            if (!espacio.getTipo().startsWith("ESTACIONAMIENTO")) {
                throw new IllegalArgumentException("El espacio seleccionado no corresponde a un estacionamiento");
            }

            if (puestoNumero == null || puestoNumero < 1 || puestoNumero > espacio.getCapacidad()) {
                throw new IllegalArgumentException("El puesto seleccionado no es válido");
            }

            if (fechaReserva.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("No se puede reservar una fecha pasada");
            }

            if (fechaReserva.isEqual(LocalDate.now()) && horaInicio.isBefore(LocalTime.now())) {
                throw new IllegalArgumentException("No se puede reservar una hora que ya pasó");
            }

            if (!horaFin.isAfter(horaInicio)) {
                throw new IllegalArgumentException("La hora de término debe ser posterior a la hora de inicio");
            }

            Long reservasActivas = em.createQuery(
                            "SELECT COUNT(r) FROM Reserva r " +
                                    "WHERE r.espacio.id = :espacioId " +
                                    "AND r.puestoNumero = :puestoNumero " +
                                    "AND r.fechaReserva = :fechaReserva " +
                                    "AND r.estado IN ('PENDIENTE', 'APROBADA', 'ACTIVA') " +
                                    "AND r.horaInicio < :horaFin " +
                                    "AND r.horaFin > :horaInicio",
                            Long.class
                    )
                    .setParameter("espacioId", espacioId)
                    .setParameter("puestoNumero", puestoNumero)
                    .setParameter("fechaReserva", fechaReserva)
                    .setParameter("horaInicio", horaInicio)
                    .setParameter("horaFin", horaFin)
                    .getSingleResult();

            if (reservasActivas > 0) {
                throw new IllegalStateException("El puesto seleccionado ya está ocupado en ese horario");
            }

            Reserva reserva = new Reserva(
                    fechaReserva,
                    horaInicio,
                    horaFin,
                    "PENDIENTE",
                    puestoNumero,
                    usuario,
                    espacio,
                    vehiculo
            );

            em.persist(reserva);
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

    public List<Espacio> obtenerEspaciosPorRol(String usuarioRol) {
        return espacioRepository.findAll()
                .stream()
                .filter(e -> e.getTipo() != null)
                .filter(e -> e.getTipo().startsWith("ESTACIONAMIENTO"))
                .toList();
    }

    public void cancelarReserva(Long usuarioId, Long reservaId) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Reserva reserva = em.createQuery(
                            "SELECT r FROM Reserva r " +
                                    "JOIN FETCH r.usuario " +
                                    "WHERE r.id = :reservaId",
                            Reserva.class
                    )
                    .setParameter("reservaId", reservaId)
                    .getSingleResult();

            if (!reserva.getUsuario().getId().equals(usuarioId)) {
                throw new IllegalStateException("No puedes cancelar una reserva que no te pertenece");
            }

            if ("CANCELADA".equals(reserva.getEstado())) {
                throw new IllegalStateException("La reserva ya está cancelada");
            }

            if ("ACTIVA".equals(reserva.getEstado())) {
                throw new IllegalStateException("No se puede cancelar una reserva que ya registró ingreso");
            }

            if ("FINALIZADA".equals(reserva.getEstado())) {
                throw new IllegalStateException("No se puede cancelar una reserva finalizada");
            }

            reserva.setEstado("CANCELADA");

            em.merge(reserva);
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

    public List<Reserva> obtenerReservasPendientes() {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                            "SELECT r FROM Reserva r " +
                                    "JOIN FETCH r.usuario " +
                                    "JOIN FETCH r.espacio e " +
                                    "LEFT JOIN FETCH r.vehiculo " +
                                    "WHERE r.estado = 'PENDIENTE' " +
                                    "AND e.tipo LIKE 'ESTACIONAMIENTO%' " +
                                    "ORDER BY r.fechaReserva ASC, r.horaInicio ASC",
                            Reserva.class
                    )
                    .getResultList();

        } finally {
            em.close();
        }
    }

    public void aprobarReserva(Long reservaId) {
        cambiarEstadoReserva(reservaId, "APROBADA");
    }

    public void rechazarReserva(Long reservaId) {
        cambiarEstadoReserva(reservaId, "RECHAZADA");
    }

    private void cambiarEstadoReserva(Long reservaId, String nuevoEstado) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Reserva reserva = em.find(Reserva.class, reservaId);

            if (reserva == null) {
                throw new IllegalArgumentException("Reserva no encontrada");
            }

            if (!"PENDIENTE".equals(reserva.getEstado())) {
                throw new IllegalStateException("Solo se pueden modificar reservas pendientes");
            }

            reserva.setEstado(nuevoEstado);

            em.merge(reserva);
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

    public void registrarUsuarioConVehiculo(String nombre, String correo, String password,
                                            String rolNombre, String patente, String marca, String modelo) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Long correoExiste = em.createQuery(
                            "SELECT COUNT(u) FROM Usuario u WHERE LOWER(u.correo) = LOWER(:correo)",
                            Long.class
                    )
                    .setParameter("correo", correo)
                    .getSingleResult();

            if (correoExiste > 0) {
                throw new IllegalArgumentException("El correo ya está registrado");
            }

            Long patenteExiste = em.createQuery(
                            "SELECT COUNT(v) FROM Vehiculo v WHERE LOWER(v.patente) = LOWER(:patente)",
                            Long.class
                    )
                    .setParameter("patente", patente)
                    .getSingleResult();

            if (patenteExiste > 0) {
                throw new IllegalArgumentException("La patente ya está registrada");
            }

            var rol = em.createQuery(
                            "SELECT r FROM Rol r WHERE r.nombre = :nombre",
                            cl.ucn.app.model.Rol.class
                    )
                    .setParameter("nombre", rolNombre)
                    .getSingleResult();

            Usuario usuario = new Usuario(
                    nombre,
                    correo,
                    password,
                    true,
                    rol
            );

            em.persist(usuario);

            Vehiculo vehiculo = new Vehiculo(
                    patente.toUpperCase(),
                    marca,
                    modelo,
                    usuario
            );

            em.persist(vehiculo);

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
}