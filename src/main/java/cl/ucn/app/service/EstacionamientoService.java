package cl.ucn.app.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

import cl.ucn.app.model.Estacionamiento;
import cl.ucn.app.model.TipoEstacionamiento;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.EstacionamientoRepository;
import cl.ucn.app.repository.ReservaEstacionamientoRepository;

public class EstacionamientoService {

    private static final Pattern ESPACIOS_EXTERIORES = Pattern.compile("^\\s+|\\s+$", Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern CODIGO_VALIDO = Pattern.compile("[A-Za-z0-9]{2,10}");

    private final EstacionamientoRepository estacionamientoRepository;
    private final ReservaEstacionamientoRepository reservaRepository;
    private final Clock clock;

    public EstacionamientoService() {
        this(new EstacionamientoRepository(), new ReservaEstacionamientoRepository(),
                Clock.system(ZoneId.of("America/Santiago")));
    }

    public EstacionamientoService(EstacionamientoRepository estacionamientoRepository,
            ReservaEstacionamientoRepository reservaRepository, Clock clock) {
        this.estacionamientoRepository = Objects.requireNonNull(estacionamientoRepository);
        this.reservaRepository = Objects.requireNonNull(reservaRepository);
        this.clock = Objects.requireNonNull(clock);
    }

    public List<Estacionamiento> listar(Long usuarioId) {
        exigirAdministrador(usuarioId);
        return estacionamientoRepository.findAll();
    }

    public Estacionamiento registrar(Long usuarioId, String codigo, String tipo, String estadoInicial) {
        exigirAdministrador(usuarioId);
        String codigoLimpio = codigo == null ? "" : ESPACIOS_EXTERIORES.matcher(codigo).replaceAll("");
        if (!CODIGO_VALIDO.matcher(codigoLimpio).matches()) {
            throw new IllegalArgumentException("El código debe contener entre 2 y 10 letras de la A a la Z o números.");
        }

        String codigoNormalizado = codigoLimpio.toUpperCase(Locale.ROOT);
        TipoEstacionamiento tipoEstacionamiento = switch (tipo == null ? "" : tipo) {
            case "COMUN" -> TipoEstacionamiento.COMUN;
            case "DOCENTES" -> TipoEstacionamiento.DOCENTES;
            default -> throw new IllegalArgumentException("Selecciona el tipo Común o Docentes.");
        };
        boolean habilitado = leerHabilitacion(estadoInicial);

        if (estacionamientoRepository.existsByCodigo(codigoNormalizado)) {
            throw new IllegalArgumentException("Ya existe un estacionamiento con ese código.");
        }
        Estacionamiento estacionamiento = new Estacionamiento(codigoNormalizado, habilitado, tipoEstacionamiento);
        estacionamientoRepository.save(estacionamiento);
        return estacionamiento;
    }

    public Estacionamiento cambiarHabilitacion(Long usuarioId, Long estacionamientoId, String estado) {
        exigirAdministrador(usuarioId);
        if (estacionamientoId == null || estacionamientoId <= 0) {
            throw new IllegalArgumentException("El identificador del estacionamiento no es válido.");
        }
        boolean habilitado = leerHabilitacion(estado);

        return estacionamientoRepository.conEstacionamientoBloqueado(estacionamientoId, (em, estacionamiento) -> {
            if (estacionamiento == null) {
                throw new IllegalArgumentException("El estacionamiento no existe.");
            }
            if (!habilitado && Boolean.TRUE.equals(estacionamiento.getHabilitado())) {
                // Truncar evita que JDBC redondee el corte hacia el siguiente microsegundo.
                LocalDateTime ahora = LocalDateTime.now(clock).truncatedTo(ChronoUnit.MICROS);
                if (reservaRepository.existenReservasVigentes(em, estacionamientoId, ahora)) {
                    throw new IllegalArgumentException(
                            "El estacionamiento tiene reservas pendientes o aprobadas que aún no terminan.");
                }
                // HU-06: comprobar la ocupación física antes de deshabilitar el espacio.
            }
            estacionamiento.setHabilitado(habilitado);
            return estacionamiento;
        });
    }

    private void exigirAdministrador(Long usuarioId) {
        Usuario usuario = usuarioId == null ? null : reservaRepository.findUsuarioConRol(usuarioId);
        if (usuario == null || !Boolean.TRUE.equals(usuario.getActivo()) || usuario.getRol() == null
                || !"ADMIN".equals(usuario.getRol().getNombre())) {
            throw new AccesoDenegadoException();
        }
    }

    private boolean leerHabilitacion(String estado) {
        return switch (estado == null ? "" : estado) {
            case "true" -> true;
            case "false" -> false;
            default -> throw new IllegalArgumentException("Selecciona un estado de habilitación válido.");
        };
    }

    public static class AccesoDenegadoException extends RuntimeException {
        public AccesoDenegadoException() {
            super("Esta operación requiere una cuenta administradora activa.");
        }
    }
}
