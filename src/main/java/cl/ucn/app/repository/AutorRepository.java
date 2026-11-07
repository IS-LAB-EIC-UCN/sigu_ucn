package cl.ucn.app.repository;

import cl.ucn.app.model.Autor;
import java.util.List;
import java.util.Optional;

public interface AutorRepository {
    Optional<Autor> findById(Long id);
    List<Autor> findAll();
    Autor save(Autor autor);
}
