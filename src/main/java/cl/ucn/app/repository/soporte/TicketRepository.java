package cl.ucn.app.repository.soporte;

import cl.ucn.app.model.soporte.Ticket;

import java.util.List;

public interface TicketRepository {
    Ticket guardar(Ticket ticket);

    List<Ticket> listarPendientes();

    Ticket asignarTecnico(Long ticketId, Long tecnicoId, Long administradorId);

    List<Ticket> listarAsignados(Long tecnicoId);

    Ticket iniciarResolucion(Long ticketId, Long tecnicoId);

    List<Ticket> listarEnResolucion(Long tecnicoId);

    Ticket cerrarTicket(Long ticketId, Long tecnicoId, String resolucion);
}
