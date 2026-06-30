package cl.ucn.app.repository;

import cl.ucn.app.model.Comentario;
import java.util.List;

public interface IComentarioRepository {

    void guardar(Comentario comentario);

    List<Comentario> listarPorTicket(Long ticketId);
}
