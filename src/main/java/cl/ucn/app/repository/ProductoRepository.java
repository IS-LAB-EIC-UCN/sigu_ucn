package cl.ucn.app.repository;
import cl.ucn.app.model.Insumo;
import cl.ucn.app.model.Producto;

import java.util.List;

/**
 * Acceso a los productos del inventario: equipos e insumos (Grupo 5).
 */
public interface ProductoRepository {

    /**
     * Productos activos, filtrados opcionalmente por categoría y por texto
     * (coincidencia parcial en nombre y código). Un filtro null se ignora.
     */
    List<Producto> buscar(Long categoriaId, String texto);

    boolean existeCodigo(String codigo);

    void guardar(Producto producto);

    List<Insumo> listarInsumos();
}