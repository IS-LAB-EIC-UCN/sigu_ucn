
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

    private static final String RESERVA_NO_DISPONIBLE = "No se encontró la reserva solicitada.";

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

    public List<ResumenReserva> listarPropias(Long usuarioId) {
        exigirCuentaActiva(usuarioId);
        return resumirReservas(reservaRepository.findByUsuarioId(usuarioId), false);
    }

    public List<ResumenReserva> listarParaAdministracion(Long usuarioId) {
        exigirAdministrador(usuarioId);
        return resumirReservas(reservaRepository.findAllConDetalle(), true);
    }

    public void cancelarPropia(Long usuarioId, Long reservaId) {
        exigirCuentaActiva(usuarioId);
        cancelar(reservaId, usuarioId);
    }

    public void cancelarComoAdministrador(Long usuarioId, Long reservaId) {
        exigirAdministrador(usuarioId);
        cancelar(reservaId, null);
    }

    private Usuario exigirCuentaActiva(Long usuarioId) {
        Usuario usuario = usuarioId == null ? null : reservaRepository.findUsuarioConRol(usuarioId);
        if (usuario == null || !Boolean.TRUE.equals(usuario.getActivo())) {
            throw new AccesoDenegadoException("Esta operación requiere una cuenta activa.");
        }
        return usuario;
    }

    private void exigirAdministrador(Long usuarioId) {
        Usuario usuario = exigirCuentaActiva(usuarioId);
        if (usuario.getRol() == null || !"ADMIN".equals(usuario.getRol().getNombre())) {
            throw new AccesoDenegadoException("Esta operación requiere una cuenta administradora activa.");
        }
    }

    private List<ResumenReserva> resumirReservas(List<ReservaEstacionamiento> reservas, boolean mostrarPropietario) {
        LocalDateTime ahora = LocalDateTime.now(clock);
        return reservas.stream().map(reserva -> {
            boolean estadoCancelable = esEstadoCancelable(reserva);
            String estadoVisible = estadoCancelable && !reserva.getFechaHoraFin().isAfter(ahora)
                    ? "VENCIDA" : reserva.getEstado();
            Usuario propietario = mostrarPropietario ? reserva.getUsuario() : null;
            return new ResumenReserva(reserva.getId(), reserva.getEstacionamiento().getCodigo(),
                    reserva.getVehiculo().getPatente(), reserva.getFechaHoraInicio(), reserva.getFechaHoraFin(),
                    estadoVisible, estadoCancelable && ahora.isBefore(reserva.getFechaHoraInicio()),
                    propietario == null ? null : propietario.getNombre(),
                    propietario == null ? null : propietario.getCorreo());
        }).toList();
    }

    private void cancelar(Long reservaId, Long propietarioRequerido) {
        if (reservaId == null || reservaId <= 0) {
            throw new IllegalArgumentException("El identificador de la reserva no es válido.");
        }

        ReservaEstacionamiento referencia = reservaRepository.findById(reservaId);
        comprobarAccesoAReserva(referencia, propietarioRequerido);
        Long estacionamientoId = referencia.getEstacionamiento().getId();

        estacionamientoRepository.conEstacionamientoBloqueado(estacionamientoId, (em, estacionamiento) -> {
            if (estacionamiento == null) {
                throw new IllegalArgumentException(RESERVA_NO_DISPONIBLE);
            }
            ReservaEstacionamiento reserva = reservaRepository.findByIdBloqueada(em, reservaId);
            comprobarAccesoAReserva(reserva, propietarioRequerido);
            if (!estacionamientoId.equals(reserva.getEstacionamiento().getId())) {
                throw new IllegalArgumentException("La reserva cambió. Vuelve a consultar tus reservas.");
            }
            if (!esEstadoCancelable(reserva)) {
                throw new IllegalArgumentException("La reserva no se puede cancelar en su estado actual.");
            }
            if (!LocalDateTime.now(clock).isBefore(reserva.getFechaHoraInicio())) {
                throw new IllegalArgumentException("Solo puedes cancelar una reserva antes de que comience.");
            }
            reserva.setEstado("CANCELADA");
            return null;
        });
    }

    private void comprobarAccesoAReserva(ReservaEstacionamiento reserva, Long propietarioRequerido) {
        if (reserva == null || (propietarioRequerido != null
                && !propietarioRequerido.equals(reserva.getUsuario().getId()))) {
            throw new IllegalArgumentException(RESERVA_NO_DISPONIBLE);
        }
    }

    private boolean esEstadoCancelable(ReservaEstacionamiento reserva) {
        return "PENDIENTE".equals(reserva.getEstado()) || "APROBADA".equals(reserva.getEstado());
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

    public record ResumenReserva(Long id, String codigoEstacionamiento, String patente,
            LocalDateTime inicio, LocalDateTime fin, String estado, boolean puedeCancelar,
            String nombrePropietario, String correoPropietario) {
    }

    public static class AccesoDenegadoException extends RuntimeException {
        public AccesoDenegadoException(String mensaje) {
            super(mensaje);
        }
    }
}
