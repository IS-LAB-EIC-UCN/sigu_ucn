package cl.ucn.app.repository;
import cl.ucn.app.model.Evento;
import java.util.List;
public interface EventoStore {
    Evento findById(Long id);
    List<Evento> findAll();
    void save(Evento evento);
    void update(Evento evento);
    void reactivar(Long id);
}
