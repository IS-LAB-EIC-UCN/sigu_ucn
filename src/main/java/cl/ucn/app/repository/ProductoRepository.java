package cl.ucn.app.repository;

import cl.ucn.app.model.Producto;

/**
 * Acceso a los productos del inventario: equipos e insumos (Grupo 5).
 */
public interface ProductoRepository {

    boolean existeCodigo(String codigo);

    void guardar(Producto producto);
}