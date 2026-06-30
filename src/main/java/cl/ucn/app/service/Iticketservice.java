package cl.ucn.app.service;

import cl.ucn.app.model.Ticket;
import cl.ucn.app.repository.TicketRepository;

import java.util.List;

public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService() {
        this.ticketRepository = new TicketRepository();
    }

    public void crearTicket(Ticket ticket) {

    if (ticket.getTitulo() == null || ticket.getTitulo().isBlank()) {
        throw new IllegalArgumentException("El titulo es obligatorio");
    }

    if (ticket.getDescripcion() == null || ticket.getDescripcion().isBlank()) {
        throw new IllegalArgumentException("La descripcion es obligatoria");
    }

    if (ticket.getPrioridad() == null || ticket.getPrioridad().isBlank()) {
        ticket.setPrioridad("MEDIA");
    }

    if (ticket.getEstado() == null || ticket.getEstado().isBlank()) {
        ticket.setEstado("ABIERTO");
    }

    ticketRepository.guardar(ticket);
}

    public Ticket buscarPorId(Long id) {
        return ticketRepository.buscarPorId(id);
    }

    public List<Ticket> listarTodos() {
        return ticketRepository.listarTodos();
    }

    public void cambiarEstado(Long id, String nuevoEstado) {

    Ticket ticket = ticketRepository.buscarPorId(id);

    if (ticket == null) {
        throw new IllegalArgumentException("Ticket no encontrado");
    }

    if (!nuevoEstado.equals("ABIERTO")
            && !nuevoEstado.equals("EN_PROCESO")
            && !nuevoEstado.equals("CERRADO")) {

        throw new IllegalArgumentException("Estado invalido");
    }

    if (nuevoEstado.equals("CERRADO")
            && (ticket.getResolucion() == null
            || ticket.getResolucion().isBlank())) {

        throw new IllegalArgumentException(
                "No se puede cerrar un ticket sin resolucion");
    }

    ticket.setEstado(nuevoEstado);

    ticketRepository.actualizar(ticket);
}
    
    public void eliminarTicket(Long id) {
        ticketRepository.eliminar(id);
    }
}