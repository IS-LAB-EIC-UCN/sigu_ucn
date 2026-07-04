package cl.ucn.app.repository;

import cl.ucn.app.model.Ticket;
import java.util.List;
import java.util.Optional;


public interface TicketRepository {

    void guardar(Ticket ticket);

    Optional<Ticket> buscarPorId(Long id);

    List<Ticket> listarTodos();

    /** Cualquier parámetro puede ser null → se omite ese filtro. */
    List<Ticket> filtrar(String estado, String prioridad, Long tecnicoId);

    void actualizar(Ticket ticket);
}