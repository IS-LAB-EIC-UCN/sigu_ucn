package cl.ucn.app.repository;

import cl.ucn.app.model.Proveedor;

import java.util.List;
import java.util.Optional;

/**
 * Acceso a los proveedores del laboratorio (Grupo 5).
 */
public interface ProveedorRepository {

    List<Proveedor> listar();

    Optional<Proveedor> buscarPorId(Long id);
}
