package cl.ucn.app.service;

import cl.ucn.app.model.CategoriaTicket;
import cl.ucn.app.model.Comentario;
import cl.ucn.app.model.Ticket;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.ICategoriaTicketRepository;
import cl.ucn.app.repository.IComentarioRepository;
import cl.ucn.app.repository.TicketRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

public class TicketServiceImpl implements Iticketservice {

    private static final List<String> ESTADOS_VALIDOS = List.of("ABIERTO", "EN_PROCESO", "CERRADO");
    private static final List<String> PRIORIDADES_VALIDAS = List.of("BAJA", "MEDIA", "ALTA");

    private final TicketRepository ticketRepository;
    private final IComentarioRepository comentarioRepository;
    private final ICategoriaTicketRepository categoriaTicketRepository;

    public TicketServiceImpl(TicketRepository ticketRepository,
                              IComentarioRepository comentarioRepository,
                              ICategoriaTicketRepository categoriaTicketRepository) {
        this.ticketRepository = ticketRepository;
        this.comentarioRepository = comentarioRepository;
        this.categoriaTicketRepository = categoriaTicketRepository;
    }

    @Override
    public Ticket crearTicket(String titulo, String descripcion, String prioridad,
                               Long categoriaId, Long solicitanteId) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título es obligatorio.");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripción es obligatoria.");
        }
        if (prioridad == null || !PRIORIDADES_VALIDAS.contains(prioridad)) {
            throw new IllegalArgumentException("Prioridad inválida. Valores permitidos: " + PRIORIDADES_VALIDAS);
        }
        if (categoriaId == null) {
            throw new IllegalArgumentException("Debe seleccionar una categoría.");
        }
        CategoriaTicket categoria = categoriaTicketRepository.buscarPorId(categoriaId)
                .orElseThrow(() -> new NoSuchElementException("Categoría no encontrada: " + categoriaId));
        if (solicitanteId == null) {
            throw new IllegalArgumentException("El solicitante es obligatorio.");
        }

        Ticket ticket = new Ticket();
        ticket.setTitulo(titulo);
        ticket.setDescripcion(descripcion);
        ticket.setPrioridad(prioridad);
        ticket.setEstado("ABIERTO");
        ticket.setFechaCreacion(LocalDateTime.now());
        ticket.setCategoria(categoria);

        Usuario solicitante = new Usuario();
        solicitante.setId(solicitanteId);
        ticket.setSolicitante(solicitante);

        ticketRepository.guardar(ticket);
        return ticket;
    }

    @Override
    public Ticket cambiarEstado(Long ticketId, String nuevoEstado, Long usuarioId) {
        Ticket ticket = obtenerTicket(ticketId);

        if ("CERRADO".equals(ticket.getEstado())) {
            throw new IllegalStateException("No se puede modificar un ticket cerrado.");
        }
        if (nuevoEstado == null || !ESTADOS_VALIDOS.contains(nuevoEstado)) {
            throw new IllegalArgumentException("Estado inválido. Valores permitidos: " + ESTADOS_VALIDOS);
        }
        if ("EN_PROCESO".equals(nuevoEstado)) {
            if (ticket.getTecnico() == null || !ticket.getTecnico().getId().equals(usuarioId)) {
                throw new IllegalStateException("Solo el técnico asignado puede marcar el ticket en proceso.");
            }
        }
        if ("CERRADO".equals(nuevoEstado)) {
            throw new IllegalStateException("Use cerrarTicket() para cerrar un ticket (requiere resolución).");
        }

        ticket.setEstado(nuevoEstado);
        ticketRepository.actualizar(ticket);
        return ticket;
    }

    @Override
    public Ticket asignarTecnico(Long ticketId, Long tecnicoId) {
        Ticket ticket = obtenerTicket(ticketId);
        if ("CERRADO".equals(ticket.getEstado())) {
            throw new IllegalStateException("No se puede asignar técnico a un ticket cerrado.");
        }
        if (tecnicoId == null) {
            throw new IllegalArgumentException("Debe indicar un técnico.");
        }
        Usuario tecnico = new Usuario();
        tecnico.setId(tecnicoId);
        ticket.setTecnico(tecnico);
        ticketRepository.actualizar(ticket);
        return ticket;
    }

    @Override
    public Ticket cerrarTicket(Long ticketId, String resolucion) {
        Ticket ticket = obtenerTicket(ticketId);
        if ("CERRADO".equals(ticket.getEstado())) {
            throw new IllegalStateException("El ticket ya se encuentra cerrado.");
        }
        if (resolucion == null || resolucion.isBlank()) {
            throw new IllegalArgumentException("No se puede cerrar un ticket sin resolución registrada.");
        }
        ticket.setResolucion(resolucion);
        ticket.setEstado("CERRADO");
        ticket.setFechaCierre(LocalDateTime.now());
        ticketRepository.actualizar(ticket);
        return ticket;
    }

    @Override
    public Comentario agregarComentario(Long ticketId, String contenido, Long autorId) {
        Ticket ticket = obtenerTicket(ticketId);
        if ("CERRADO".equals(ticket.getEstado())) {
            throw new IllegalStateException("No se pueden agregar comentarios a un ticket cerrado.");
        }
        if (contenido == null || contenido.isBlank()) {
            throw new IllegalArgumentException("El comentario no puede estar vacío.");
        }
        if (autorId == null) {
            throw new IllegalArgumentException("El autor es obligatorio.");
        }

        Comentario comentario = new Comentario();
        comentario.setContenido(contenido);
        comentario.setFecha(LocalDateTime.now());
        comentario.setTicket(ticket);
        Usuario autor = new Usuario();
        autor.setId(autorId);
        comentario.setAutor(autor);

        comentarioRepository.guardar(comentario);
        return comentario;
    }

    @Override
    public Ticket obtenerTicket(Long ticketId) {
        if (ticketId == null) {
            throw new IllegalArgumentException("El id del ticket es obligatorio.");
        }
        return ticketRepository.buscarPorId(ticketId)
                .orElseThrow(() -> new NoSuchElementException("Ticket no encontrado: " + ticketId));
    }

    @Override
    public List<Ticket> listarTickets() {
        return ticketRepository.listarTodos();
    }

    @Override
    public List<Ticket> filtrarTickets(String estado, String prioridad, Long tecnicoId) {
        return ticketRepository.filtrar(estado, prioridad, tecnicoId);
    }

    @Override
    public List<Comentario> obtenerComentarios(Long ticketId) {
        return comentarioRepository.listarPorTicket(ticketId);
    }
}