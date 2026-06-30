package cl.ucn.app.repository;

import cl.ucn.app.model.CategoriaTicket;
import java.util.List;
import java.util.Optional;

public interface CategoriaTicketRepository {

    List<CategoriaTicket> findAll();

    Optional<CategoriaTicket> findById(Long id);
}
