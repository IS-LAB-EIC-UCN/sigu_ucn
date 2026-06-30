package cl.ucn.app.service;

import cl.ucn.app.model.Espacio;
import cl.ucn.app.model.Reserva;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.EspacioRepository;
import cl.ucn.app.repository.ReservaRepository;
import cl.ucn.app.repository.UsuarioRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ReservaService {
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EspacioRepository espacioRepository;

    public ReservaService(ReservaRepository reservaRepository, UsuarioRepository usuarioRepository, EspacioRepository espacioRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.espacioRepository = espacioRepository;
    }

    public Reserva crearReserva(Long usuarioId, Long espacioId, LocalDateTime inicio, LocalDateTime fin) {
        if (inicio.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No se pueden crear reservas en el pasado.");
        }
        if (inicio.isAfter(fin)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin.");
        }
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        Espacio espacio = espacioRepository.findById(espacioId)
            .orElseThrow(() -> new IllegalArgumentException("Espacio no encontrado."));

        LocalDate fechaReserva = inicio.toLocalDate();
        LocalTime horaInicio = inicio.toLocalTime();
        LocalTime horaFin = fin.toLocalTime();

        if (reservaRepository.hasOverlap(espacioId, fechaReserva, horaInicio, horaFin)) {
            throw new IllegalStateException("El espacio ya está reservado en ese horario.");
        }

        Reserva reserva = new Reserva(
            fechaReserva,
            horaInicio,
            horaFin,
            "PENDIENTE",
            usuario,
            espacio
        );
        reservaRepository.save(reserva);
        return reserva;
    }

    public Reserva actualizarEstado(Long reservaId, String nuevoEstado) {
        Reserva reserva = reservaRepository.findById(reservaId)
            .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada."));
        reserva.setEstado(nuevoEstado.toUpperCase());
        reservaRepository.save(reserva);
        return reserva;
    }

    public List<Reserva> buscarConFiltros(Long usuarioId, Long espacioId, String estado, LocalDateTime desde, LocalDateTime hasta) {
        return reservaRepository.findWithFilters(usuarioId, espacioId, estado, desde, hasta);
    }

    public List<Reserva> obtenerTodas() {
        return reservaRepository.findAll();
    }

    public List<Reserva> obtenerPorUsuario(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId);
    }
}
