package cl.ucn.app.service;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Comentario;
import cl.ucn.app.model.Ticket;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.ICategoriaTicketRepository;
import cl.ucn.app.repository.IComentarioRepository;
import cl.ucn.app.repository.ITicketRepository;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Lógica de negocio del módulo de soporte.
 *
 * SOLID:
 *  S – solo orquesta reglas de tickets, sin HTTP ni SQL directo.
 *  O – extender con notificaciones sin modificar esta clase.
 *  D – recibe interfaces por constructor, no clases concretas.
 *
 * Estados válidos (según CHECK en BD): ABIERTO | EN_PROCESO | CERRADO
 * Prioridades válidas:                 BAJA | MEDIA | ALTA
 */
public class TicketServiceImpl implements ITicketService {

    private final ITicketRepository       ticketRepo;
    private final IComentarioRepository   comentarioRepo;
    private final ICategoriaTicketRepository categoriaRepo;

    public TicketServiceImpl(ITicketRepository ticketRepo,
                             IComentarioRepository comentarioRepo,
                             ICategoriaTicketRepository categoriaRepo) {
        this.ticketRepo    = ticketRepo;
        this.comentarioRepo = comentarioRepo;
        this.categoriaRepo  = categoriaRepo;
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    protected Usuario buscarUsuario(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Usuario u = em.find(Usuario.class, id);
            if (u == null) throw new IllegalArgumentException("Usuario no encontrado: " + id);
            return u;
        } finally {
            em.close();
        }
    }

    private Ticket ticketExistente(Long id) {
        return ticketRepo.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado: " + id));
    }

    private void verificarAbierto(Ticket ticket) {
        if ("CERRADO".equals(ticket.getEstado())) {
            throw new IllegalStateException("El ticket está cerrado y no puede modificarse.");
        }
    }

    // ── Implementación ────────────────────────────────────────────────────────

    @Override
    public Ticket crearTicket(String titulo, String descripcion, String prioridad,
                              Long categoriaId, Long solicitanteId) {
        if (titulo == null || titulo.isBlank())
            throw new IllegalArgumentException("El título no puede estar vacío.");
        if (descripcion == null || descripcion.isBlank())
            throw new IllegalArgumentException("La descripción no puede estar vacía.");

        String prio = (prioridad != null && !prioridad.isBlank()) ? prioridad.toUpperCase() : "MEDIA";
        if (!List.of("BAJA", "MEDIA", "ALTA").contains(prio))
            throw new IllegalArgumentException("Prioridad inválida: " + prio);

        Ticket ticket = new Ticket();
        ticket.setTitulo(titulo.trim());
        ticket.setDescripcion(descripcion.trim());
        ticket.setPrioridad(prio);
        ticket.setEstado("ABIERTO");
        ticket.setFechaCreacion(LocalDateTime.now());
        ticket.setSolicitante(buscarUsuario(solicitanteId));
        categoriaRepo.buscarPorId(categoriaId).ifPresent(ticket::setCategoria);

        ticketRepo.guardar(ticket);
        return ticket;
    }

    @Override
    public Ticket cambiarEstado(Long ticketId, String nuevoEstado, Long usuarioId) {
        Ticket ticket = ticketExistente(ticketId);
        verificarAbierto(ticket);

        String estado = nuevoEstado.toUpperCase();
        if (!List.of("ABIERTO", "EN_PROCESO", "CERRADO").contains(estado))
            throw new IllegalArgumentException("Estado inválido: " + estado);

        // Regla: solo el técnico asignado puede marcar EN_PROCESO
        if ("EN_PROCESO".equals(estado)) {
            if (ticket.getTecnico() == null || !ticket.getTecnico().getId().equals(usuarioId))
                throw new IllegalStateException(
                        "Solo el técnico asignado puede marcar el ticket como EN_PROCESO.");
        }

        ticket.setEstado(estado);
        ticketRepo.actualizar(ticket);
        return ticket;
    }

    @Override
    public Ticket asignarTecnico(Long ticketId, Long tecnicoId) {
        Ticket ticket = ticketExistente(ticketId);
        verificarAbierto(ticket);
        ticket.setTecnico(buscarUsuario(tecnicoId));
        ticketRepo.actualizar(ticket);
        return ticket;
    }

    @Override
    public Ticket cerrarTicket(Long ticketId, String resolucion) {
        Ticket ticket = ticketExistente(ticketId);
        verificarAbierto(ticket);

        if (resolucion == null || resolucion.isBlank())
            throw new IllegalArgumentException("No se puede cerrar un ticket sin resolución registrada.");

        ticket.setResolucion(resolucion.trim());
        ticket.setEstado("CERRADO");
        ticket.setFechaCierre(LocalDateTime.now());
        ticketRepo.actualizar(ticket);
        return ticket;
    }

    @Override
    public Comentario agregarComentario(Long ticketId, String contenido, Long autorId) {
        Ticket ticket = ticketExistente(ticketId);
        verificarAbierto(ticket);

        if (contenido == null || contenido.isBlank())
            throw new IllegalArgumentException("El comentario no puede estar vacío.");

        Comentario comentario = new Comentario();
        comentario.setContenido(contenido.trim());
        comentario.setFecha(LocalDateTime.now());
        comentario.setTicket(ticket);
        comentario.setAutor(buscarUsuario(autorId));

        comentarioRepo.guardar(comentario);
        return comentario;
    }

    @Override
    public Ticket obtenerTicket(Long ticketId) {
        return ticketExistente(ticketId);
    }

    @Override
    public List<Ticket> listarTickets() {
        return ticketRepo.listarTodos();
    }

    @Override
    public List<Ticket> filtrarTickets(String estado, String prioridad, Long tecnicoId) {
        return ticketRepo.filtrar(estado, prioridad, tecnicoId);
    }

    @Override
    public List<Comentario> obtenerComentarios(Long ticketId) {
        ticketExistente(ticketId);
        return comentarioRepo.listarPorTicket(ticketId);
    }
}
