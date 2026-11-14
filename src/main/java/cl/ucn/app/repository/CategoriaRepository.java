package cl.ucn.app.repository;

import cl.ucn.app.model.Categoria;

import java.util.List;
import java.util.Optional;

/**
 * Acceso a las categorías del inventario (Grupo 5).
 */
public interface CategoriaRepository {

    List<Categoria> listar();

    Optional<Categoria> buscarPorId(Long id);
}
