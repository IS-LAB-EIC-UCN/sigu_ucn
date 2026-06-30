package cl.ucn.app.repository;

import cl.ucn.app.model.Ticket;
import java.util.List;
import java.util.Optional;

/**
 * Contrato de acceso a datos para Ticket.
 * Depender de esta abstracción (D de SOLID) permite sustituir
 * la implementación JPA por cualquier otra sin tocar el servicio.
 */
public interface TicketRepository {

    Ticket save(Ticket ticket);

    Optional<Ticket> findById(Long id);

    List<Ticket> findAll();

    /** Filtra por estado, prioridad o técnico; cualquier parámetro puede ser null. */
    List<Ticket> findByFiltros(String estado, String prioridad, Long tecnicoId);

    void update(Ticket ticket);
}
