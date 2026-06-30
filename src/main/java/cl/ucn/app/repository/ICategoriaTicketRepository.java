package cl.ucn.app.repository;

import cl.ucn.app.model.CategoriaTicket;
import java.util.List;
import java.util.Optional;

public interface ICategoriaTicketRepository {

    List<CategoriaTicket> listarTodas();

    Optional<CategoriaTicket> buscarPorId(Long id);
}
