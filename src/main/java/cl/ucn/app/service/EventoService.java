package cl.ucn.app.service;

import cl.ucn.app.model.Evento;
import cl.ucn.app.model.Expositor;
import cl.ucn.app.model.Inscripcion;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.EventoRepository;
import cl.ucn.app.repository.InscripcionRepository;
import cl.ucn.app.repository.UsuarioRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventoService {

    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InscripcionRepository inscripcionRepository;

    public EventoService() {
        this.eventoRepository = new EventoRepository();
        this.usuarioRepository = new UsuarioRepository();
        this.inscripcionRepository = new InscripcionRepository();
    }
    // Constructor para los tests
    public EventoService(EventoRepository eventoRepository,
                         UsuarioRepository usuarioRepository,
                         InscripcionRepository inscripcionRepository) {
        this.eventoRepository = eventoRepository;
        this.usuarioRepository = usuarioRepository;
        this.inscripcionRepository = inscripcionRepository;
    }

    public Evento registrar(Evento evento) {

        if (evento.getTitulo() == null || evento.getTitulo().isBlank()) {
            throw new IllegalArgumentException("El título del evento es obligatorio.");
        }
        if (evento.getFecha() == null) {
            throw new IllegalArgumentException("La fecha del evento es obligatoria.");
        }
        if (evento.getHoraInicio() == null || evento.getHoraFin() == null) {
            throw new IllegalArgumentException("Las horas de inicio y fin son obligatorias.");
        }
        if (!evento.getHoraInicio().isBefore(evento.getHoraFin())) {
            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin.");
        }
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        if (evento.getFecha().isBefore(today)) {
            throw new IllegalArgumentException("No se puede registrar un evento en una fecha pasada.");
        }
        if (evento.getFecha().isEqual(today) && evento.getHoraInicio().isBefore(now)) {
            throw new IllegalArgumentException("No se puede registrar un evento con una hora de inicio ya pasada.");
        }

        if (evento.getCapacidad() == null || evento.getCapacidad() <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser un número positivo.");
        }
        if (evento.getEspacio() == null || evento.getEspacio().getId() == null) {
            throw new IllegalArgumentException("Debe seleccionar un espacio o ubicación.");
        }

        if (evento.getCapacidad() > evento.getEspacio().getCapacidad()) {
            throw new IllegalArgumentException(
                    "La capacidad del evento (" + evento.getCapacidad() +
                    ") no puede superar la capacidad del espacio (" +
                    evento.getEspacio().getCapacidad() + ").");
        }

        boolean conflicto = eventoRepository.existeConflictoHorario(
                evento.getEspacio().getId(),
                evento.getFecha(),
                evento.getHoraInicio(),
                evento.getHoraFin(),
                null
        );

        if (conflicto) {
            throw new IllegalArgumentException(
                    "Ya existe un evento programado en el mismo espacio y horario.");
        }

        if (evento.getExpositores() != null && !evento.getExpositores().isEmpty()) {
            Set<Long> expositorIds = new HashSet<>();
            for (Expositor ex : evento.getExpositores()) {
                if (ex.getId() == null) {
                    throw new IllegalArgumentException("Expositor inválido.");
                }
                if (!expositorIds.add(ex.getId())) {
                    throw new IllegalArgumentException(
                            "El expositor \"" + ex.getNombre() + "\" no puede repetirse en el mismo evento.");
                }
                boolean conflictoExpositor = eventoRepository.expositorTieneConflictoHorario(
                        ex.getId(),
                        evento.getFecha(),
                        evento.getHoraInicio(),
                        evento.getHoraFin(),
                        null
                );
                if (conflictoExpositor) {
                    throw new IllegalArgumentException(
                            "El expositor \"" + ex.getNombre() + "\" ya se encuentra asignado a otro evento en la misma fecha y horario.");
                }
            }
        }

        if (evento.getEstado() == null || evento.getEstado().isBlank()) {
            evento.setEstado("PLANIFICADO");
        }

        return eventoRepository.save(evento);
    }

    public Evento buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del evento es obligatorio.");
        }
        Evento evento = eventoRepository.findById(id);
        if (evento == null) {
            throw new IllegalArgumentException("Evento no encontrado con ID: " + id);
        }
        return evento;
    }

    public List<Evento> listarTodos() {
        return eventoRepository.findAll();
    }

    public List<Evento> listarConFiltros(LocalDate fecha, String tematica) {
        return eventoRepository.findByFilters(fecha, tematica);
    }

    public List<Evento> listarPorFecha(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria para filtrar.");
        }
        return eventoRepository.findByFecha(fecha);
    }

    public List<Evento> listarPorTematica(String tematica) {
        if (tematica == null || tematica.isBlank()) {
            throw new IllegalArgumentException("La temática es obligatoria para filtrar.");
        }
        return eventoRepository.findByTematica(tematica);
    }

    public List<Inscripcion> listarAsistentes(Long eventoId) {
        Evento evento = buscarPorId(eventoId);
        return inscripcionRepository.findByEventoId(evento.getId());
    }

    public Evento cancelar(Long id) {
        Evento evento = buscarPorId(id);
        if ("CANCELADO".equals(evento.getEstado())) {
            throw new IllegalArgumentException("El evento ya se encuentra cancelado.");
        }
        evento.setEstado("CANCELADO");
        evento.getInscripciones().clear(); // Invalida todas las inscripciones asociadas
        return eventoRepository.save(evento);
    }

    public void eliminar(Long id) {
        Evento evento = buscarPorId(id);
        if (!"CANCELADO".equals(evento.getEstado())) {
            throw new IllegalArgumentException("Solo se pueden eliminar eventos cancelados.");
        }
        eventoRepository.delete(evento);
    }

    public void inscribirAsistente(Long usuarioId, Long eventoId) {
        if (usuarioId == null || eventoId == null) {
            throw new IllegalArgumentException("El ID de usuario y de evento son obligatorios.");
        }

        Evento evento = buscarPorId(eventoId);
        if ("CANCELADO".equals(evento.getEstado())) {
            throw new IllegalArgumentException("No es posible inscribirse en un evento cancelado.");
        }

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        if (evento.getFecha().isBefore(today)) {
            throw new IllegalArgumentException("No es posible inscribirse en un evento que ya ha finalizado.");
        }
        if (evento.getFecha().isEqual(today) && evento.getHoraInicio().isBefore(now)) {
            throw new IllegalArgumentException("No es posible inscribirse en un evento que ya ha comenzado.");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId);
        }

        Inscripcion existente = inscripcionRepository.findByUsuarioAndEvento(usuarioId, eventoId);
        if (existente != null) {
            throw new IllegalArgumentException("Ya estás inscrito en este evento.");
        }

        int cuposDisponibles = evento.getCapacidad() - evento.getInscripciones().size();
        if (cuposDisponibles <= 0) {
            throw new IllegalArgumentException("El evento se encuentra sin cupos disponibles.");
        }

        Inscripcion inscripcion = new Inscripcion(usuario, evento);
        inscripcionRepository.save(inscripcion);
    }

    public void cancelarInscripcion(Long usuarioId, Long eventoId) {
        if (usuarioId == null || eventoId == null) {
            throw new IllegalArgumentException("El ID de usuario y de evento son obligatorios.");
        }

        Inscripcion inscripcion = inscripcionRepository.findByUsuarioAndEvento(usuarioId, eventoId);
        if (inscripcion == null) {
            throw new IllegalArgumentException("No se encontró una inscripción activa para este evento.");
        }

        inscripcionRepository.delete(inscripcion);
    }
}
