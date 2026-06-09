package cl.ucn.app.repository;

import cl.ucn.app.model.Taller;
import java.util.List;

public interface ITallerRepository {
    void save(Taller taller);
    Taller findById(Long id);
    List<Taller> findAll();
}
