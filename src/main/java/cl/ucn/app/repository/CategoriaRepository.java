package cl.ucn.app.repository;

import cl.ucn.app.model.Categoria;
import java.util.List;
import java.util.Optional;

public interface CategoriaRepository {
    Optional<Categoria> findById(Long id);
    Optional<Categoria> findByNombre(String nombre);
    List<Categoria> findAll();
    Categoria save(Categoria categoria);
}
