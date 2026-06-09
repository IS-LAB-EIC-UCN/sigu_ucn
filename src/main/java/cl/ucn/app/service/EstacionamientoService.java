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

    public List<Espacio> obtenerEspaciosDisponibles() {
        return obtenerEspacios()
                .stream()
                .filter(Espacio::getDisponible)
                .toList();
    }

    public List<Vehiculo> obtenerVehiculosPorUsuario(Long usuarioId) {
        return vehiculoRepository.findByUsuarioId(usuarioId);
    }

    public List<Reserva> obtenerReservasPorUsuario(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId);
    }

    public void reservarEspacio(Long usuarioId, Long espacioId, Long vehiculoId) {

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

            if (!espacio.getDisponible()) {
                throw new IllegalStateException("El espacio no está disponible");
            }

            Reserva reserva = new Reserva(
                    LocalDate.now(),
                    LocalTime.of(8, 0),
                    LocalTime.of(10, 0),
                    "PENDIENTE",
                    usuario,
                    espacio,
                    vehiculo
            );

            espacio.setDisponible(false);

            em.persist(reserva);
            em.merge(espacio);

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