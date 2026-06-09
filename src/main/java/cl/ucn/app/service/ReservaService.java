package cl.ucn.app.service;

import cl.ucn.app.model.Espacio;
import cl.ucn.app.model.Reserva;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.EspacioRepository;
import cl.ucn.app.repository.ReservaRepository;
import cl.ucn.app.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ReservaService {
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EspacioRepository espacioRepository;

    public ReservaService(ReservaRepository reservaRepository, UsuarioRepository usuarioRepository, EspacioRepository espacioRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.espacioRepository = espacioRepository;
    }

    public Reserva crearReserva(String usuarioId, String espacioId, LocalDateTime inicio, LocalDateTime fin) {
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

        if (reservaRepository.hasOverlap(espacioId, inicio, fin)) {
            throw new IllegalStateException("El espacio ya está reservado en ese horario.");
        }

        Reserva reserva = new Reserva(
            UUID.randomUUID().toString(),
            usuario,
            espacio,
            inicio,
            fin,
            Reserva.Estado.PENDIENTE
        );
        reservaRepository.save(reserva);
        return reserva;
    }

    public Reserva actualizarEstado(String reservaId, Reserva.Estado nuevoEstado) {
        Reserva reserva = reservaRepository.findById(reservaId)
            .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada."));
        reserva.setEstado(nuevoEstado);
        reservaRepository.save(reserva);
        return reserva;
    }

    public List<Reserva> buscarConFiltros(String usuarioId, String espacioId, String estado, LocalDateTime desde, LocalDateTime hasta) {
        return reservaRepository.findWithFilters(usuarioId, espacioId, estado, desde, hasta);
    }

    public List<Reserva> obtenerTodas() {
        return reservaRepository.findAll();
    }

    public List<Reserva> obtenerPorUsuario(String usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId);
    }
}
