package cl.ucn.app.service.soporte;

import cl.ucn.app.model.Usuario;
import cl.ucn.app.model.soporte.Ticket;
import cl.ucn.app.repository.UsuarioRepository;
import cl.ucn.app.repository.soporte.TicketRepository;

import java.util.Objects;

public class TicketService {
    private final TicketRepository ticketRepository;
    private final UsuarioRepository usuarioRepository;

    public TicketService() {
        this(new TicketRepository(), new UsuarioRepository());
    }

    public TicketService(TicketRepository ticketRepository, UsuarioRepository usuarioRepository) {
        this.ticketRepository = Objects.requireNonNull(ticketRepository);
        this.usuarioRepository = Objects.requireNonNull(usuarioRepository);
    }

    public Ticket crearTicket(String titulo, String descripcion, Long usuarioId) {
        validarDatos(titulo, descripcion);

        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("Usuario no válido");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId);

        if(usuario == null || !Boolean.TRUE.equals(usuario.getActivo())) {
            throw new IllegalArgumentException("El usuario no existe o se encuentra inactivo.");
        }

        Ticket ticket = new Ticket(titulo.trim(), descripcion.trim(), usuario);
        return ticketRepository.guardar(ticket);
    }

    private void validarDatos(String titulo, String descripcion) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título del ticket es obligatorio.");
        }

        if (titulo.trim().length() > 150) {
            throw new IllegalArgumentException("El título no puede superar los 150 caracteres.");
        }

        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripción del ticket es obligatoria.");
        }
    }
}
