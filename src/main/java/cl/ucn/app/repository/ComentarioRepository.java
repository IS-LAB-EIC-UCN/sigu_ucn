package cl.ucn.app.repository;

import cl.ucn.app.model.Comentario;
import java.util.List;

public interface ComentarioRepository {

    Comentario save(Comentario comentario);

    List<Comentario> findByTicketId(Long ticketId);
}
