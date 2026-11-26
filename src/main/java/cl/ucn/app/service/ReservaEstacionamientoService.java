
package cl.ucn.app.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

import cl.ucn.app.model.Estacionamiento;
import cl.ucn.app.model.ReservaEstacionamiento;
import cl.ucn.app.model.TipoEstacionamiento;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.model.Vehiculo;
import cl.ucn.app.repository.EstacionamientoRepository;
import cl.ucn.app.repository.ReservaEstacionamientoRepository;
import cl.ucn.app.repository.UsuarioRepository;
import cl.ucn.app.repository.VehiculoRepository;

public class ReservaEstacionamientoService {

    private final ReservaEstacionamientoRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final VehiculoRepository vehiculoRepository;
    private final EstacionamientoRepository estacionamientoRepository;
    private final Clock clock;

    public ReservaEstacionamientoService() {
        this(
            new ReservaEstacionamientoRepository(),
            new UsuarioRepository(),
            new VehiculoRepository(),
            new EstacionamientoRepository()
        );
    }

    public ReservaEstacionamientoService(
            ReservaEstacionamientoRepository reservaRepository,
            UsuarioRepository usuarioRepository,
            VehiculoRepository vehiculoRepository,
            EstacionamientoRepository estacionamientoRepository) {

        this(reservaRepository, usuarioRepository, vehiculoRepository,
                estacionamientoRepository, Clock.system(ZoneId.of("America/Santiago")));
    }

    public ReservaEstacionamientoService(
            ReservaEstacionamientoRepository reservaRepository,
            UsuarioRepository usuarioRepository,
            VehiculoRepository vehiculoRepository,
            EstacionamientoRepository estacionamientoRepository,
            Clock clock) {

        this.reservaRepository = Objects.requireNonNull(reservaRepository);
        this.usuarioRepository = Objects.requireNonNull(usuarioRepository);
        this.vehiculoRepository = Objects.requireNonNull(vehiculoRepository);
        this.estacionamientoRepository = Objects.requireNonNull(estacionamientoRepository);
        this.clock = Objects.requireNonNull(clock);
    }

    public List<Estacionamiento> listarDisponiblesParaUsuario(Long usuarioId) {

        if (usuarioId == null) {
            throw new IllegalArgumentException(
                    "El identificador del usuario es obligatorio.");
        }

        Usuario usuario = reservaRepository.findUsuarioConRol(usuarioId);

        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no existe.");
        }

        boolean esDocente = usuario.getRol() != null
                && "DOCENTE".equals(usuario.getRol().getNombre());

        return estacionamientoRepository.findDisponiblesParaUsuario(esDocente);
    }

    public ReservaEstacionamiento registrarReserva(
            Long usuarioId,
            Long vehiculoId,
            Long estacionamientoId,
            LocalDateTime inicio,
            LocalDateTime fin) {

        if (usuarioId == null || vehiculoId == null || estacionamientoId == null) {
            throw new IllegalArgumentException("Los identificadores son obligatorios.");
        }

        if (inicio == null || fin == null || !fin.isAfter(inicio)) {
            throw new IllegalArgumentException("El intervalo de reserva no es válido.");
        }

        if (inicio.isBefore(LocalDateTime.now(clock))) {
            throw new IllegalArgumentException("La fecha y hora de inicio no pueden estar en el pasado.");
        }

        Usuario usuario = reservaRepository.findUsuarioConRol(usuarioId);

        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no existe.");
        }

        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId);

        if (vehiculo == null) {
            throw new IllegalArgumentException("El vehículo no existe.");
        }

        if (!vehiculo.getPropietario().getId().equals(usuarioId)) {
            throw new IllegalArgumentException("El vehículo no pertenece al usuario.");
        }

        return estacionamientoRepository.conEstacionamientoBloqueado(estacionamientoId, (em, estacionamiento) -> {
            if (estacionamiento == null) {
                throw new IllegalArgumentException("El estacionamiento no existe.");
            }

            if (!Boolean.TRUE.equals(estacionamiento.getHabilitado())) {
                throw new IllegalArgumentException("El estacionamiento no está habilitado.");
            }

            if (estacionamiento.getTipo() == TipoEstacionamiento.DOCENTES
                    && (usuario.getRol() == null || !"DOCENTE".equals(usuario.getRol().getNombre()))) {
                throw new IllegalArgumentException("Este estacionamiento es exclusivo para docentes.");
            }

            if (reservaRepository.existsOverlappingReservation(em, estacionamientoId, inicio, fin)) {
                throw new IllegalArgumentException("El estacionamiento ya está reservado en ese horario.");
            }

            if (reservaRepository.existsOverlappingReservationForVehicle(em, vehiculoId, inicio, fin)) {
                throw new IllegalArgumentException("El vehículo ya tiene una reserva en ese horario.");
            }

            ReservaEstacionamiento reserva = new ReservaEstacionamiento(
                    inicio, fin, "PENDIENTE", usuario, vehiculo, estacionamiento);
            reservaRepository.save(em, reserva);
            return reserva;
        });
    }
}
